package com.github.waterpeak.onepage;

import android.content.Intent;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OnePageManager implements Iterable<OnePage>, IOnePage, LifecycleObserver {

    private int size = 0;
    private Node first = null;
    private final IOnePageHost host;

    private boolean currentLifeStatusResume = false;
    private boolean currentLifeStatusStart = false;

    private boolean isDestroyed = false;

    public OnePageManager(IOnePageHost host) {
        this.host = host;
    }

    @Override
    public OnePageManager getPageManager() {
        return this;
    }


    @Override
    public void navigate(@NonNull OnePage page) {
        if (isDestroyed) return;
        final OnePage top = peek();
        if (currentLifeStatusResume && top != null) {
            top.onPause();
        }
        push(page);
        page.createInternal(host);
        if (currentLifeStatusStart) {
            page.onStart();
        }
        host.getContainer().addView(page.mContentView);
        if (currentLifeStatusResume) {
            page.onResume();
        }
        if (!page.doNotRemoveLastView() && top != null) {
            host.getContainer().removeView(top.mContentView);
        }
        if (currentLifeStatusStart && top != null) {
            top.onStop();
        }
    }

    public void replace(@NonNull OnePage page) {
        if (isDestroyed) return;
        final OnePage top = pop();
        if (top == page) {
            return;
        }
        if (currentLifeStatusResume && top != null) {
            top.onPause();
        }
        push(page);
        page.createInternal(host);
        if (currentLifeStatusStart) {
            page.onStart();
        }
        host.getContainer().addView(page.mContentView);
        if (currentLifeStatusResume) {
            page.onResume();
        }
        if (top != null) {
            host.getContainer().removeView(top.mContentView);
        }
        if (currentLifeStatusStart && top != null) {
            top.onStop();
        }
    }

    public void pageResume(OnePage page, ViewGroup container) {
        if (isDestroyed) return;
        page.createInternal(host);
        if (currentLifeStatusStart) {
            page.onStart();
        }
        if (container != null) {
            container.addView(page.mContentView);
            if (currentLifeStatusResume) {
                page.onResume();
            }
        }
    }

    public void pageStop(OnePage page, ViewGroup container) {
        if (isDestroyed) return;
        if (currentLifeStatusResume) {
            page.onPause();
        }
        if (container != null) {
            container.removeView(page.mContentView);
            if (currentLifeStatusStart) {
                page.onStop();
            }
        }
    }

    public boolean onBackPressed() {
        if (isDestroyed) return false;
        if (!isEmpty()) {
            final OnePage top = peek();
            if (top != null) {
                top.onBackPressed();
                return true;
            }

        }
        return false;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (isDestroyed) return;
        final OnePage top = peek();
        if (top != null) {
            top.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (isDestroyed) return;
        final OnePage top = peek();
        if (top != null) {
            top.onActivityResult(requestCode, resultCode, data);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        currentLifeStatusStart = false;
        final OnePage page = peek();
        if (page != null) {
            page.onStart();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        currentLifeStatusStart = true;
        final OnePage page = peek();
        if (page != null) {
            page.onStop();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        currentLifeStatusResume = true;
        final OnePage page = peek();
        if (page != null) {
            page.onResume();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        currentLifeStatusResume = false;
        final OnePage page = peek();
        if (page != null) {
            page.onPause();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        isDestroyed = true;
        if (!isEmpty()) {
            host.getContainer().removeAllViews();
            Node node = first;
            while (node != null) {
                node.page.destroyInternal();
                node = node.next;
            }
        }
    }

    public void handlePageFinish(final OnePage from) {
        if (isDestroyed) return;
        if (size() < 2) {
            host.doAfterLastPageFinished();
            return;
        }
        final OnePage top = peek();
        if (from != top) {
            remove(from);
            return;
        }
        pop();
        final OnePage afterTop = peek();
        if (currentLifeStatusResume && top != null) {
            top.onPause();
        }
        if (currentLifeStatusStart && afterTop != null) {
            afterTop.onStart();
        }
        if (afterTop != null) {
            host.getContainer().addView(afterTop.mContentView);
        }
        if (currentLifeStatusResume && afterTop != null) {
            afterTop.onResume();
        }
        if (top != null && afterTop != null) {
            afterTop.onUnwindFromPage(top);
        }
        if (top != null) {
            host.getContainer().removeView(top.mContentView);
        }
        if (currentLifeStatusStart && top != null) {
            top.onStop();
        }
        if (top != null) {
            top.destroyInternal();
        }
    }


    private void push(@NonNull OnePage page) {
        first = new Node(page, first);
        size++;
    }

    @Nullable
    private OnePage pop() {
        final Node node = this.first;
        if (node != null) {
            this.first = node.next;
            size--;
            return node.page;
        }
        return null;
    }

    @Nullable
    private OnePage peek() {
        final Node node = this.first;
        if (node != null) {
            return node.page;
        }
        return null;
    }

    private int size() {
        return size;
    }

    public boolean isEmpty() {
        return first == null;
    }

    public void removePage(@NonNull OnePagePredicate predicate) {
        final Node head = first;
        if (head != null) {
            Node pre = first;
            Node node = first.next;
            while (node != null) {
                if (predicate.predicate(node.page)) {
                    pre.next = node.next;
                    node.page.destroyInternal();
                    size--;
                }
                node = node.next;
            }
            if (predicate.predicate(head.page)) {
                handlePageFinish(head.page);
            }
        }
    }

    @Nullable
    public OnePage getPage(@NonNull OnePagePredicate predicate) {
        Node node = first;
        while (node != null) {
            if (predicate.predicate(node.page)) {
                return node.page;
            }
            node = node.next;
        }
        return null;
    }

    @NonNull
    public List<OnePage> getPages(@NonNull OnePagePredicate predicate) {
        List<OnePage> result = new ArrayList<>();
        Node node = first;
        while (node != null) {
            if (predicate.predicate(node.page)) {
                result.add(node.page);
            }
            node = node.next;
        }
        return result;
    }

    @NonNull
    @Override
    public Iterator<OnePage> iterator() {
        return new Iterator<OnePage>() {

            private Node currentNode = first;
            private Node lastNode = null;

            @Override
            public boolean hasNext() {
                if (currentNode == null) {
                    return false;
                }
                return currentNode.next != null;
            }

            @Override
            public OnePage next() {
                lastNode = currentNode;
                currentNode = lastNode.next;
                return currentNode.page;
            }

            @Override
            public void remove() {
                Node removeNode = currentNode;
                if (removeNode == first) { //移除栈顶
                    handlePageFinish(removeNode.page);
                } else {
                    lastNode.next = currentNode.next;
                    currentNode.page.destroyInternal();
                    size--;
                }
            }
        };
    }

    public void remove(OnePage page) {
        Node node = first;
        Node pre = null;
        while (node != null && node.page != page) {
            pre = node;
            node = node.next;
        }
        if (node != null) {
            if (node == first) { //移除栈顶
                handlePageFinish(node.page);
            } else {
                pre.next = node.next;
                node.page.destroyInternal();
                size--;
            }
        }
    }

    private static class Node {
        @NonNull
        final OnePage page;
        Node next;

        Node(@NonNull OnePage page) {
            this.page = page;
        }

        Node(@NonNull OnePage page, Node next) {
            this.page = page;
            this.next = next;
        }
    }
}
