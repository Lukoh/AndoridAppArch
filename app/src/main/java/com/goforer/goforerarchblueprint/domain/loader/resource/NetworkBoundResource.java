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
import com.goforer.goforerarchblueprint.repository.network.response.ApiResponse;
import com.goforer.goforerarchblueprint.repository.network.response.Resource;

/**
 * A generic class that can provide a resource backed by the network.
 * <p>
 * You can read more about it in the <a href="https://developer.android.com/arch">Architecture
 * Guide</a>.
 * @param <ResultType>
 * @param <RequestType>
 */
public abstract class NetworkBoundResource<ResultType, RequestType> {
    @SuppressWarnings("unused")
    public static final int BOUND_TO_BACKEND = 0;
    public static final int BOUND_FROM_BACKEND = 1;

    private final AppExecutors mAppExecutors;

    private final MediatorLiveData<Resource<ResultType>> mResult = new MediatorLiveData<>();

    @MainThread
    protected NetworkBoundResource(AppExecutors appExecutors, int boundType) {
        mAppExecutors = appExecutors;
        if (boundType == BOUND_FROM_BACKEND) {
            mResult.setValue(Resource.loading(null));
            LiveData<ResultType> dbSource = loadFromCache();
            mResult.addSource(dbSource, data -> {
                mResult.removeSource(dbSource);
                if (shouldFetch(data)) {
                    fetchFromNetwork(dbSource);
                } else {
                    mResult.addSource(dbSource, updatedData -> mResult.setValue(
                            Resource.success(updatedData)));
                }
            });
        } else {
            // TO BE::Implemented code to send something
        }
    }

    private void fetchFromNetwork(final LiveData<ResultType> dbSource) {
        LiveData<ApiResponse<RequestType>> apiResponse = loadFromNetwork();
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        mResult.addSource(dbSource, newData -> mResult.setValue(Resource.loading(newData)));
        mResult.addSource(apiResponse, response -> {
            mResult.removeSource(apiResponse);
            mResult.removeSource(dbSource);
            //no inspection ConstantConditions
            if (response != null && response.isSuccessful()) {
                mAppExecutors.diskIO().execute(() -> {
                    saveResponse(processResponse(response));
                    mAppExecutors.mainThread().execute(() ->
                            // we specially request a new live data,
                            // otherwise we will get immediately last cached value,
                            // which may not be updated with latest results received from network.
                            mResult.addSource(loadFromCache(),
                                    newData -> mResult.setValue(Resource.success(newData)))
                    );
                });
            } else {
                onFetchFailed();
                mResult.addSource(dbSource,
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
    protected RequestType processResponse(ApiResponse<RequestType> response) {
        return response.body;
    }

    @WorkerThread
    protected abstract void saveResponse(@NonNull RequestType item);

    @MainThread
    protected abstract boolean shouldFetch(@Nullable ResultType data);

    @NonNull
    @MainThread
    protected abstract LiveData<ResultType> loadFromCache();

    @NonNull
    @MainThread
    protected abstract LiveData<ApiResponse<RequestType>> loadFromNetwork();
}
