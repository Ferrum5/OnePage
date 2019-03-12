package com.github.waterpeak.onepage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public interface IOnePage {

    public void removePage(@Nullable OnePage page);

    public void removePages(@NonNull OnePagePredicate predicate);
    @Nullable
    public OnePage getPage(@NonNull OnePagePredicate predicate);
    @NonNull
    public List<OnePage> getPages(@NonNull OnePagePredicate predicate);;

    @Nullable
    public OnePage topPage();

    public void navigate(@NonNull OnePage page);

    public void unwind();
}
