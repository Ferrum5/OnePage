package com.github.waterpeak.onepage;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.viewpager.widget.PagerAdapter;

public class OnePageSwitcher implements LifecycleObserver {

    private final OnePageActivity mHost;
    private final OnePageContainerLayout container;
    private OnePage pages[];
    private int index = -1;

    private boolean currentLifeStatusResume = false;
    private boolean currentLifeStatusStart = false;
    private boolean currentLifeStatusDestroy = false;

    public OnePageSwitcher(@NonNull OnePageActivity host,
                           @NonNull OnePageContainerLayout container,
                           @NonNull OnePage... pages) {
        mHost = host;
        this.container = container;
        this.pages = pages;
    }

    public void setPages(OnePage pages[]){
        this.pages = pages;
    }

    public void setIndex(int index) {
        if(currentLifeStatusDestroy){
            return;
        }
        if (index != this.index) {
            OnePage current = null;
            if (this.index >= 0) {
                current = pages[this.index];
            }
            if (current != null) {
                if (currentLifeStatusResume) {
                    current.onPause();
                }
            }
            OnePage target = pages[index];
            target.createInternal(mHost);
            if (currentLifeStatusStart) {
                target.onStart();
            }
            container.addView(target.mContentView);
            if (currentLifeStatusResume) {
                target.onResume();
            }
            if (current != null) {
                container.removeView(current.mContentView);
                if (currentLifeStatusStart) {
                    current.onStop();
                }
            }
            this.index = index;
        }
    }

    public int getIndex() {
        return index;
    }

    public OnePage getCurrentPage(){
        return pages[index];
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        currentLifeStatusStart = true;
        if (index >= 0) {
            if (pages[index].isCreated()) {
                pages[index].onStart();
            }

        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        currentLifeStatusResume = true;
        if (index >= 0) {
            if (pages[index].isCreated()) {
                pages[index].onResume();
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        currentLifeStatusStart = false;
        if (index >= 0) {
            if (pages[index].isCreated()) {
                pages[index].onPause();
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        currentLifeStatusResume = false;
        if (index >= 0) {
            if (pages[index].isCreated()) {
                pages[index].onStop();
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        currentLifeStatusStart = false;
        currentLifeStatusDestroy = true;
        for (OnePage page : pages) {
            if (page.isCreated()) {
                page.destroyInternal();
            }
        }
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
                if(currentLifeStatusDestroy){
                    return pages[position];
                }
                OnePage page = pages[position];
                page.createInternal(mHost);
                if (currentLifeStatusStart) {
                    page.onStart();
                }
                container.addView(page.mContentView);
                if (currentLifeStatusResume) {
                    page.onResume();
                }
                return page.mContentView;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                if(currentLifeStatusDestroy){
                    return;
                }
                OnePage page = pages[position];
                if (currentLifeStatusResume) {
                    page.onPause();
                }
                container.removeView(page.mContentView);
                if (currentLifeStatusStart) {
                    page.onStop();
                }
            }
        };
    }
}
