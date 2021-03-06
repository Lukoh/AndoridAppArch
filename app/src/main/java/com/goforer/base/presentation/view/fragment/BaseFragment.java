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

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.goforer.base.presentation.view.activity.BaseActivity;
import com.goforer.base.presentation.view.di.Injectable;

import butterknife.ButterKnife;

public class BaseFragment extends Fragment implements LifecycleRegistryOwner, Injectable {
    protected BaseActivity mActivity;
    protected Context mContext;

    private final LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);

    @Override
    public LifecycleRegistry getLifecycle() {
        return mLifecycleRegistry;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mActivity = (BaseActivity)context;
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @SuppressWarnings("unused")
    public BaseActivity getBaseActivity() {
        return mActivity;
    }

    public Context getContext() {
        return mContext;
    }
}
