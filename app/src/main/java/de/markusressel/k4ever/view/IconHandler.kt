/*
 * Copyright (C) 2018 Markus Ressel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.markusressel.k4ever.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.IIcon
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic
import de.markusressel.k4ever.R
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper class for access to often used icons
 *
 *
 * Created by Markus on 13.12.2015.
 */
@Singleton
class IconHandler @Inject constructor() {

    @Inject
    lateinit var context: Context

    @Inject
    lateinit var themeHandler: ThemeHandler

    /**
     * Get an icon suitable for the swipe menu
     *
     * @param icon the icon resource
     *
     * @return the icon
     */
    fun getNavigationIcon(icon: IIcon): IconicsDrawable {
        val color = themeHandler.getThemeAttrColor(context, android.R.attr.textColorPrimary)
        return getIcon(icon, color, 24)
    }

    /**
     * Get an icon suitable for the bottom navigation bar
     *
     * @param icon the icon resource
     *
     * @return the icon
     */
    fun getBottomNavigationIcon(icon: IIcon): IconicsDrawable {
        val color = themeHandler.getThemeAttrColor(context, android.R.attr.textColorPrimary)
        return getIcon(icon, color, 48)
    }

    /**
     * Get an icon suitable for a fab button of normal size
     *
     * @param icon the icon resource
     *
     * @return the icon
     */
    fun getFabIcon(icon: IIcon): IconicsDrawable {
        val color = Color.WHITE
        return getIcon(icon, color, 24, 5)
    }

    /**
     * Get an icon suitable for the options menu
     *
     * @param icon the icon resource
     *
     * @return the icon
     */
    fun getOptionsMenuIcon(icon: IIcon): IconicsDrawable {
        //        val color = themeHelper
        //                .getThemeAttrColor(context, android.R.attr.textColorPrimary)
        val color = ContextCompat.getColor(context, android.R.color.white)

        var padding = 0
        if (icon === MaterialDesignIconic.Icon.gmi_plus) {
            padding = 5
        } else if (icon === MaterialDesignIconic.Icon.gmi_reorder || icon === MaterialDesignIconic.Icon.gmi_refresh) {
            padding = 2
        }

        return getIcon(icon, color, 24, padding)
    }

    /**
     * @return an icon for a preference
     */
    fun getPreferenceIcon(icon: IIcon): Drawable {
        val color = themeHandler.getThemeAttrColor(context,
                R.attr.kute_preferences__setting__default_icon_color)
        return getIcon(icon, color = color, sizeDp = 36)
    }

    fun getHistoryItemIcon(icon: IIcon): IconicsDrawable {
        val color = themeHandler.getThemeAttrColor(context, android.R.attr.textColorPrimary)
        return getIcon(icon, color, 36)
    }


    fun getIcon(icon: IIcon, @ColorInt color: Int, sizeDp: Int, paddingDp: Int = 0): IconicsDrawable {
        return IconicsDrawable(context, icon).sizeDp(sizeDp).paddingDp(paddingDp).color(color)
    }

}
