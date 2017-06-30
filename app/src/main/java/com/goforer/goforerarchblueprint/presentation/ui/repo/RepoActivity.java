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

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.goforer.base.presentation.view.activity.BaseActivity;
import com.goforer.base.presentation.view.fragment.RecyclerFragment;
import com.goforer.goforerarchblueprint.R;
import com.goforer.goforerarchblueprint.domain.sort.SortImpl;
import com.goforer.goforerarchblueprint.domain.sort.comparator.RepoNameComparator;
import com.goforer.goforerarchblueprint.domain.sort.comparator.RepoStarComparator;
import com.goforer.goforerarchblueprint.presentation.caller.Caller;
import com.goforer.goforerarchblueprint.repository.model.data.Repo;

import java.lang.reflect.Method;

import javax.inject.Inject;

import butterknife.BindView;

public class RepoActivity extends BaseActivity {
    private MenuItem mMenuItemSortName;
    private MenuItem mMenuItemSortStar;

    @Inject
    SortImpl<Repo> mSort;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.iv_disconnect)
    ImageView mDisconnectImage;
    @BindView(R.id.tv_notice1)
    TextView mNoticeText1;
    @BindView(R.id.tv_notice2)
    TextView mNoticeText2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isNetworkAvailable()) {
            mDisconnectImage.setVisibility(View.VISIBLE);
            mNoticeText1.setVisibility(View.VISIBLE);
            mNoticeText2.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Caller.INSTANCE.unBindService(this);
    }

    @Override
    protected void setViews(Bundle savedInstanceState) {
        transactFragment(RepoFragment.class, R.id.content_holder, null);
    }

    @Override
    protected void setActionBar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_USE_LOGO);
            actionBar.setElevation(1);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_base);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fragment_repo_menu, menu);
        mMenuItemSortName = menu.findItem(R.id.sort_by_name);
        mMenuItemSortStar = menu.findItem(R.id.sort_by_star);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (NoSuchMethodException e) {
                    System.err.println(e.getMessage());
                    e.printStackTrace();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return super.onPrepareOptionsPanel(view, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.sort_by_name:
                mSort.setFragment((RecyclerFragment) this.getFragment(RepoFragment.class));
                mSort.sort(((RepoFragment)this.getFragment(RepoFragment.class))
                                              .getAdapter().getItems(), new RepoNameComparator());

                return true;
            case R.id.sort_by_star:
                mSort.setFragment((RecyclerFragment) this.getFragment(RepoFragment.class));
                mSort.sort(((RepoFragment)this.getFragment(RepoFragment.class))
                                              .getAdapter().getItems(), new RepoStarComparator());

                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    public void setMenuVisible(boolean visible) {
        mMenuItemSortName.setVisible(visible);
        mMenuItemSortStar.setVisible(visible);
    }
}

