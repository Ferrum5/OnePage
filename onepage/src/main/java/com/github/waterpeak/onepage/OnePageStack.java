package com.github.waterpeak.onepage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OnePageStack implements Iterable<OnePage> {

    private int size = 0;
    private Node first = null;
    private final OnePageActivity host;

    public OnePageStack(OnePageActivity host) {
        this.host = host;
    }

    void push(@NonNull OnePage page) {
        first = new Node(page, first);
        size++;
    }

    @Nullable
    OnePage pop() {
        final Node node = this.first;
        if (node != null) {
            this.first = node.next;
            size--;
            return node.page;
        }
        return null;
    }

    @Nullable
    public OnePage peek() {
        final Node node = this.first;
        if (node != null) {
            return node.page;
        }
        return null;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return first == null;
    }


    public void removePage(@NonNull OnePagePredicate predicate) {
        Iterator<OnePage> iterator = iterator();
        while (iterator.hasNext()) {
            OnePage page = iterator.next();
            if (predicate.predicate(page)) {
                iterator.remove();
            }
        }
    }

    @Nullable
    public OnePage getPage(@NonNull OnePagePredicate predicate) {
        for (OnePage page : this) {
            if (predicate.predicate(page)) {
                return page;
            }
        }
        return null;
    }

    @NonNull
    public List<OnePage> getPages(@NonNull OnePagePredicate predicate) {
        List<OnePage> result = new ArrayList<>();
        for (OnePage page : this) {
            if (predicate.predicate(page)) {
                result.add(page);
            }
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
                    host.handlePageFinish(removeNode.page);
                } else {
                    lastNode.next = currentNode.next;
                    currentNode.page.destroyInternal();
                }
            }
        };
    }

    public void remove(OnePage page) {
        Node node = first;
        while (node != null && node.page != page) {
            node = node.next;
        }
        if (node != null) {
            if (node == first) { //移除栈顶
                host.handlePageFinish(node.page);
            } else {
                node.page.destroyInternal();
            }
        }
    }

    void onDestroy() {
        Node node = first;
        while (node != null) {
            node.page.destroyInternal();
            node = node.next;
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
