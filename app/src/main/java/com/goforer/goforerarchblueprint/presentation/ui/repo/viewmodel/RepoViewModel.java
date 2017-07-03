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

package com.goforer.goforerarchblueprint.presentation.ui.repo.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;

import com.goforer.goforerarchblueprint.domain.network.loader.RepoLoader;
import com.goforer.goforerarchblueprint.repository.network.response.Resource;
import com.goforer.goforerarchblueprint.repository.model.data.AbsentLiveData;
import com.goforer.goforerarchblueprint.repository.model.data.entity.Repo;

import java.util.List;

import javax.inject.Inject;

public class RepoViewModel extends ViewModel {
    @VisibleForTesting
    private final MutableLiveData<String> mLiveUserName;

    private final LiveData<Resource<List<Repo>>> mRepos;

    private final RepoLoader mLoader;

    @Inject
    RepoViewModel(RepoLoader loader) {
        mLoader = loader;
        mLiveUserName = new MutableLiveData<>();
        mRepos = Transformations.switchMap(mLiveUserName, userName -> {
            if (userName == null || userName.isEmpty()) {
                return AbsentLiveData.create();
            }

            return loader.load(userName);
        });
    }

    public LiveData<Resource<List<Repo>>> getRepos() {
        return mRepos;
    }

    public LiveData<Resource<List<Repo>>> getNextRepos(int uid, int page) {
        return mLoader.loadNext(uid, page);
    }

    public void setUserName(String userName) {
        mLiveUserName.setValue(userName);
    }
}
