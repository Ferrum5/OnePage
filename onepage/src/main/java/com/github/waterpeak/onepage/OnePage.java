package com.github.waterpeak.onepage;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.view.View;
import android.widget.Space;

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

    private View mContentView;

    View getContentView() {
        if (mContentView == null) {
            mContentView = onCreateContentView(mHost);
        }
        if (mContentView == null) {
            mContentView = new View(mHost);
        }
        return mContentView;
    }

    @Nullable
    protected abstract View onCreateContentView(@NonNull  Context context);

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mRegistry;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }

    public void attachHost(OnePageActivity host) {
        attachBaseContext(host);
        this.mHost = host;
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
        mRegistry.markState(Lifecycle.State.DESTROYED);
    }

    protected void onBackPressed() {
        mHost.unwind();
    }

    protected void navigate(OnePage page) {
        mHost.navigate(page);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }
}
