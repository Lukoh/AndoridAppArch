package com.goforer.goforerarchblueprint.domain.sort;

import com.goforer.base.presentation.view.fragment.RecyclerFragment;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

public class SortImpl<T> {
    private RecyclerFragment mFragment;

    @Inject
    public SortImpl() {
    }

    public void sort(List<T> items, Comparator<T> comparator) {
        if (items != null && !items.isEmpty()) {
            Collections.sort(items, comparator);
        }

        mFragment.onSorted(items);
    }

    public void setFragment(RecyclerFragment fragment) {
        mFragment = fragment;
    }

    @SuppressWarnings("unused")
    public List<T> testSort(List<T> items, Comparator<T> comparator) {
        if (items != null && !items.isEmpty()) {
            Collections.sort(items, comparator);
        }

        return items;
    }
}
