package com.github.waterpeak.onepage;

import androidx.annotation.NonNull;

public interface IOnePage {

    void navigate(@NonNull OnePage page);

    OnePageManager getPageManager();
}
