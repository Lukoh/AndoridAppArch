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

package com.goforer.base.presentation.view.customs;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatImageView;

import com.goforer.goforerarchblueprint.R;


@SuppressWarnings("unused")
public class LoadingImageView extends AppCompatImageView {

    AnimationDrawable mDrawable;

    public LoadingImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        startAnimation();
    }

    private void startAnimation() {
        setBackgroundResource(R.drawable.loading_on_list);
        mDrawable = (AnimationDrawable)getBackground();
        mDrawable.start();
    }
}
