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

package com.goforer.goforerarchblueprint.presentation.ui.repo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.goforer.base.presentation.utils.CommonUtils;
import com.goforer.base.presentation.view.activity.BaseActivity;
import com.goforer.base.presentation.view.adatper.BaseListAdapter;
import com.goforer.base.presentation.view.fragment.RecyclerFragment;
import com.goforer.base.presentation.view.helper.ItemTouchHelperListener;
import com.goforer.base.presentation.view.holder.BaseViewHolder;
import com.goforer.base.presentation.view.holder.DefaultViewHolder;
import com.goforer.base.presentation.view.customs.SquircleImageView;
import com.goforer.goforerarchblueprint.GoforerArchBlueprint;
import com.goforer.goforerarchblueprint.R;
import com.goforer.goforerarchblueprint.presentation.caller.Caller;
import com.goforer.goforerarchblueprint.repository.model.data.entity.Repo;

import java.util.Collections;
import java.util.Objects;

public class RepoAdapter extends BaseListAdapter<Repo> implements ItemTouchHelperListener {
    private Context mContext;

    private RecyclerFragment mFragment;

    public RepoAdapter(Context context, RecyclerFragment fragment) {
        super(R.layout.list_repository_item);

        mContext = context;
        mFragment = fragment;
    }

    @Override
    public int getItemCount() {
        int count  = super.getItemCount();

        if (isReachedToLastPage()) {
            return count + 1;
        }

        return count;
    }

    @Override
    public int getItemViewType(int position) {
        int itemCount = getItemCount() - 1;

        if (isReachedToLastPage() && position == itemCount) {
            return VIEW_TYPE_FOOTER;
        } else if (position == itemCount) {
            return VIEW_TYPE_LOADING;
        }

        return VIEW_TYPE_ITEM;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        View view;

        switch (type) {
            case VIEW_TYPE_FOOTER:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_last_item,
                        viewGroup, false);
                return new DefaultViewHolder(view);
            case VIEW_TYPE_LOADING:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.list_loading_item, viewGroup, false);
                return new DefaultViewHolder(view);
            default:
                return super.onCreateViewHolder(viewGroup, type);
        }
    }

    @Override
    protected BaseViewHolder createViewHolder(ViewGroup viewGroup, View view, int type) {
        return new RepositoryViewHolder(view, ((BaseActivity)mContext).resumed());
    }

    @Override
    public void onBindViewHolder(final BaseViewHolder viewHolder, int position) {
        switch (getItemViewType(position)){
            case VIEW_TYPE_FOOTER:
            case VIEW_TYPE_LOADING:
                return;
            default:
                super.onBindViewHolder(viewHolder, position);
        }
    }

    @Override
    public void onItemDismiss(int position) {
        getItems().remove(position);
        notifyItemRemoved(position);
        notifyItemChanged(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(getItems(), fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        notifyItemChanged(toPosition);
        notifyItemChanged(fromPosition);

        return true;
    }

    @Override
    public void onItemDrag(int actionState) {
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            mFragment.getRefreshLayout().setRefreshing(false);
            mFragment.getRefreshLayout().setEnabled(false);
        } else if (actionState == ItemTouchHelper.ACTION_STATE_IDLE){
            mFragment.getRefreshLayout().setEnabled(true);
        }
    }

    @Override
    protected boolean areItemsTheSame(Repo oldItem, Repo newItem) {
        return Objects.equals(oldItem.getId(), newItem.getId());
    }

    @Override
    protected boolean areContentsTheSame(Repo oldItem, Repo newItem) {
        return Objects.equals(oldItem.getName(), newItem.getName())
                && Objects.equals(oldItem.getDescription(), newItem.getDescription());
    }

    @SuppressWarnings("unused")
    public void showError(String errorMessage) {
        CommonUtils.showToastMessage(mContext, errorMessage, Toast.LENGTH_SHORT);
        new Handler().postDelayed(GoforerArchBlueprint::closeApplication, Toast.LENGTH_SHORT);

    }

    public void setEnableLoadingImage(boolean usedLoadingImage) {
        setUsedLoadingImage(usedLoadingImage);
    }



    @SuppressWarnings("WeakerAccess")
    final static class RepositoryViewHolder extends BaseViewHolder<Repo> {
        private View mView;

        @SuppressWarnings("unused")
        private boolean mIsResumed;

        RepositoryViewHolder(View itemView, boolean isResumed) {
            super(itemView);

            mView = itemView;
            mIsResumed = isResumed;
        }

        @SuppressWarnings("ConstantConditions")
        @SuppressLint("SetTextI18n")
        @Override
        public void bindItemHolder(final BaseViewHolder holder, @NonNull final Repo repo,
                                   final int position) {
            holder.getView().setOnClickListener(view -> {
                if (repo.getHomepage() == null || "".equals(repo.getHomepage())) {
                    CommonUtils.showToastMessage(getContext(),
                            getContext().getString(R.string.no_homepage), Toast.LENGTH_SHORT);
                    return;
                }

                if (mIsResumed) {
                    Caller.INSTANCE.callChromeCustomTabs(getContext(), repo.getHomepage());
                }
            });

            ((TextView)holder.getView().findViewById(R.id.tv_name))
                    .setText(repo.getName());
            ((TextView)holder.getView().findViewById(R.id.tv_description))
                    .setText(repo.getDescription());
            ((TextView)holder.getView().findViewById(R.id.tv_count))
                    .setText(holder.getContext().getString(R.string.star_count)
                            +  "  " + String.valueOf(repo.getStars()));
            ((SquircleImageView)holder.getView().findViewById(R.id.iv_avatar))
                    .setImage(repo.getOwner().getAvatarUrl());
        }

        @Override
        public void onItemSelected() {
            mView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            mView.setBackgroundColor(0);
        }
    }

}

