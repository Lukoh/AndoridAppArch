/*
 * Copyright (C) 2017 The Android Open Source Project
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

package com.goforer.goforerarchblueprint.domain.loader.resource;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.goforer.goforerarchblueprint.AppExecutors;
import com.goforer.goforerarchblueprint.repository.model.data.AbsentLiveData;
import com.goforer.goforerarchblueprint.repository.network.response.ApiResponse;
import com.goforer.goforerarchblueprint.repository.network.response.Resource;

/**
 * A generic class that can provide a resource backed by the network.
 * <p>
 * You can read more about it in the <a href="https://developer.android.com/arch">Architecture
 * Guide</a>.
 * @param <ResultType>
 */
public abstract class NetworkBoundResource<ResultType> {
    @SuppressWarnings("unused")
    public static final int BOUND_TO_BACKEND = 0;
    public static final int BOUND_FROM_BACKEND = 1;

    public static final int LOAD_FIRST = 0;
    public static final int LOAD_NEXT = 1;
    // This LOAD_UPDATE is not used in this AppArchitecture project.
    // But in case of updating data from back-end server, this static variable could be used.
    @SuppressWarnings("unused")
    public static final int LOAD_UPDATE = 2;


    private final AppExecutors mAppExecutors;

    private final MediatorLiveData<Resource<ResultType>> mResult = new MediatorLiveData<>();

    @MainThread
    protected NetworkBoundResource(AppExecutors appExecutors, int loadType, int boundType) {
        mAppExecutors = appExecutors;
        if (boundType == BOUND_FROM_BACKEND) {
            mResult.setValue(Resource.loading(null));
            LiveData<ResultType> cacheSource;
            switch (loadType) {
                case LOAD_FIRST:
                    cacheSource = loadFromCache();
                    break;
                case LOAD_NEXT:
                case LOAD_UPDATE:
                default:
                    cacheSource = AbsentLiveData.create();
                    break;
            }

            mResult.addSource(cacheSource, data -> {
                mResult.removeSource(cacheSource);
                if (shouldFetch(data)) {
                    fetchFromNetwork(cacheSource, loadType);
                } else {
                    mResult.addSource(cacheSource, updatedData -> mResult.setValue(
                            Resource.success(updatedData, 0)));
                }
            });
        } else {
            // TO BE::Implemented code to send something
        }
    }

    private void fetchFromNetwork(final LiveData<ResultType> cacheSource, int loadType) {
        LiveData<ApiResponse<ResultType>> apiResponse = loadFromNetwork();
        // we re-attach cacheSource as a new source, it will dispatch its latest value quickly
        mResult.addSource(cacheSource, newData -> mResult.setValue(Resource.loading(newData)));
        mResult.addSource(apiResponse, response -> {
            mResult.removeSource(apiResponse);
            mResult.removeSource(cacheSource);
            //no inspection ConstantConditions
            if (response != null && response.isSuccessful()) {
                mAppExecutors.diskIO().execute(() -> {
                    switch (loadType) {
                        case LOAD_FIRST:
                            clearCache();
                            saveToCache(processResponse(response));
                            mAppExecutors.mainThread().execute(() -> {
                                // we specially request a new live data,
                                // otherwise we will get immediately last cached value,
                                // which may not be updated with latest results received from network.
                                mResult.addSource(loadFromCache(),
                                        newData -> {
                                            if (response.links.size() > 0
                                                    && response.getLastPage() != null) {
                                                mResult.setValue(Resource.success(newData,
                                                        response.getLastPage()));
                                            } else {
                                                mResult.setValue(Resource.success(newData, 0));
                                            }
                                        }
                                );
                            });
                            break;
                        case LOAD_NEXT:
                        case LOAD_UPDATE:
                        default:
                            saveToCache(processResponse(response));
                            break;
                    }
                });
            } else {
                onFetchFailed();
                mResult.addSource(cacheSource,
                        newData -> mResult.setValue(Resource.error(response != null
                                ? response.errorMessage : null, newData)));
            }

        });
    }

    protected void onFetchFailed() {
    }

    public LiveData<Resource<ResultType>> asLiveData() {
        return mResult;
    }

    @WorkerThread
    protected ResultType processResponse(ApiResponse<ResultType> response) {
        return response.body;
    }

    @WorkerThread
    protected abstract void saveToCache(@NonNull ResultType item);

    @MainThread
    protected abstract boolean shouldFetch(@Nullable ResultType data);

    @NonNull
    @MainThread
    protected abstract LiveData<ResultType> loadFromCache();

    @NonNull
    @MainThread
    protected abstract LiveData<ApiResponse<ResultType>> loadFromNetwork();

    protected abstract void clearCache();
}
