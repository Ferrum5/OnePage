package com.github.waterpeak.onepage;

import android.view.ViewGroup;

public interface IOnePageHost {
    void doAfterLastPageFinished();
    OnePageActivity getHostActivity();
    ViewGroup getContainer();
}
