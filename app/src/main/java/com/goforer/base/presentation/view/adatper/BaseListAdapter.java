/*
 * Copyright (C) 2015-2017 Lukoh Nam, goForer
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

package com.goforer.base.presentation.view.adatper;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;

import com.goforer.base.presentation.model.BaseModel;
import com.goforer.base.presentation.view.holder.BaseViewHolder;
import com.goforer.base.presentation.view.holder.ItemHolderBinder;

import java.util.List;

public abstract class BaseListAdapter<T extends BaseModel> extends BaseAdapter {
    private boolean mIsReachedToLastItem = false;
    private boolean mIsReachedToLastPage = false;
    private boolean mIsEmptyItems = false;
    private boolean mIsLoadingItems = false;
    private boolean mUsedLoadingImage = false;

    @Nullable
    private List<T> mItems;

    // each time data is set, we update this variable so that if DiffUtil calculation returns
    // after repetitive updates, we can ignore the old calculation
    private int mDataVersion = 0;

    public BaseListAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    public int getItemCount() {
        if (mItems != null) {
            return mItems.size();
        }

        return 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder != null) {
            if (position >= (mItems != null ? mItems.size() : 0)) {
                return;
            }

            T item = mItems != null ? mItems.get(position) : null;
            if (item != null) {
                ((ItemHolderBinder<T>) holder).bindItemHolder(holder, item, position);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    @MainThread
    public void addItems(List<T> items, boolean isSorted) {
        if (isSorted) {
            mItems = items;
            notifyDataSetChanged();

            return;
        }

        mDataVersion ++;
        if (mItems == null) {
            if (items == null) {
                return;
            }
            mItems = items;
            notifyDataSetChanged();
        } else if (items == null) {
            int oldSize = mItems.size();
            mItems = null;
            notifyItemRangeRemoved(0, oldSize);
        } else {
            final int startVersion = mDataVersion;
            final List<T> oldItems = mItems;
            new AsyncTask<Void, Void, DiffUtil.DiffResult>() {
                @Override
                protected DiffUtil.DiffResult  doInBackground(Void... voids) {
                    return DiffUtil.calculateDiff(new DiffUtil.Callback() {
                        @Override
                        public int getOldListSize() {
                            return oldItems.size();
                        }

                        @Override
                        public int getNewListSize() {
                            return items.size();
                        }

                        @Override
                        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                            T oldItem = oldItems.get(oldItemPosition);
                            T newItem = items.get(newItemPosition);
                            return BaseListAdapter.this.areItemsTheSame(oldItem, newItem);
                        }

                        @Override
                        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                            T oldItem = oldItems.get(oldItemPosition);
                            T newItem = items.get(newItemPosition);
                            return BaseListAdapter.this.areContentsTheSame(oldItem, newItem);
                        }
                    });
                }

                @Override
                protected void onPostExecute(DiffUtil.DiffResult diffResult) {
                    if (startVersion != mDataVersion) {
                        // ignore update
                        return;
                    }

                    mItems = items;
                    diffResult.dispatchUpdatesTo(BaseListAdapter.this);
                }
            }.execute();
        }
    }

    /**
     * Set true if the item is reached to the last.
     *
     * @param isReachedToLast true if the item is reached to the last
     */
    public void setReachedToLastItem(boolean isReachedToLast) {
        mIsReachedToLastItem = isReachedToLast;
    }

    /**
     * Set true if the page is reached to the last and notify any registered observers that the item
     * reflected at last item in last page has been newly inserted for last footer.
     *
     * @param isReachedToLast true if the page is reached to the last
     */
    public void setReachedToLastPage(boolean isReachedToLast) {
        mIsReachedToLastPage = isReachedToLast;
        if (isReachedToLast) {
            setReachedToLastItem(false);
            notifyItemInserted(mItems != null ? mItems.size() : 0);
        }
    }

    public List<T> getItems() {
        return mItems;
    }

    /**
     * Set true if the items is empty.
     *
     * @param isEmptyItems true if the items is empty
     */
    @SuppressWarnings("unused")
    public void setEmptyItems(boolean isEmptyItems) {
        mIsEmptyItems = isEmptyItems;
    }

    /**
     * Set true if the items is loading.
     *
     * @param isLoadingItems true if the items is loading
     */
    public void setLoadingItems(boolean isLoadingItems) {
        mIsLoadingItems = isLoadingItems;
    }

    /**
     * Scroll to the specified adapter position.
     * Actual position of the item on the screen depends on the LayoutManager implementation.
     *
     * @param layoutManager The currently bound LayoutManager
     * @param position Scroll to this adapter position
     */
    @SuppressWarnings("unused")
    public boolean moveSelectedPosition(RecyclerView.LayoutManager layoutManager, int position) {
        if (position >= 0 && position < getItemCount()) {
            layoutManager.scrollToPosition(position);
            return true;
        }

        return false;
    }

    /**
     * Check if the item is reached to the last.
     *
     * @return true if the item or page is reached to the last
     */
    @SuppressWarnings("unused")
    public boolean isReachedToLastItem() {
        return mIsReachedToLastItem;
    }

    /**
     * Check if the page is reached to the last.
     *
     * @return true if the item or page is reached to the last
     */
    public boolean isReachedToLastPage() {

        return mIsReachedToLastPage;
    }

    /**
     * Check if the items is empty.
     *
     * @return true if the items is empty
     */
    @SuppressWarnings("unused")
    public boolean isEmptyItems() {
        return mIsEmptyItems;
    }

    /**
     * Check if the items is loading.
     *
     * @return true if the items is loading
     */
    @SuppressWarnings("unused")
    public boolean isLoadingItems() {
        return mIsLoadingItems;
    }

    /**
     * Check if the loading image is used to the list as an item.
     *
     * @return true if the loading image is used
     */
    @SuppressWarnings("unused")
    public boolean usedLoadImage() {
        return mUsedLoadingImage;
    }

    /**
     * Set true if the loading image is used.
     *
     * @param usedLoadingImage true if the loading image is used
     */
    protected void setUsedLoadingImage(boolean usedLoadingImage) {
        mUsedLoadingImage = usedLoadingImage;
    }

    protected abstract boolean areItemsTheSame(T oldItem, T newItem);

    protected abstract boolean areContentsTheSame(T oldItem, T newItem);
}