package com.github.waterpeak.onepage;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
        final OnePage first = createFirstPage();
        mPageStack.addFirst(first);
        if (first.getBaseContext() == null) {
            first.attachHost(this);
            first.onCreate();
        }
        mContainerLayout.addView(first.mContentView);
    }

    @NonNull
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
        if (mPageStack.size() < 2) {
            finish();
            return;
        }
        final OnePage top = mPageStack.removeFirst();
        final OnePage afterTop = mPageStack.getFirst();
        top.onPause();
        afterTop.onStart();
        mContainerLayout.addView(afterTop.mContentView);
        afterTop.onResume();
        afterTop.onUnwindFromPage(top);
        mContainerLayout.removeView(top.mContentView);
        top.onStop();
        top.onDestroy();
    }

    void navigate(OnePage page, boolean removeLastView, boolean keepInStack) {
        final OnePage top;
        if (keepInStack) {
            top = mPageStack.getFirst();
        } else {
            top = mPageStack.removeFirst();
        }
        top.onPause();
        mPageStack.addFirst(page);
        if (page.getBaseContext() == null) {
            page.attachHost(this);
            page.onCreate();
        }
        page.onStart();
        mContainerLayout.addView(page.mContentView);
        page.onResume();
        if (removeLastView || (!keepInStack)) {
            mContainerLayout.removeView(top.mContentView);
        }
        top.onStop();
        if (!keepInStack) {
            top.onDestroy();
        }
    }

    @Override
    public void onBackPressed() {
        if (mPageStack.size() > 1) {
            mPageStack.getFirst().onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mPageStack.getFirst().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mPageStack.getFirst().onActivityResult(requestCode, resultCode, data);
    }


    public void removePages(@NonNull OnePagePredicate predicate) {
        Iterator<OnePage> iterator = mPageStack.iterator();
        while (iterator.hasNext()) {
            OnePage page = iterator.next();
            if (predicate.predicate(page)) {
                iterator.remove();
                page.onDestroy();
            }
        }
    }

    @Nullable
    public OnePage getPage(@NonNull OnePagePredicate predicate) {
        for (OnePage page : mPageStack) {
            if (predicate.predicate(page)) {
                return page;
            }
        }
        return null;
    }

    @NonNull
    public List<OnePage> getPages(@NonNull OnePagePredicate predicate) {
        List<OnePage> result = new ArrayList<>();
        for (OnePage page : mPageStack) {
            if (predicate.predicate(page)) {
                result.add(page);
            }
        }
        return result;
    }
}
