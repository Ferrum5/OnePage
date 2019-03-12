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

public abstract class OnePageActivity extends AppCompatActivity implements IOnePage{

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
        first.createInternal(this);
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
                if(!page.isDestroyed()) {
                    page.onDestroy();
                }
            }
            mPageStack.clear();
        }
    }

    @Override
    public void unwind() {
        unwind(mPageStack.getFirst());
    }

    void unwind(final OnePage from){
        if (mPageStack.size() < 2) {
            finish();
            return;
        }
        final OnePage top = mPageStack.getFirst();
        if(from != top){
            mPageStack.remove(from);
            from.destroyInternal();
            return;
        }
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

    @Override
    public void navigate(@NonNull OnePage page) {
        final OnePage top = mPageStack.getFirst();
        top.onPause();
        mPageStack.addFirst(page);
        page.createInternal(this);
        page.onStart();
        mContainerLayout.addView(page.mContentView);
        page.onResume();
        if(!page.doNotRemoveLastView()) {
            mContainerLayout.removeView(top.mContentView);
        }
        top.onStop();
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

    @Override
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

    @Override
    public void removePage(@Nullable final OnePage page) {
        mPageStack.remove(page);
        if(page!=null){
            page.destroyInternal();
        }
    }

    @Override
    @Nullable
    public OnePage getPage(@NonNull OnePagePredicate predicate) {
        for (OnePage page : mPageStack) {
            if (predicate.predicate(page)) {
                return page;
            }
        }
        return null;
    }

    @Override
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

    @Override
    @Nullable
    public OnePage topPage() {
        return mPageStack.getFirst();
    }
}
