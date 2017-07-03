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

package com.goforer.goforerarchblueprint.domain.network.loader;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.goforer.goforerarchblueprint.domain.network.resource.NetworkBoundResource;
import com.goforer.goforerarchblueprint.repository.model.cache.dao.UserDao;
import com.goforer.goforerarchblueprint.repository.network.response.ApiResponse;
import com.goforer.goforerarchblueprint.repository.network.response.Resource;
import com.goforer.goforerarchblueprint.repository.model.data.AbsentLiveData;
import com.goforer.goforerarchblueprint.repository.model.data.entity.User;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Loader that handles User objects.
 */
@Singleton
public class UserLoader extends Loader<Resource<User>> {
    private final UserDao mUserDao;

    @Inject
    UserLoader(UserDao userDao) {
        mUserDao = userDao;
    }

    @Override
    public LiveData<Resource<User>> load(String userName) {
        return new NetworkBoundResource<User>(mAppExecutors, NetworkBoundResource.LOAD_FIRST,
                NetworkBoundResource.BOUND_FROM_BACKEND) {
            @Override
            protected void saveToCache(@NonNull User user) {
                mUserDao.insert(user);
            }

            @Override
            protected boolean shouldFetch(@Nullable User user) {
                return user == null;
            }

            @NonNull
            @Override
            protected LiveData<User> loadFromCache() {
                return Transformations.switchMap(mUserDao.getUser(), user -> {
                    if (user == null) {
                        return AbsentLiveData.create();
                    } else {
                        return mUserDao.getUser();
                    }
                });
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<User>> loadFromNetwork() {
                return mGithubService.getUser(userName);
            }

            @Override
            protected void clearCache() {
                mUserDao.removeUser();
            }
        }.asLiveData();
    }
}
