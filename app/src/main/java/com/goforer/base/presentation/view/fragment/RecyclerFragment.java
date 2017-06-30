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

package com.goforer.base.presentation.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.goforer.base.presentation.view.adatper.BaseListAdapter;
import com.goforer.base.presentation.view.decoration.DividerItemDecoration;

import com.goforer.goforerarchblueprint.R;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public abstract class RecyclerFragment<T> extends BaseFragment {
    private static final String TAG = "RecyclerFragment";

    private static final int FIRST_PAGE = 1;

    private BaseListAdapter mBaseArrayAdapter;
    private OnProcessListener mListener;

    private ItemTouchHelper mItemTouchHelper;

    private boolean mItemTouchHelperEnabled = false;
    private boolean mIsLoading = false;
    @SuppressWarnings("unused")
    private boolean mIsUpdated = false;

    private int mTotalPage = 0;
    private int mCurrentPage = 0;

    private List<T> mItems = new ArrayList<>();

    protected RecyclerView.OnScrollListener mOnScrollListener;

    @BindView(R.id.swipe_layout)
    protected SwipyRefreshLayout mSwipeLayout;
    @BindView(R.id.recycler_view)
    protected RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_default_recycler, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setViews();
    }

    @Override
    public void onResume() {
        super.onResume();

        setScrollListener();
    }

    @Override
    public void onPause() {
        super.onPause();

        removeScrollListener();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void setViews() {
        setupSwipeLayout();

        mRecyclerView.setLayoutManager(createLayoutManager());
        if (isItemDecorationVisible()) {
            addItemDecorations();
        }

        addItemTouchListener();
        mRecyclerView.setItemAnimator(createItemAnimator());
        Adapter adapter = createAdapter();
        ItemTouchHelper.Callback callback = createItemTouchHelper();
        if (callback != null) {
            setItemTouchHelper(callback);
        }

        setScrollListener();

        if (adapter instanceof BaseListAdapter) {
            mBaseArrayAdapter = (BaseListAdapter)adapter;
        }

        Log.i(TAG, "Initialize views");
    }

    private void request(boolean isRefreshed) {
        if (isLastPage(mCurrentPage)) {
            doneRefreshing();
            if (mBaseArrayAdapter != null) {
                mBaseArrayAdapter.setReachedToLastPage(true);
            }
        }

        if (!mIsLoading) {
            mIsLoading = true;
            if (mBaseArrayAdapter != null) {
                mBaseArrayAdapter.setLoadingItems(true);
            }

            mCurrentPage = FIRST_PAGE;
            setTotalPage(FIRST_PAGE);
            if (isRefreshed) {
                requestData(false);
            } else {
                requestData(true);
            }
        }
    }

    private void requestUpdate() {
        if (!mIsLoading) {
            mIsLoading = true;
            if (mBaseArrayAdapter != null) {
                mBaseArrayAdapter.setLoadingItems(true);
            }

            mIsUpdated = true;

            updateData();
        }
    }

    /**
     * An OnScrollListener can be set on a RecyclerView to receive messages
     * when a scrolling event has occurred on that RecyclerView.
     *
     * If you are planning to have several listeners at the same time, use
     * RecyclerView#addOnScrollListener. If there will be only one listener at the time and you
     * want your components to be able to easily replace the listener use
     * RecyclerView#setOnScrollListener.
     */
    private void setScrollListener() {
        removeScrollListener();

        mOnScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!mIsLoading && !mBaseArrayAdapter.isReachedToLastPage() && dy >= 0) {
                    int lastVisibleItemPosition = getLastVisibleItem();
                    int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                    if (lastVisibleItemPosition >= totalItemCount - 1) {
                        mBaseArrayAdapter.setReachedToLastItem(true);
                        if (isLastPage(mCurrentPage)) {
                            reachToLastPage();
                            doneRefreshing();
                            mBaseArrayAdapter.setReachedToLastPage(true);

                            return;
                        }

                        scrolledReachToLast();
                        requestNextData(mCurrentPage);
                        mListener.onScrolledToLast(recyclerView, dx, dy);
                    } else {
                        mBaseArrayAdapter.setReachedToLastItem(false);
                    }
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    mListener.onScrolling();
                } else {
                    onScrolled(recyclerView, recyclerView.getScrollX(), recyclerView.getScrollY());
                    mListener.onScrolled(recyclerView, recyclerView.getScrollX(), recyclerView.getScrollY());
                }
            }
        };

        mRecyclerView.addOnScrollListener(mOnScrollListener);
    }

    /**
     * Remove a listener that was notified of any changes in scroll state or position.
     */
    private void removeScrollListener() {
        if (mOnScrollListener != null) {
            mRecyclerView.removeOnScrollListener(mOnScrollListener);
        }
    }

    /**
     * Set an ItemTouchHelper that will work with the given Callback
     *
     * @param callback This is the contract between ItemTouchHelper and your application.
     *                  It lets you control which touch behaviors are enabled per each ViewHolder
     *                  and also receive callbacks when user performs these actions.
     *
     * @see ItemTouchHelper.Callback
     */
    private void setItemTouchHelper(ItemTouchHelper.Callback callback) {
        mItemTouchHelperEnabled = true;
        attachItemTouchHelperToRecyclerView(callback);
    }

    /**
     * Attaches the ItemTouchHelper to the provided RecyclerView. If TouchHelper is already
     * attached to a RecyclerView, it will first detach from the previous one. You can call this
     * method with {@code null} to detach it from the current RecyclerView.
     *
     * @param callback This is the contract between ItemTouchHelper and your application.
     *                  It lets you control which touch behaviors are enabled per each ViewHolder
     *                  and also receive callbacks when user performs these actions.
     *
     * @return the ItemTouchHelper for attaching to the provided RecyclerView
     *
     * @see ItemTouchHelper
     *
     */
    private ItemTouchHelper attachItemTouchHelperToRecyclerView(ItemTouchHelper.Callback callback) {
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(getRecyclerView());

        return mItemTouchHelper;
    }

    /**
     * Check if the given page is the last page.
     * Return true if the given page is the last page
     * <p>
     * To check if the given page is the last page, you must override
     * This method is called whenever the swipe gesture triggers a refresh.
     * </p>
     *
     * @param pageNum the current page number to check if given page is the last page
     *
     */
    protected boolean isLastPage(int pageNum) {
        return (getTotalPage() == pageNum) && (getTotalPage() >= 1);
    }

    /**
     * Set the total page count
     *
     * @param totalPage The total page count
     *
     */
    protected void setTotalPage(int totalPage) {
        mTotalPage = totalPage;
    }

    /**
     * Get the total page count
     *
     * @return The total page count
     */
    protected int getTotalPage() {
        return mTotalPage;
    }

    /**
     * RecyclerView can perform several optimizations if it can know in advance that changes in
     * adapter content cannot change the size of the RecyclerView itself.
     * If your use of RecyclerView falls into this category, set this to true.
     *
     * @param hasFixedSize true if adapter changes cannot affect the size of the RecyclerView.
     *                     setItemHasFixedSize(true) means the RecyclerView has children (items)
     *                     that has fixed width and
     */
    protected void setItemHasFixedSize(boolean hasFixedSize) {
        mRecyclerView.setHasFixedSize(hasFixedSize);
    }

    /**
     * Add an {@link ItemDecoration} to this RecyclerView. Item decorations can
     * affect both measurement and drawing of individual item views.
     *
     * <p>Item decorations are ordered. Decorations placed earlier in the list will
     * be run/queried/drawn first for their effects on item views. Padding added to views
     * will be nested; a padding added by an earlier decoration will mean further
     * item decorations in the list will be asked to draw/pad within the previous decoration's
     * given area.</p>
     *
     */
    protected void addItemDecorations() {
        mRecyclerView.addItemDecoration(createItemDecoration());
    }

    protected void addItemTouchListener(){
    }

    /**
     * Sets the {@link SwipyRefreshLayout}'s setting that this RecyclerFragment will use.
     *
     * <p>
     * To set the {@link SwipyRefreshLayout}'s more setting, you must override
     * </p>
     *
     */
    protected void setupSwipeLayout() {
        mSwipeLayout.setColorSchemeResources(R.color.redLight);
        mSwipeLayout.setOnRefreshListener(direction -> requestUpdate());
    }

    /**
     * Create the {@link LayoutManager} that this RecyclerFragment will use.
     *
     * <p>
     * To set the {@link LayoutManager} to provide RecyclerFragment, you must override
     * </p>
     *
     * <p>In contrast to other adapter-backed views such as {@link android.widget.ListView}
     * or {@link android.widget.GridView}, RecyclerFragment allows client code to provide custom
     * layout arrangements for child views. These arrangements are controlled by the
     * {@link LayoutManager}. A LayoutManager must be provided for RecyclerFragment to function.</p>
     *
     * <p>Several default strategies are provided for common uses such as lists and grids.</p>
     *
     * @return LayoutManager to use
     *
     * If you'd like to use GridLayout, you have to override this setLayoutManager method
     * in your fragment as below example:
     *
     * Example
     * @@Override
     * protected RecyclerView.LayoutManager setLayoutManager() {
     *     return new GridLayoutManager(activity, 1);
     * }
     * or to use LinearLayout, you have to override this setLayoutManager method
     * in your fragment as below:
     *
     * @@Override
     * protected RecyclerView.LayoutManager setLayoutManager() {
     *     return new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
     * }
     *
     */
    protected abstract LayoutManager createLayoutManager();

    protected int getLastVisibleItem() {
        LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        }

        return 0;
    }

    /**
     * Create the ItemDecoration.
     *
     * An ItemDecoration allows the application to add a special drawing and layout offset
     * to specific item views from the adapter's data set. This can be useful for drawing dividers
     * between items, highlights, visual grouping boundaries and more.
     *
     * <p>All ItemDecorations are drawn in the order item were added. </p>
     *
     */
    protected ItemDecoration createItemDecoration() {
        return new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL_LIST);
    }

    /**
     * Starts dragging the provided ViewHolder.
     *
     * @param viewHolder The ViewHolder to start dragging. It must be a direct child of
     *                    RecyclerView.
     */
    @Deprecated
    @SuppressWarnings("unused")
    protected void startDrag(RecyclerView.ViewHolder viewHolder) {
        if (mItemTouchHelper != null && mItemTouchHelperEnabled) {
            mSwipeLayout.setRefreshing(false);
            mItemTouchHelper.startDrag(viewHolder);
        }
    }

    /**
     * This implementation of {@link RecyclerView.ItemAnimator} provides basic
     * animations on remove, add, and move events that happen to the items in
     * a RecyclerView. RecyclerView uses a DefaultItemAnimator by default.
     *
     * @return The ItemAnimator for the animations that take place on items as changes are made
     *           to the adapter.
     *
     * @see RecyclerView#setItemAnimator(RecyclerView.ItemAnimator)
     */
    protected RecyclerView.ItemAnimator createItemAnimator() {
        return new DefaultItemAnimator();
    }

    /**
     * Create a new adapter to provide child views on demand.
     * <p>
     * To set a new adapter to provide child views on demand, you must override
     * </p>
     * <p>
     * When adapter is changed, all existing views are recycled back to the pool. If the pool has
     * only one adapter, it will be cleared.
     *
     * @return The new adapter to set, or null to set no adapter.
     */
    protected abstract Adapter createAdapter();

    /**
     * Attach an ItemTouchHelper to the provided RecyclerView.
     * <p>
     * To attach an ItemTouchHelper to the provided RecyclerView, you must override.
     * </p>
     *
     * @return The callback which is the contract between ItemTouchHelper and your application.
     *           It lets you control which touch behaviors are enabled per each ViewHolder
     *           and also receive callbacks when user performs these actions.
     *
     *           return null if you don't want to attach an ItemTouchHelper to provided RecyclerView.
     */
    protected abstract ItemTouchHelper.Callback createItemTouchHelper();

    /**
     * RequestClient to get the information or images from server.
     * <p>
     * To request information or data to Web server, you must override
     * This method is called whenever the adapter position of the last visible view is to last item on the list.
     * </p>
     *
     * Don't implement any code in this overridden method if you don't like to request any information
     * or data to Web server.
     *
     * @param isNew set to true to request new information or images, or false
     *
     */
    protected abstract void requestData(boolean isNew);

    protected abstract void requestNextData(int page);

    /**
     * RequestClient to get the updated information or images from server.
     * <p>
     * To request information or data to Web server, you must override
     * This method is called whenever the swipe gesture triggers a refresh.
     * </p>
     *
     */
    protected abstract void updateData();

    /**
     * Check if the DividerItemDecoration is visible.
     *
     * <p>
     * To check if the given page is the last page, you must override
     * This method is called in {@link #onViewCreated}.
     * </p>
     *
     * @return true if the ItemDecoration in {@link RecyclerView} must be visible
     */
    protected abstract boolean isItemDecorationVisible();

    /**
     * Notify when the end of the page is reached
     */
    protected abstract void reachToLastPage();

    public abstract void onSorted(List<T> items);

    /**
     * The information should be refreshed whenever the RecyclerFragment is created or
     * the user refresh the contents of a view via a vertical swipe gesture.
     *
     * <P> Must.
     * This method have to be called in override onViewCreated{@link #onViewCreated} method
     * to get the information from server.
     * </P>
     *
     * <p>
     * Be refreshed as a result of the gesture in case a vertical swipe gesture.
     * The information must be provided to allow refresh of the content wherever this gesture
     * is used in case of a vertical swipe gesture.
     * </p>
     *
     */
    protected void refresh(boolean refreshed) {
        Log.i(TAG, "refresh");

        if (refreshed) {
            mSwipeLayout.post(() -> {
                mSwipeLayout.setRefreshing(true);
                request(true);
            });
        }
    }

    protected void scrolledReachToLast() {
        Log.i(TAG, "scrolledReachToLast");

        if (!mIsLoading) {
            mIsLoading = true;
            if (mBaseArrayAdapter != null) {
                mBaseArrayAdapter.setLoadingItems(true);
            }

            mCurrentPage++;
        }
    }

    @SuppressWarnings("unused")
    protected void clear() {
        mCurrentPage = FIRST_PAGE;

        if (mItems != null && mItems.size() > 0) {
            mItems.clear();
        }
    }

    /**
     * Notify that data-parsing processing is completed.
     */
    public void doneRefreshing() {
        mSwipeLayout.setRefreshing(false);

        mIsLoading = false;
        mIsUpdated = false;

        if (mBaseArrayAdapter != null) {
            mBaseArrayAdapter.setLoadingItems(false);
        }
    }

    /**
     * Sets the listener to be notified when a process is completed
     */
    public void setOnProcessListener(OnProcessListener listener) {
        mListener = listener;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public SwipyRefreshLayout getRefreshLayout() {
        return mSwipeLayout;
    }

    /**
     * Gets current page
     *
     * @return current page
     */
    @SuppressWarnings("unused")
    public int getCurrentPage() {
        return mCurrentPage;
    }

    /**
     * Classes that wish to be notified when a process is completed should implement this interfaces.
     * An OnProcessListener allows the application to intercept the process events.
     *
     * <p>This can be useful for applications that wish to implement some module
     * after the process is done.
     * /p>
     */
    public interface OnProcessListener {
        /**
         * This listener method to be invoked when the RecyclerView has been scrolled to the last.
         * This will be called after the scroll has completed.
         * <p>
         * This listener method will also be called if visible item range changes after a layout
         * calculation. In that case, dx and dy will be 0.
         *
         * @param recyclerView The RecyclerView which scrolled.
         * @param dx The amount of horizontal scroll.
         * @param dy The amount of vertical scroll.
         */
        void onScrolledToLast(RecyclerView recyclerView, int dx, int dy);

        /**
         * This listener method to be invoked on scrolling.
         */
        void onScrolling();

        /**
         * This listener method to be invoked when scrolling is done.
         */
        void onScrolled(RecyclerView recyclerView, int dx, int dy);

        @SuppressWarnings("unused")
        void onError(String message);
    }
}
