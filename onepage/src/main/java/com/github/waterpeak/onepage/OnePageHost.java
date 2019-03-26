package com.github.waterpeak.onepage;

import android.view.ViewGroup;

public class OnePageHost implements IOnePageHost {

    private final OnePageActivity host;
    private final ViewGroup container;

    public OnePageHost(OnePageActivity host, ViewGroup container) {
        this.host = host;
        this.container = container;
    }

    public OnePageHost(OnePage host) {
        this.host = host.mHost;
        this.container = host.mContentView;
    }


    @Override
    public void doAfterLastPageFinished() {

    }

    @Override
    public OnePageActivity getHostActivity() {
        return host;
    }

    @Override
    public ViewGroup getContainer() {
        return container;
    }
}
