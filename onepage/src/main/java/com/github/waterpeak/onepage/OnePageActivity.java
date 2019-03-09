package com.github.waterpeak.onepage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import java.util.Iterator;
import java.util.LinkedList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class OnePageActivity extends AppCompatActivity {

    private LinkedList<OnePage> mPageStack;
    private OnePageContainerLayout mContainerLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageStack = new LinkedList<>();
        mContainerLayout = new OnePageContainerLayout(this);
        setContentView(mContainerLayout);
        OnePage first = createFirstPage();
        first.attachHost(this);
        mPageStack.addFirst(first);
        first.onCreate();
        mContainerLayout.addView(first.mContentView);
    }

    protected abstract OnePage createFirstPage();

    @Override
    protected void onStart() {
        super.onStart();
        mPageStack.getFirst().onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPageStack.getFirst().onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPageStack.getFirst().onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPageStack.getFirst().onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mPageStack.isEmpty()) {
            mContainerLayout.removeAllViews();
            for (OnePage page : mPageStack) {
                page.onDestroy();
            }
            mPageStack.clear();
        }
    }

    void unwind() {
        OnePage top = mPageStack.removeFirst();
        OnePage afterTop = mPageStack.peek();
        top.onPause();
        afterTop.onStart();
        mContainerLayout.addView(afterTop.mContentView);
        afterTop.onResume();
        mContainerLayout.removeView(top.mContentView);
        top.onStop();
        top.onDestroy();
    }

    void navigate(OnePage page) {
        navigate(page, true);
    }

    void navigate(OnePage page, boolean removeLast) {
        OnePage top = mPageStack.peek();
        top.onPause();
        mPageStack.addFirst(page);
        page.attachHost(this);
        page.onCreate();
        page.onStart();
        mContainerLayout.addView(page.mContentView);
        page.onResume();
        if (removeLast) {
            mContainerLayout.removeView(top.mContentView);
        }
        top.onStop();
    }

    @Override
    public void onBackPressed() {
        if (mPageStack.size() > 1) {
            mPageStack.peek().onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mPageStack.peek().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mPageStack.peek().onActivityResult(requestCode, resultCode, data);
    }


    protected void removePages(OnePagePredicate predicate) {
        Iterator<OnePage> iterator = mPageStack.iterator();
        while (iterator.hasNext()) {
            OnePage page = iterator.next();
            if (predicate.predicate(page)) {
                iterator.remove();
                page.onDestroy();
            }
        }
    }
}
