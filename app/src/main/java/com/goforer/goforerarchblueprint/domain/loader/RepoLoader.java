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

package com.goforer.goforerarchblueprint.domain.loader;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.goforer.goforerarchblueprint.domain.loader.resource.NetworkBoundResource;
import com.goforer.goforerarchblueprint.repository.model.cache.RepoDao;
import com.goforer.goforerarchblueprint.presentation.util.RateLimiter;
import com.goforer.goforerarchblueprint.repository.network.response.ApiResponse;
import com.goforer.goforerarchblueprint.repository.network.response.Resource;
import com.goforer.goforerarchblueprint.repository.model.data.Repo;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RepoLoader extends Loader<Resource<List<Repo>>> {
    private final RepoDao mRepoDao;

    private RateLimiter<String> repoListRateLimit = new RateLimiter<>(10, TimeUnit.MINUTES);

    @Inject
    RepoLoader(RepoDao repoDao) {
        mRepoDao = repoDao;
    }

    @Override
    public LiveData<Resource<List<Repo>>> load(String userName) {
        return new NetworkBoundResource<List<Repo>>(mAppExecutors, NetworkBoundResource.LOAD_FIRST,
                            NetworkBoundResource.BOUND_FROM_BACKEND) {
            @Override
            protected void saveToCache(@NonNull List<Repo> repos) {
                mRepoDao.insertRepos(repos);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Repo> data) {
                return data == null || data.isEmpty() || repoListRateLimit.shouldFetch(userName);
            }

            @NonNull
            @Override
            protected LiveData<List<Repo>> loadFromCache() {
                return mRepoDao.getRepo();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Repo>>> loadFromNetwork() {
                return mGithubService.getRepos(userName);
            }

            @Override
            protected void onFetchFailed() {
                repoListRateLimit.reset(userName);
            }

            @Override
            protected void clearCache() {
                mRepoDao.removeRepos();
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<Repo>>> loadNext(int uid, int page) {
        return new NetworkBoundResource<List<Repo>>(mAppExecutors, NetworkBoundResource.LOAD_NEXT,
                NetworkBoundResource.BOUND_FROM_BACKEND) {
            @Override
            protected void saveToCache(@NonNull List<Repo> repos) {
                mRepoDao.insertRepos(repos);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Repo> data) {
                return data == null || data.isEmpty();
            }

            @NonNull
            @Override
            protected LiveData<List<Repo>> loadFromCache() {
                return mRepoDao.getRepo();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Repo>>> loadFromNetwork() {
                return mGithubService.getNextRepos(uid, page);
            }

            @Override
            protected void onFetchFailed() {
                repoListRateLimit.reset(Integer.toString(uid));
            }

            @Override
            protected void clearCache() {
                mRepoDao.removeRepos();
            }
        }.asLiveData();
    }
}

