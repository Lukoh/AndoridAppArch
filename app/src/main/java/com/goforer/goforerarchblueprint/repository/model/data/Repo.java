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

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.support.annotation.Nullable;

import com.goforer.base.presentation.model.BaseModel;
import com.google.gson.annotations.SerializedName;

@Entity(indices = {@Index("id")}, primaryKeys = {"name"})
public class Repo extends BaseModel{
    @SerializedName("id")
    private final int id;
    @SerializedName("name")
    private final String name;
    @SerializedName("owner")
    @Embedded(prefix = "owner_")
    private final Owner owner;
    @SerializedName("description")
    private final String description;
    @SerializedName("homepage")
    private final String homepage;
    @SerializedName("stargazers_count")
    private final int stars;

    public Repo(int id, String name, Owner owner, String description, String homepage, int stars) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.description = description;
        this.homepage = homepage;
        this.stars = stars;
    }

    public int getId() {
        return id;
    }

    @Nullable
    public String getName() {
        return name;
    }

    @Nullable
    public Owner getOwner() {
        return owner;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    @Nullable
    public String getHomepage() {
        return homepage;
    }

    public int getStars() {
        return stars;
    }

    public final static class Owner {
        @SerializedName("id")
        private final int id;
        @SerializedName("avatar_url")
        private final String avatarUrl;
        @SerializedName("repos_url")
        private final String reposUrl;

        public Owner(int id, String avatarUrl, String reposUrl) {
            this.id = id;
            this.avatarUrl = avatarUrl;
            this.reposUrl = reposUrl;
        }

        public int getId() {
            return id;
        }

        @Nullable
        public String getAvatarUrl() {
            return avatarUrl;
        }

        @Nullable
        public String getReposUrl() {
            return reposUrl;
        }
    }
}
