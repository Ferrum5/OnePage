package com.github.waterpeak.onepage;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class OnePageActivity extends AppCompatActivity implements IOnePage {

    OnePageStack mPageStack;
    private OnePageContainerLayout mContainerLayout;

    private boolean currentLifeStatusResume = false;
    private boolean currentLifeStatusStart = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageStack = new OnePageStack(this);
        mContainerLayout = new OnePageContainerLayout(this);
        setContentView(mContainerLayout);
        final OnePage first = createFirstPage();
        mPageStack.push(first);
        first.createInternal(this);
        mContainerLayout.addView(first.mContentView);
    }

    @NonNull
    protected abstract OnePage createFirstPage();

    @Override
    protected void onStart() {
        super.onStart();
        currentLifeStatusStart = false;
        final OnePage page = mPageStack.peek();
        if (page != null) {
            page.onStart();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        currentLifeStatusStart = true;
        final OnePage page = mPageStack.peek();
        if (page != null) {
            page.onStop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentLifeStatusResume = true;
        final OnePage page = mPageStack.peek();
        if (page != null) {
            page.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        currentLifeStatusResume = false;
        final OnePage page = mPageStack.peek();
        if (page != null) {
            page.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mPageStack.isEmpty()) {
            mContainerLayout.removeAllViews();
            mPageStack.onDestroy();
        }
    }

    void handlePageFinish(final OnePage from) {
        if (mPageStack.size() < 2) {
            finish();
            return;
        }
        final OnePage top = mPageStack.peek();
        if (from != top) {
            mPageStack.remove(from);
            return;
        }
        mPageStack.pop();
        final OnePage afterTop = mPageStack.peek();
        if (currentLifeStatusResume) {
            top.onPause();
        }
        if (currentLifeStatusStart) {
            afterTop.onStart();
        }
        mContainerLayout.addView(afterTop.mContentView);
        if (currentLifeStatusResume) {
            afterTop.onResume();
        }
        afterTop.onUnwindFromPage(top);
        mContainerLayout.removeView(top.mContentView);
        if (currentLifeStatusStart) {
            top.onStop();
        }
        top.destroyInternal();
    }

    @Override
    public void navigate(@NonNull OnePage page) {
        final OnePage top = mPageStack.peek();
        if (currentLifeStatusResume) {
            top.onPause();
        }
        mPageStack.push(page);
        page.createInternal(this);
        if (currentLifeStatusStart) {
            page.onStart();
        }
        mContainerLayout.addView(page.mContentView);
        if (currentLifeStatusResume) {
            page.onResume();
        }
        if (!page.doNotRemoveLastView()) {
            mContainerLayout.removeView(top.mContentView);
        }
        if (currentLifeStatusStart) {
            top.onStop();
        }
    }

    @Override
    public OnePageStack getPageStack() {
        return mPageStack;
    }

    @Override
    public void onBackPressed() {
        if (!mPageStack.isEmpty()) {
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
}
