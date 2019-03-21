package com.github.waterpeak.onepage;

import android.app.Activity;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;

import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

import java.util.Iterator;
import java.util.List;

public class OnePage extends ContextWrapper
        implements LifecycleOwner, ActivityCompat.OnRequestPermissionsResultCallback,IOnePage{

    protected static final int RESULT_OK = Activity.RESULT_OK;
    protected static final int RESULT_CANCEL = Activity.RESULT_CANCELED;

    private final LifecycleRegistry mRegistry;
    protected OnePageActivity mHost;

    private boolean destroyed = false;

    public boolean isDestroyed(){
        return destroyed;
    }
    public void setDestroyed(boolean value){
        this.destroyed = value;
    }

    public boolean isCreated(){
        return getBaseContext() != null;
    }

    protected boolean doNotRemoveLastView(){
        return false;
    }

    public OnePage() {
        super(null);
        mRegistry = new LifecycleRegistry(this);
        mRegistry.markState(Lifecycle.State.INITIALIZED);
    }

    FrameLayout mContentView;

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mRegistry;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }

    public void setContentView(View view) {
        if (mContentView.getChildCount() > 0) {
            mContentView.removeAllViews();
        }
        mContentView.addView(view);
    }

    public void setContentView(View view, FrameLayout.LayoutParams params) {
        if (mContentView.getChildCount() > 0) {
            mContentView.removeAllViews();
        }
        mContentView.addView(view, params);
    }


    public void attachHost(@NonNull OnePageActivity host) {
        attachBaseContext(host);
        this.mHost = host;
        mContentView = new FrameLayout(host);
    }



    void createInternal(OnePageActivity host){
        if(!isCreated()){
            attachHost(host);
            mHost = host;
            onCreate();
        }
    }

    void destroyInternal(){
        if(destroyed){
            onDestroy();
        }
    }

    protected void onCreate() {
        mRegistry.markState(Lifecycle.State.CREATED);
    }

    protected void onStart() {
        mRegistry.markState(Lifecycle.State.STARTED);
    }

    protected void onResume() {
        mRegistry.markState(Lifecycle.State.RESUMED);
    }

    protected void onPause() {
        mRegistry.markState(Lifecycle.State.STARTED);
    }

    protected void onStop() {
        mRegistry.markState(Lifecycle.State.CREATED);
    }

    protected void onDestroy() {
        destroyed = true;
        mRegistry.markState(Lifecycle.State.DESTROYED);
    }

    protected void onBackPressed() {
        mHost.handlePageFinish(this);
    }


    protected void onUnwindFromPage(@NonNull OnePage page) { }


    @Override
    public void navigate(@NonNull OnePage page) {
        mHost.navigate(page);
    }

    public void finish(){
        getPageStack().remove(this);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { }


    @Override
    public OnePageStack getPageStack() {
        return mHost.mPageStack;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
