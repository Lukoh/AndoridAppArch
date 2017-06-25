package com.goforer.goforerarchblueprint.domain.broker.sender;

import android.arch.lifecycle.LiveData;

import com.goforer.goforerarchblueprint.AppExecutors;
import com.goforer.goforerarchblueprint.repository.network.api.GithubService;

import javax.inject.Inject;

@SuppressWarnings("unused")
public abstract class Sender<T> {
    @Inject
    GithubService mGithubService;

    @Inject
    AppExecutors mAppExecutors;

    abstract public void send(LiveData<T> contents);
}
