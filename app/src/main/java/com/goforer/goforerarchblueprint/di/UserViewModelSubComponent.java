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

import com.goforer.goforerarchblueprint.GoforerArchBlueprint;
import com.goforer.goforerarchblueprint.presentation.ui.splash.viewmodel.UserViewModel;

import dagger.Subcomponent;

/**
 * A sub component to create UserViewModel. It is called by the
 * {@link GoforerArchBlueprint}.
 * Using this component allows UserViewModel to define {@link javax.inject.Inject} constructors.
 */
@Subcomponent
public interface UserViewModelSubComponent {
    @Subcomponent.Builder
    interface Builder {
        UserViewModelSubComponent build();
    }

    UserViewModel userViewModel();
}

