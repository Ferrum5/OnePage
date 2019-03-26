package com.github.waterpeak.onepage;

import android.view.ViewGroup;

public class OnePageHost implements IOnePageHost {

    private final IOnePageHost host;
    private final ViewGroup container;

    public OnePageHost(IOnePageHost host, ViewGroup container) {
        this.host = host;
        this.container = container;
    }


    @Override
    public void doAfterLastPageFinished() {
        host.doAfterLastPageFinished();
    }

    @Override
    public OnePageActivity getHostActivity() {
        return host.getHostActivity();
    }

    @Override
    public ViewGroup getContainer() {
        return container;
    }

    @Override
    public void childPageNotHandleBackPressed(OnePage page) {
        host.childPageNotHandleBackPressed(page);
    }
}
