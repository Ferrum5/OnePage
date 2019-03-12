package com.github.waterpeak.onepage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public interface IOnePage {

    public void navigate(@NonNull OnePage page);

    public OnePageStack getPageStack();
}
