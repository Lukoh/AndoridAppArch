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

package com.goforer.goforerarchblueprint.repository.model.data;

import android.arch.persistence.room.Entity;
import android.support.annotation.Nullable;

import com.goforer.base.presentation.model.BaseModel;
import com.google.gson.annotations.SerializedName;

@Entity(primaryKeys = "name")
public class User extends BaseModel {
    @SerializedName("id")
    private final int id;
    @SerializedName("avatar_url")
    private final String avatarUrl;
    @SerializedName("gravatar_id")
    private final String avatarId;
    @SerializedName("url")
    private final String url;
    @SerializedName("name")
    private final String name;
    @SerializedName("email")
    private final String email;
    @SerializedName("company")
    private final String company;
    @SerializedName("blog")
    private final String blog;
    @SerializedName("location")
    private final String location;
    @SerializedName("public_repos")
    private final int publicRepos;
    @SerializedName("followers")
    private final int followers;
    @SerializedName("following")
    private final int following;

    public User(int id, String avatarUrl, String avatarId, String url, String name, String email,
                String company, String blog, String location, int publicRepos, int followers,
                int following) {
        this.id = id;
        this.avatarUrl = avatarUrl;
        this.avatarId = avatarId;
        this.url = url;
        this.name = name;
        this.email = email;
        this.company = company;
        this.blog = blog;
        this.location = location;
        this.publicRepos = publicRepos;
        this.followers = followers;
        this.following = following;
    }

    public int getId() {
        return id;
    }

    @Nullable
    public String getAvatarUrl() {
        return avatarUrl;
    }

    @Nullable
    public String getAvatarId() {
        return avatarId;
    }

    @Nullable
    public String getUrl() {
        return url;
    }

    @Nullable
    public String getName() {
        return name;
    }

    @Nullable
    public String getEmail() {
        return email;
    }

    @Nullable
    public String getCompany() {
        return company;
    }

    @Nullable
    public String getBlog() {
        return blog;
    }

    @Nullable
    public String getLocation() {
        return location;
    }

    public int getPublicRepos() {
        return publicRepos;
    }

    public int getFollowers() {
        return followers;
    }

    public int getFollowing() {
        return following;
    }
}
