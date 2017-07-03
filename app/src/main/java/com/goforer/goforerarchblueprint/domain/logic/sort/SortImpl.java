/*
 * Copyright (C) 2017 Lukoh Nam, goForer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.goforer.goforerarchblueprint.domain.logic.sort;

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
