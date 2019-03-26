package com.github.waterpeak.onepage;

import android.app.Activity;
import android.content.ContextWrapper;
import android.content.Intent;
import android.view.*;

import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

public class OnePage extends ContextWrapper
        implements LifecycleOwner, ActivityCompat.OnRequestPermissionsResultCallback,IOnePage, IOnePageHost{

    protected static final int RESULT_OK = Activity.RESULT_OK;
    protected static final int RESULT_CANCEL = Activity.RESULT_CANCELED;

    private final LifecycleRegistry mRegistry;
    protected OnePageActivity mHost;

    private boolean isDestroyed = false;

    public boolean isDestroyed(){
        return isDestroyed;
    }
    public void setDestroyed(boolean value){
        this.isDestroyed = value;
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

    OnePageContainerLayout mContentView;
    private OnePageManager childPageManager;

    protected OnePageContainerLayout getPageContainer(){
        return mContentView;
    }

    @Override
    public void doAfterLastPageFinished() {
        mHost.getPageManager().handlePageFinish(this);
    }

    @Override
    public OnePageActivity getHostActivity() {
        return mHost.getHostActivity();
    }

    @Override
    public ViewGroup getContainer() {
        return mContentView;
    }

    @Override
    public OnePageManager getPageManager() {
        return mHost.getPageManager();
    }

    protected OnePageManager getChildPageManager(){
        if(childPageManager==null){
            childPageManager = new OnePageManager(this);
        }
        return childPageManager;
    }

    protected void setChildPageManager(OnePageManager manager){
        this.childPageManager = manager;
    }

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
        mContentView = new OnePageContainerLayout(host);
    }



    void createInternal(OnePageActivity host){
        if(!isCreated()){
            attachHost(host);
            mHost = host;
            onCreate();
        }
    }

    void destroyInternal(){
        if(!isDestroyed){
            onDestroy();
        }
    }

    protected void onCreate() {
        mRegistry.markState(Lifecycle.State.CREATED);
    }

    protected void onStart() {
        mRegistry.markState(Lifecycle.State.STARTED);
        if(childPageManager!=null){
            childPageManager.onStart();
        }
    }

    protected void onResume() {
        mRegistry.markState(Lifecycle.State.RESUMED);
        if(childPageManager!=null){
            childPageManager.onResume();
        }
    }

    protected void onPause() {
        mRegistry.markState(Lifecycle.State.STARTED);
        if(childPageManager!=null){
            childPageManager.onPause();
        }
    }

    protected void onStop() {
        mRegistry.markState(Lifecycle.State.CREATED);
        if(childPageManager!=null){
            childPageManager.onStop();
        }
    }

    protected void onDestroy() {
        isDestroyed = true;
        mRegistry.markState(Lifecycle.State.DESTROYED);
        if(childPageManager!=null){
            childPageManager.onDestroy();
        }
    }

    protected void onBackPressed() {
        if(childPageManager!=null){
            if(childPageManager.onBackPressed()){
                return;
            }
        }
        mHost.getPageManager().handlePageFinish(this);
    }


    protected void onUnwindFromPage(@NonNull OnePage page) { }


    @Override
    public void navigate(@NonNull OnePage page) {
        mHost.navigate(page);
    }

    public void finish(){
        getPageManager().remove(this);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
