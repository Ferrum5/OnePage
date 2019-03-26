package com.github.waterpeak.onepage;

import android.content.Intent;
import android.os.Bundle;

import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class OnePageActivity extends AppCompatActivity implements IOnePage, IOnePageHost {

    private OnePageManager mPageManager;
    private OnePageContainerLayout containerLayout;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        containerLayout  = new OnePageContainerLayout(this);
        setContentView(containerLayout);
        mPageManager = new OnePageManager(this);
    }

    @Override
    public ViewGroup getContainer() {
        return containerLayout;
    }

    @Override
    public OnePageActivity getHostActivity() {
        return this;
    }

    @Override
    public void doAfterLastPageFinished() {
        finish();
    }

    @Override
    public void childPageNotHandleBackPressed(OnePage page) {
        mPageManager.handlePageFinish(page);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPageManager.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPageManager.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPageManager.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPageManager.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPageManager.onDestroy();
    }



    @Override
    public void navigate(@NonNull OnePage page) {
        mPageManager.navigate(page);
    }

    @Override
    public OnePageManager getPageManager() {
        return mPageManager;
    }

    @Override
    public void onBackPressed() {
        if(!mPageManager.onBackPressed()){
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mPageManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mPageManager.onActivityResult(requestCode, resultCode, data);
    }
}
