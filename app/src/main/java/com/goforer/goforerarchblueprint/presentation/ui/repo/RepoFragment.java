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

package com.goforer.goforerarchblueprint.presentation.ui.repo;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.goforer.base.presentation.utils.CommonUtils;
import com.goforer.base.presentation.view.decoration.RemoverItemDecoration;
import com.goforer.base.presentation.view.fragment.RecyclerFragment;
import com.goforer.base.presentation.view.helper.RecyclerItemTouchHelperCallback;
import com.goforer.goforerarchblueprint.R;
import com.goforer.goforerarchblueprint.presentation.ui.repo.adapter.RepoAdapter;
import com.goforer.goforerarchblueprint.presentation.ui.repo.viewmodel.RepoViewModel;
import com.goforer.goforerarchblueprint.presentation.ui.splash.SplashActivity;
import com.goforer.goforerarchblueprint.presentation.ui.splash.viewmodel.UserViewModel;
import com.goforer.goforerarchblueprint.presentation.ui.view.SlidingDrawer;
import com.goforer.goforerarchblueprint.presentation.ui.repo.viewmodel.factory.RepoViewModelFactory;
import com.goforer.goforerarchblueprint.presentation.ui.splash.viewmodel.factory.UserViewModelFactory;
import com.goforer.goforerarchblueprint.presentation.util.AutoClearedValue;
import com.goforer.goforerarchblueprint.repository.network.response.Resource;
import com.goforer.goforerarchblueprint.repository.model.data.entity.Repo;
import com.goforer.goforerarchblueprint.repository.model.data.entity.User;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

import static com.goforer.goforerarchblueprint.repository.network.response.Status.ERROR;
import static com.goforer.goforerarchblueprint.repository.network.response.Status.SUCCESS;

public class RepoFragment extends RecyclerFragment<Repo> {
    private static final String TAG = "RepoFragment";

    private RepoAdapter mAdapter;

    private SlidingDrawer<User> mSlidingDrawer;

    private User mUser;

    private RepoViewModel mRepoViewModel;

    private AutoClearedValue<RepoAdapter> mACVAdapter;

    @Inject
    UserViewModelFactory mUserViewModelFactory;

    @Inject
    RepoViewModelFactory mRepoViewModelFactory;

    @BindView(R.id.tv_noresult)
    TextView mNoResultText;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        AutoClearedValue<View> acvView = new AutoClearedValue<>(this,
                inflater.inflate(R.layout.fragment_respository_list, container, false));
        return acvView.get().getRootView();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setItemHasFixedSize(true);
        refresh(true);

        UserViewModel userViewModel
                = ViewModelProviders.of(this, mUserViewModelFactory)
                                    .get(UserViewModel.class);
        userViewModel.setUserName(SplashActivity.USER_NAME);
        userViewModel.getUser().observe(this, (Resource<User> userResource) -> {
            if (userResource != null && userResource.getData() != null
                    && userResource.getStatus().equals(SUCCESS)) {
                mUser = userResource.getData();
                ActionBar actionBar = this.getBaseActivity().getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_USE_LOGO);
                    actionBar.setElevation(1);
                    actionBar.setTitle(userResource.getData().getName() + "'s " + getString(R.string.repository));
                    actionBar.setDisplayShowTitleEnabled(true);
                    actionBar.setDisplayHomeAsUpEnabled(false);
                    actionBar.setHomeButtonEnabled(true);
                }

                mSlidingDrawer = new SlidingDrawer<>(getContext(), savedInstanceState);
                mSlidingDrawer.setRootViewRes(R.id.drawer_container);
                mSlidingDrawer.setType(SlidingDrawer.DRAWER_PROFILE_TYPE);
                mSlidingDrawer.setDrawerInfo(userResource.getData());
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = mSlidingDrawer.getDrawer().saveInstanceState(outState);
        outState = mSlidingDrawer.getDrawerHeader().saveInstanceState(outState);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mSlidingDrawer != null) {
            mSlidingDrawer.setDrawerInfo(mUser);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onSorted(List<Repo> items) {
        mACVAdapter.get().addItems(items, true);
    }

    @Override
    public void onSearched(List<Repo> items) {

    }

    @Override
    public void onFirstVisibleItem(int position) {
        // To Do::Implement playing the video file with position in case of video item
        Log.i(TAG, "onFirstVisibleItem : " + position);
    }

    @Override
    public void onLastVisibleItem(int position) {
        // To Do::Implement playing the video file with position in case of video item
        Log.i(TAG, "onLastVisibleItem : " + position);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected RecyclerView.LayoutManager createLayoutManager() {
        super.setOnProcessListener(new RecyclerFragment.OnProcessListener() {
            @Override
            public void onScrolledToLast(RecyclerView recyclerView, int dx, int dy) {
                Log.i(TAG, "onScrolledToLast");
            }

            @Override
            public void onScrolling() {
                Log.i(TAG, "onScrolling");
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                Log.i(TAG, "onScrolled");

            }

            @Override
            public void onError(String message) {
                CommonUtils.showToastMessage(mContext, message, Toast.LENGTH_SHORT);
            }
        });

        return new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
    }

    @Override
    protected RecyclerView.ItemDecoration createItemDecoration() {
        return new RemoverItemDecoration(Color.BLUE);
    }

    @Override
    protected RecyclerView.Adapter createAdapter() {
        mAdapter = new RepoAdapter(getContext(), this);
        mACVAdapter = new AutoClearedValue<>(this, mAdapter);
        mAdapter.setEnableLoadingImage(true);
        getRecyclerView().setAdapter(mAdapter);

        return mAdapter;
    }

    @Override
    protected ItemTouchHelper.Callback createItemTouchHelper() {
        return new RecyclerItemTouchHelperCallback(getContext(), mAdapter, Color.BLUE);
    }

    @Override
    protected boolean isItemDecorationVisible() {
        return true;
    }

    @Override
    protected void requestData(boolean isNew) {
        mNoResultText.setVisibility(View.GONE);

        try {
            requestRepositoryList(isNew);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "requestData");
    }

    @Override
    protected void reachToEndPage(int page) {
        mRepoViewModel.getNextRepos(mUser.getId(), page).observe(this, repoListResource -> {
            if (repoListResource != null && repoListResource.getData() != null
                    && repoListResource.getStatus().equals(SUCCESS)) {
                if (repoListResource.getData().size() > 0) {
                    mACVAdapter.get().addItems(repoListResource.getData(), false);
                } else {
                    if (repoListResource.getMessage() != null) {
                        mSwipeLayout.setVisibility(View.GONE);
                        mNoResultText.setText(repoListResource.getMessage());
                        mNoResultText.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                if (repoListResource != null && repoListResource.getStatus().equals(ERROR)) {
                    mSwipeLayout.setVisibility(View.GONE);
                    mNoResultText.setText(repoListResource.getMessage());
                    mNoResultText.setVisibility(View.VISIBLE);
                }
            }

            stopLoading(STOP_LOADING_TIME0UT);
        });
    }

    @Override
    protected void updateData() {
        /*
         * Please put some module to update new data here, instead of doneRefreshing() method if
         * there is some data to be updated from the backend side.
         * I just put doneRefreshing() method because there is no data to be updated from
         * the backend side in this app-architecture project.
         */
        stopRefreshing();

        Log.i(TAG, "updateData");
    }

    @Override
    protected void reachToLastPage() {
        ((RepoActivity)getActivity()).setMenuVisible(true);
    }

    private void requestRepositoryList(final boolean isNew)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        mRepoViewModel = ViewModelProviders.of(this, mRepoViewModelFactory)
                                            .get(RepoViewModel.class);

        if (!isNew) {
            mRepoViewModel.setUserName(SplashActivity.USER_NAME);
            mRepoViewModel.getRepos().observe(this, repoListResource -> {
                if (repoListResource != null && repoListResource.getData() != null
                        && repoListResource.getStatus().equals(SUCCESS)) {
                    mSwipeLayout.setVisibility(View.VISIBLE);
                    if (repoListResource.getData().size() > 0) {
                        setTotalPage(repoListResource.getLastPage());
                        mACVAdapter.get().addItems(repoListResource.getData(), false);
                        stopLoading(STOP_REFRESHING_TIMEOUT);
                    } else {
                        if (repoListResource.getMessage() != null) {
                            stopLoading(STOP_REFRESHING_TIMEOUT);
                            mSwipeLayout.setVisibility(View.GONE);
                            mNoResultText.setText(repoListResource.getMessage());
                            mNoResultText.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    if (repoListResource != null && repoListResource.getStatus().equals(ERROR)) {
                        stopLoading(STOP_REFRESHING_TIMEOUT);
                        mSwipeLayout.setVisibility(View.GONE);
                        mNoResultText.setText(repoListResource.getMessage());
                        mNoResultText.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    public RepoAdapter getAdapter() {
        return mAdapter;
    }
}

