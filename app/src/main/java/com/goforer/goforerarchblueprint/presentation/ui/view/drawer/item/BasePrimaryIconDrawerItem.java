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

package com.goforer.goforerarchblueprint.presentation.ui.view.drawer.item;

import android.content.Context;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;

import com.goforer.goforerarchblueprint.presentation.ui.view.drawer.holder.IconBaseViewHolder;
import com.goforer.goforerarchblueprint.presentation.ui.view.drawer.loader.SlidingDrawerImageLoader;
import com.mikepenz.materialdrawer.holder.ColorHolder;
import com.mikepenz.materialdrawer.holder.ImageHolder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.BaseDrawerItem;
import com.mikepenz.materialdrawer.model.BaseViewHolder;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;
import com.mikepenz.materialize.util.UIUtils;

@SuppressWarnings("unchecked")
abstract class BasePrimaryIconDrawerItem <T, VH extends BaseViewHolder>
        extends BaseDrawerItem<T, VH> {
    private StringHolder description;
    private ColorHolder descriptionTextColor;

    @SuppressWarnings("unused")
    public T withIcon(String url) {
        this.icon = new ImageHolder(url);
        return (T) this;
    }

    @SuppressWarnings("unused")
    public T withIcon(Uri uri) {
        this.icon = new ImageHolder(uri);
        return (T) this;
    }

    @SuppressWarnings("unused")
    public T withDescription(String description) {
        this.description = new StringHolder(description);
        return (T) this;
    }

    @SuppressWarnings("unused")
    public T withDescription(@StringRes int descriptionRes) {
        this.description = new StringHolder(descriptionRes);
        return (T) this;
    }

    @SuppressWarnings("unused")
    public T withDescriptionTextColor(@ColorInt int color) {
        this.descriptionTextColor = ColorHolder.fromColor(color);
        return (T) this;
    }

    @SuppressWarnings("unused")
    public T withDescriptionTextColorRes(@ColorRes int colorRes) {
        this.descriptionTextColor = ColorHolder.fromColorRes(colorRes);
        return (T) this;
    }

    @Override
    public ImageHolder getIcon() {
        return icon;
    }

    public StringHolder getDescription() {
        return description;
    }

    private ColorHolder getDescriptionTextColor() {
        return descriptionTextColor;
    }

    /**
     * a helper method to have the logic for all secondaryDrawerItems only once
     *
     * @param viewHolder A ViewHolder describes an item view and metadata about its place
     *                   within the RecyclerView.
     */
    void bindViewHelper(IconBaseViewHolder viewHolder) {
        Context context = viewHolder.itemView.getContext();

        //set the identifier from the drawerItem here. It can be used to run tests
        viewHolder.itemView.setId(hashCode());

        //set the item selected if it is
        viewHolder.itemView.setSelected(isSelected());

        //set the item enabled if it is
        viewHolder.itemView.setEnabled(isEnabled());

        //
        viewHolder.itemView.setTag(this);

        //get the correct color for the background
        int selectedColor = getSelectedColor(context);
        //get the correct color for the text
        int color = getColor(context);
        ColorStateList selectedTextColor = getTextColorStateList(color,
                getSelectedTextColor(context));
        //get the correct color for the icon
        @SuppressWarnings("unused")
        int iconColor = getIconColor(context);
        @SuppressWarnings("unused")
        int selectedIconColor = getSelectedIconColor(context);

        //set the background for the item
        UIUtils.setBackground(viewHolder.getView(), UIUtils.getSelectableBackground(context,
                selectedColor, true));
        //set the text for the name
        StringHolder.applyTo(this.getName(), viewHolder.getName());
        //set the text for the description or hide
        StringHolder.applyToOrHide(this.getDescription(), viewHolder.getDescription());

        //set the colors for textViews
        viewHolder.getName().setTextColor(selectedTextColor);
        //set the description text color
        ColorHolder.applyToOr(getDescriptionTextColor(), viewHolder.getDescription(),
                selectedTextColor);

        //define the typeface for our textViews
        if (getTypeface() != null) {
            viewHolder.getName().setTypeface(getTypeface());
            viewHolder.getDescription().setTypeface(getTypeface());
        }

        //get the drawables for our icon and set it
        SlidingDrawerImageLoader.getInstance().cancelImage(viewHolder.getIcon());
        //set the placeholder
        viewHolder.getIcon().setImageDrawable(SlidingDrawerImageLoader.getInstance()
                .getImageLoader().placeholder(viewHolder.getIcon().getContext(),
                        SlidingDrawerImageLoader.Tags.PRIMARY_ICON.name()));
        //set the icon
        ImageHolder.applyTo(getIcon(), viewHolder.getIcon(),
                SlidingDrawerImageLoader.Tags.PRIMARY_ICON.name());

        //for android API 17 --> Padding not applied via xml
        DrawerUIUtils.setDrawerVerticalPadding(viewHolder.getView(), getLevel());
    }
}
