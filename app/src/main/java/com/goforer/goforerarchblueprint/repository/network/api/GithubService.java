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

package com.goforer.goforerarchblueprint.repository.network.api;

import android.arch.lifecycle.LiveData;

import com.goforer.goforerarchblueprint.repository.network.response.ApiResponse;
import com.goforer.goforerarchblueprint.repository.model.data.Repo;
import com.goforer.goforerarchblueprint.repository.model.data.User;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GithubService {
    @GET("/users/{name}")
    LiveData<ApiResponse<User>> getUser(
            @Path("name") String name
    );

    @GET("/users/{name}/repos")
    LiveData<ApiResponse<List<Repo>>> getRepos(
            @Path("name") String name
    );

    @GET("/user/{uid}/repos")
    LiveData<ApiResponse<List<Repo>>> getNextRepos(
            @Path("uid") int uid,
            @Query("page") int page
    );
}

