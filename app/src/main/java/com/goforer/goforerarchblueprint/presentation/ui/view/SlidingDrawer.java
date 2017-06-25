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

package com.goforer.goforerarchblueprint.presentation.ui.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.goforer.goforerarchblueprint.R;
import com.goforer.goforerarchblueprint.presentation.ui.view.drawer.item.CustomCountPanelDrawableItem;
import com.goforer.base.presentation.view.activity.BaseActivity;
import com.goforer.goforerarchblueprint.repository.model.data.User;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import javax.inject.Inject;

public class SlidingDrawer<T> {
    public static final int DRAWER_PROFILE_TYPE = 0;

    private static final int CUSTOM_ITEM_FOLLOWER_TYPE = 0;
    private static final int CUSTOM_ITEM_FOLLOWING_TYPE = 1;
    private static final int CUSTOM_ITEM_REPOSITORY_TYPE = 2;

    private static final int DRAWER_PROFILE_ITEM_IDENTIFIER_ID = 1;
    private static final int DRAWER_PROFILE_ITEM_FOLLOWER_ID = DRAWER_PROFILE_ITEM_IDENTIFIER_ID;
    private static final int DRAWER_PROFILE_ITEM_FOLLOWING_ID
            = DRAWER_PROFILE_ITEM_IDENTIFIER_ID + 1;
    private static final int DRAWER_PROFILE_ITEM_REPOSITORY_COUNT_ID
            = DRAWER_PROFILE_ITEM_IDENTIFIER_ID + 2;

    private static final int SECONDARY_DRAWER_LEVEL = 2;

    private AccountHeader mHeader = null;
    private Drawer mDrawer = null;
    private User mUser;
    private Context mContext;
    private BaseActivity mActivity;
    private Bundle mBundle;

    private int mType;
    private int mRootViewRes;

    @Inject
    public SlidingDrawer(final Context context, final Bundle bundle) {
        mContext = context;
        mActivity = (BaseActivity) context;
        mBundle = bundle;
    }

    public void setRootViewRes(final int rootViewRes) {
        mRootViewRes = rootViewRes;
    }

    public void setType(final int type) {
        mType = type;
    }

    public Drawer getDrawer() {
        return mDrawer;
    }

    public AccountHeader getDrawerHeader() {
        return mHeader;
    }

    public void setDrawerInfo(T info) {
        if (info instanceof User) {
            mUser = (User)info;
        }

        setDrawer(mType);
    }

    private void setDrawer(int type) {
        switch (type) {
            case DRAWER_PROFILE_TYPE:
                mDrawer = createProfileDrawer(mActivity, mRootViewRes, mBundle);
                break;
            default:
        }
    }

    private void buildHeader(final Activity activity, IProfile<ProfileDrawerItem> profile, Bundle savedInstanceState) {
        // Create the AccountHeader
        mHeader = new AccountHeaderBuilder()
                .withActivity(activity)
                .withTranslucentStatusBar(true)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        profile
                )
                .withOnAccountHeaderListener((view, profile1, currentProfile) -> false)
                .withProfileImagesVisible(true)
                .withSavedInstance(savedInstanceState)
                .build();
    }

    private Drawer createProfileDrawer(final BaseActivity activity, @IdRes int rootViewRes,
                                       @Nullable Bundle savedInstanceState) {
        Toolbar toolbar = (Toolbar) mActivity.findViewById(R.id.toolbar);
        IProfile<ProfileDrawerItem> profile = null;

        if (mHeader == null) {
            profile = new ProfileDrawerItem()
                    .withName(mUser.getName())
                    .withEmail(mUser.getEmail())
                    .withIcon(mUser.getAvatarUrl())
                    .withIdentifier(DRAWER_PROFILE_ITEM_IDENTIFIER_ID);

            buildHeader(activity, profile, savedInstanceState);
        }

        assert toolbar != null;
        mDrawer = new DrawerBuilder()
                .withActivity(mActivity)
                .withRootView(rootViewRes)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withActionBarDrawerToggleAnimated(true)
                .withAccountHeader(mHeader) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_company)
                                .withDescription(mUser.getCompany())
                                .withIcon(R.drawable.ic_drawer_company)
                                .withSelectable(false),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_location)
                                .withDescription(mUser.getLocation())
                                .withIcon(R.drawable.ic_drawer_location)
                                .withSelectable(false),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_blog)
                                .withDescription(mUser.getBlog())
                                .withIcon(R.drawable.ic_drawer_blog)
                                .withSelectable(false),
                        createExpandableDrawerItem(R.drawable.ic_drawer_followers,
                                CUSTOM_ITEM_FOLLOWER_TYPE,
                                mActivity.getResources().getString(R.string.drawer_item_followers),
                                mUser.getFollowers(), SECONDARY_DRAWER_LEVEL),
                        createExpandableDrawerItem(R.drawable.ic_drawer_star,
                                CUSTOM_ITEM_FOLLOWING_TYPE,
                                mActivity.getResources().getString(R.string.drawer_item_following),
                                mUser.getFollowing(), SECONDARY_DRAWER_LEVEL),
                        createExpandableDrawerItem(R.drawable.ic_drawer_repository_count,
                                CUSTOM_ITEM_REPOSITORY_TYPE,
                                mActivity.getResources().getString(R.string.drawer_item_repository_count),
                                mUser.getPublicRepos(), SECONDARY_DRAWER_LEVEL)
                ) // Add the items we want to use with our SlidingDr
                .withOnDrawerNavigationListener(clickedView -> {
                    // This method is only called if the Arrow icon is shown.
                    // The hamburger is automatically managed by the MaterialDrawer if the back
                    // arrow is shown. Close the activity
                    mActivity.finish();
                    // Return true if we have consumed the event
                    return true;
                })
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                    }

                    @SuppressWarnings("unchecked")
                    @Override
                    public void onDrawerClosed(View drawerView) {
                        setDrawerInfo((T)mUser);
                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {

                    }
                })
                .withSavedInstance(savedInstanceState)
                .withOnDrawerItemClickListener((view, position, drawerItem) -> false)
                .buildForFragment();

        if ((savedInstanceState == null) && (profile != null)) {
            mHeader.setActiveProfile(profile);
        }

        mDrawer.getDrawerLayout().setFitsSystemWindows(false);
        mDrawer.getSlider().setFitsSystemWindows(false);

        return mDrawer;
    }

    private CustomCountPanelDrawableItem createExpandableDrawerItem(
            int iconRes, int type, String itemName, int count, int level) {
        CustomCountPanelDrawableItem drawableItem = new CustomCountPanelDrawableItem();
        setDrawerTypeItem(drawableItem, iconRes, type, itemName, count, level);

        return drawableItem;
    }

    private void setDrawerTypeItem(CustomCountPanelDrawableItem drawerItem, int iconRes, int type,
                                   String itemName, int count, int level) {
        switch (type) {
            case CUSTOM_ITEM_FOLLOWER_TYPE:
                drawerItem.withName(itemName)
                        .withCount(String.valueOf(count))
                        .withCountTextColor(ContextCompat.getColor(mContext, R.color.whiteLight))
                        .withIcon(iconRes)
                        .withIdentifier(DRAWER_PROFILE_ITEM_FOLLOWER_ID)
                        .withArrowVisible(false)
                        .withLevel(level)
                        .withSelectable(false);
                break;
            case CUSTOM_ITEM_FOLLOWING_TYPE:
                drawerItem.withName(itemName)
                        .withCount(String.valueOf(count))
                        .withCountTextColor(ContextCompat.getColor(mContext, R.color.whiteLight))
                        .withIcon(iconRes)
                        .withIdentifier(DRAWER_PROFILE_ITEM_FOLLOWING_ID)
                        .withArrowVisible(false)
                        .withLevel(level)
                        .withSelectable(false);
                break;
            case CUSTOM_ITEM_REPOSITORY_TYPE:
                drawerItem.withName(itemName)
                        .withCount(String.valueOf(count))
                        .withCountTextColor(ContextCompat.getColor(mContext, R.color.whiteLight))
                        .withIcon(iconRes)
                        .withIdentifier(DRAWER_PROFILE_ITEM_REPOSITORY_COUNT_ID)
                        .withArrowVisible(false)
                        .withLevel(level)
                        .withSelectable(false);
                break;
            default:
        }
    }
}
