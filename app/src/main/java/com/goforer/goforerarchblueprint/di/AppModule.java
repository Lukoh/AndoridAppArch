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

package com.goforer.goforerarchblueprint.di;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;

import com.goforer.goforerarchblueprint.domain.sort.SortImpl;
import com.goforer.goforerarchblueprint.repository.model.cache.GithubCache;
import com.goforer.goforerarchblueprint.repository.model.cache.RepoDao;
import com.goforer.goforerarchblueprint.repository.model.cache.UserDao;
import com.goforer.goforerarchblueprint.presentation.ui.repo.viewmodel.factory.RepoViewModelFactory;
import com.goforer.goforerarchblueprint.presentation.ui.splash.viewmodel.factory.UserViewModelFactory;
import com.goforer.goforerarchblueprint.repository.network.api.GithubService;
import com.goforer.goforerarchblueprint.repository.network.factory.LiveDataCallAdapterFactory;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A module for Android-specific dependencies which require a {@link Context} or
 * {@link android.app.Application} to create.
 */
@Module(subcomponents = {
        UserViewModelSubComponent.class,
        RepoViewModelSubComponent.class
        })
public class AppModule {
    private static final long READ_TIME_OUT = 5;
    private static final long WRITE_TIME_OUT = 5;
    private static final long CONNECT_TIME_OUT = 5;

    private static final String BASE_URL = "https://api.github.com";

    private static String mRawResponseBody;

    @Singleton @Provides
    GithubService provideGithubService() {
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder()
                .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
                .connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS);

        okHttpClient.addInterceptor(chain -> {
            Request original = chain.request();

            Request request = original.newBuilder()
                    .header("Accept", "application/vnd.github.v3+json")
                    .header("Connection", "keep-alive")
                    .method(original.method(), original.body())
                    .build();

            Response response = chain.proceed(request);

            mRawResponseBody = response.body().string();

            return response.newBuilder()
                    .body(ResponseBody.create(response.body().contentType(),
                            mRawResponseBody)).build();
        });

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build()
                .create(GithubService.class);
    }

    @Singleton @Provides
    GithubCache provideCache(Application app) {
        return Room.databaseBuilder(app, GithubCache.class,"arch_github.db").build();
    }

    @Singleton @Provides
    UserDao provideUserDao(GithubCache cache) {
        return cache.userDao();
    }

    @Singleton @Provides
    RepoDao provideRepoDao(GithubCache cache) {
        return cache.repoDao();
    }

    @SuppressWarnings("unused")
    public static String getRawResponseBody() {
        return mRawResponseBody;
    }

    @Singleton @Provides
    UserViewModelFactory provideUserViewModelFactory(
            UserViewModelSubComponent.Builder viewModelSubComponent) {
        return new UserViewModelFactory(viewModelSubComponent.build());
    }

    @Singleton @Provides
    RepoViewModelFactory provideRepoViewModelFactory(
            RepoViewModelSubComponent.Builder viewModelSubComponent) {
        return new RepoViewModelFactory(viewModelSubComponent.build());
    }

    @Singleton @Provides
    SortImpl providesSort() {
        return new SortImpl<>();
    }
}

