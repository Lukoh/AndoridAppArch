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

package com.goforer.goforerarchblueprint.presentation.ui.splash;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.goforer.goforerarchblueprint.R;
import com.goforer.goforerarchblueprint.di.Injectable;
import com.goforer.goforerarchblueprint.presentation.caller.Caller;
import com.goforer.goforerarchblueprint.presentation.ui.splash.viewmodel.UserViewModel;
import com.goforer.goforerarchblueprint.presentation.ui.splash.viewmodel.factory.UserViewModelFactory;
import com.goforer.goforerarchblueprint.presentation.util.AutoClearedValue;

import javax.inject.Inject;

public class SplashFragment extends Fragment implements LifecycleRegistryOwner, Injectable {
    private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

    @Inject
    UserViewModelFactory mUserViewModelFactory;

    @Override
    public LifecycleRegistry getLifecycle() {
        return lifecycleRegistry;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        AutoClearedValue<View> acvView = new AutoClearedValue<>(this,
                inflater.inflate(R.layout.fragment_splash, container, false));
        return acvView.get().getRootView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        UserViewModel userViewModel = ViewModelProviders.of(this, mUserViewModelFactory)
                                                    .get(UserViewModel.class);
        userViewModel.setUserName(SplashActivity.USER_NAME);
        userViewModel.getUser().observe(this, userResource -> {
            if (userResource != null && userResource.data != null) {
                moveToMain();
            }
        });
    }

    public void moveToMain() {
        Caller.INSTANCE.callRepository(this.getActivity());
        this.getActivity().finish();
    }
}