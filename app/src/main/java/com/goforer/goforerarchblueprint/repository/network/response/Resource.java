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

package com.goforer.goforerarchblueprint.repository.network.response;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
public class Resource<T> {

    @NonNull
    private final Status mStatus;

    @Nullable
    private final String mMessage;

    @Nullable
    private final T mData;

    private final int mLastPage;

    private Resource(@NonNull Status status, @Nullable T data, @Nullable String message, int lastPage) {
        mStatus = status;
        mData = data;
        mMessage = message;
        mLastPage = lastPage;
    }

    public static <T> Resource<T> success(@Nullable T data, int lastPage) {
        return new Resource<>(Status.SUCCESS, data, null, lastPage);
    }

    public static <T> Resource<T> error(String msg, @Nullable T data) {
        return new Resource<>(Status.ERROR, data, msg, 0);
    }

    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(Status.LOADING, data, null, 0);
    }

    public Status getStatus() {
        return mStatus;
    }

    public String getMessage() {
        return mMessage;
    }

    public T getData() {
        return mData;
    }

    public int getLastPage() {
        return mLastPage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Resource<?> resource = (Resource<?>) o;

        if (mStatus != resource.mStatus) {
            return false;
        }

        if (mMessage != null ? mMessage.equals(resource.mMessage) : resource.mMessage == null)
            if (mData != null ? mData.equals(resource.mData) : resource.mData == null) return true;
        return false;

    }

    @Override
    public int hashCode() {
        int result = mStatus.hashCode();
        result = 31 * result + (mMessage != null ? mMessage.hashCode() : 0);
        result = 31 * result + (mData != null ? mData.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Resource{" +
                "status=" + mStatus +
                ", message='" + mMessage + '\'' +
                ", data=" + mData +
                '}';
    }
}
