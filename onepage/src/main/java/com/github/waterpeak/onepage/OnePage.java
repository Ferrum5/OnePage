package com.github.waterpeak.onepage;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

public abstract class OnePage extends ContextWrapper
        implements LifecycleOwner, ActivityCompat.OnRequestPermissionsResultCallback {

    private final LifecycleRegistry mRegistry;
    protected OnePageActivity mHost;

    public OnePage() {
        super(null);
        mRegistry = new LifecycleRegistry(this);
        mRegistry.markState(Lifecycle.State.INITIALIZED);
    }

    View mContentView;

    @NonNull
    protected abstract View onCreateContentView(@NonNull Context context);

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mRegistry;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }

    public void attachHost(@NonNull OnePageActivity host) {
        attachBaseContext(host);
        this.mHost = host;
    }

    protected void onCreate() {
        mRegistry.markState(Lifecycle.State.CREATED);
        mContentView = onCreateContentView(mHost);
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
        mRegistry.markState(Lifecycle.State.DESTROYED);
    }

    protected void onBackPressed() {
        mHost.unwind();
    }

    protected void unwind(){
        mHost.unwind();
    }

    protected void onUnwindFromPage(@NonNull OnePage page){

    }

    protected void navigateAndRemove(@NonNull OnePage page) {
        mHost.navigate(page, true, false);
    }

    protected void navigateKeepView(@NonNull OnePage page) {
        mHost.navigate(page,false,true);
    }

    protected void navigate(@NonNull OnePage page) {
        mHost.navigate(page,true,true);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { }

}
