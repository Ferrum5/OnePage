package com.github.waterpeak.onepage;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.viewpager.widget.PagerAdapter;

public class OnePageSwitcher  {

    private OnePage pages[];
    private int index = -1;

    private final OnePageManager pageManager;

    public OnePageSwitcher(OnePageManager pageManager) {
        this.pageManager = pageManager;
    }

    public OnePageSwitcher(@NonNull IOnePageHost host) {
        pageManager = new OnePageManager(host);
    }

    public void setPages(@NonNull OnePage... pages) {
        this.pages = pages;
    }

    public void setIndex(int index) {
        this.index = index;
        pageManager.replace(pages[index]);
    }

    public int getIndex() {
        return index;
    }

    public OnePage getCurrentPage() {
        return pages[index];
    }

    public void onStart() {
        pageManager.onStart();
    }

    public void onResume() {
        pageManager.onResume();
    }

    public void onPause() {
        pageManager.onPause();
    }

    public void onStop() {
        pageManager.onStop();
    }

    public void onDestroy() {
        pageManager.onDestroy();
    }

    public LifecycleObserver getLifecycleObserver(){
        return pageManager;
    }

    @NonNull
    public PagerAdapter getAdapter() {
        return new PagerAdapter() {

            @Override
            public int getCount() {
                return pages.length;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                OnePage page = pages[position];
                pageManager.pageResume(page, container);
                return page.mContentView;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                pageManager.pageStop(pages[position], container);
            }
        };
    }
}
