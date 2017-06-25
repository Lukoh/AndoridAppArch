/*
 * Copyright (C) 2015-2017 Lukoh Nam, goForer
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

package com.goforer.goforerarchblueprint.presentation.caller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.customtabs.CustomTabsCallback;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsSession;

import com.goforer.base.presentation.customtabsclient.shared.CustomTabsHelper;
import com.goforer.base.presentation.customtabsclient.shared.ServiceConnection;
import com.goforer.base.presentation.customtabsclient.shared.ServiceConnectionCallback;
import com.goforer.goforerarchblueprint.R;
import com.goforer.goforerarchblueprint.presentation.ui.repo.RepoActivity;

public enum Caller {
    INSTANCE;

    private String mUrl;

    private ServiceConnection mServiceConnection = new ServiceConnection(new ServiceConnectionCallback() {
        @Override
        public void onServiceConnected(CustomTabsClient client) {
            client.warmup(0);

            CustomTabsSession session = client.newSession(new CustomTabsCallback());
            session.mayLaunchUrl(Uri.parse(mUrl), null, null);
        }

        @Override
        public void onServiceDisconnected() {
        }

    });

    private Intent createIntent(Context context, Class<?> cls, boolean isNewTask) {
        Intent intent = new Intent(context, cls);

        if (isNewTask && !(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        return intent;
    }

    @SuppressWarnings("unused")
    private Intent createIntent(String action) {
        Intent intent = new Intent(action);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        return intent;
    }

    public void callRepository(Context context) {
        Intent intent = createIntent(context, RepoActivity.class, true);
        context.startActivity(intent);
    }

    public void callChromeCustomTabs(Context context, final String url) {
        mUrl = url;

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setCloseButtonIcon(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.ic_close));
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            builder.setToolbarColor(context.getResources()
                    .getColor(R.color.colorPrimary, null)).setShowTitle(true);
        } else {
            builder.setToolbarColor(context.getResources()
                    .getColor(R.color.colorPrimary)).setShowTitle(true);
        }

        builder.enableUrlBarHiding();
        CustomTabsIntent customTabsIntent = builder.build();
        String packageName = CustomTabsHelper.getPackageNameToUse(context);
        CustomTabsHelper.addKeepAliveExtra(context, customTabsIntent.intent);
        CustomTabsClient.bindCustomTabsService(context, packageName, mServiceConnection);

        customTabsIntent.launchUrl(context, Uri.parse(url));
    }

    public void unBindService(Context context) {
        context.unbindService(mServiceConnection);
    }
}

