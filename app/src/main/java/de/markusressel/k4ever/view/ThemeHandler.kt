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

import android.app.Activity
import android.content.Context
import android.support.annotation.AttrRes
import android.support.annotation.ColorInt
import android.support.annotation.StyleRes
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import de.markusressel.k4ever.R
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper class for easy applying of themes
 *
 * Created by Markus on 20.12.2017.
 */
@Singleton
class ThemeHandler @Inject constructor(private var context: Context) {

    private val darkThemeValue: String by lazy {
        context.getString(R.string.theme_dark_value)
    }

    private val lightThemeValue: String by lazy {
        context.getString(R.string.theme_light_value)
    }

    /**
     * Apply a Theme to an Activity
     *
     * @param activity Activity to apply theme on
     */
    fun applyTheme(activity: Activity, theme: String) {
        when (theme) {
            lightThemeValue -> setTheme(activity, R.style.AppThemeLight)
            darkThemeValue -> setTheme(activity, R.style.AppThemeDark)
            else -> setTheme(activity, R.style.AppThemeDark)
        }
    }

    /**
     * Apply a Theme to an Activity
     *
     * @param activity Activity to apply theme on
     */
    fun applyDialogTheme(activity: Activity, theme: String) {
        when (theme) {
            lightThemeValue -> setTheme(activity, R.style.DialogThemeLight)
            darkThemeValue -> setTheme(activity, R.style.DialogThemeDark)
            else -> setTheme(activity, R.style.DialogThemeDark)
        }
    }


    /**
     * Apply a Theme to a Fragment
     *
     * @param dialogFragment Fragment to apply theme on
     */
    fun applyDialogTheme(dialogFragment: DialogFragment, theme: String) {
        when (theme) {
            lightThemeValue -> dialogFragment.setStyle(DialogFragment.STYLE_NORMAL,
                    R.style.DialogThemeLight)
            darkThemeValue -> dialogFragment.setStyle(DialogFragment.STYLE_NORMAL,
                    R.style.DialogThemeDark)
            else -> dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogThemeDark)
        }
    }

    private fun setTheme(activity: Activity, @StyleRes themeRes: Int) {
        activity.applicationContext.setTheme(themeRes)
        activity.setTheme(themeRes)
    }

    /**
     * Get Color from Theme attribute
     *
     * @param context Activity context
     * @param attr    Attribute ressource ID
     *
     * @return Color as Int
     */
    @ColorInt
    fun getThemeAttrColor(context: Context, @AttrRes attr: Int): Int {
        val typedValue = TypedValue()
        if (context.theme.resolveAttribute(attr, typedValue, true)) {
            if (typedValue.type >= TypedValue.TYPE_FIRST_INT && typedValue.type <= TypedValue.TYPE_LAST_INT) {
                return typedValue.data
            } else if (typedValue.type == TypedValue.TYPE_STRING) {
                return ContextCompat.getColor(context, typedValue.resourceId)
            }
        }

        return 0
    }

    /**
     * Apply a Theme to a BottomSheetFragment
     *
     * @param fragment Fragment to apply theme on
     */
    fun applyTheme(fragment: BottomSheetDialogFragment, theme: String) {
        when (theme) {
            lightThemeValue -> fragment.setStyle(BottomSheetDialogFragment.STYLE_NORMAL,
                    R.style.BottomSheetThemeLight)
            darkThemeValue -> fragment.setStyle(BottomSheetDialogFragment.STYLE_NORMAL,
                    R.style.BottomSheetThemeDark)
            else -> fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetThemeDark)
        }
    }

    @ColorInt
    fun getBalanceColor(currentBalance: Double): Int {
        return when {
            currentBalance < 0 -> ContextCompat.getColor(context, R.color.md_red_A400)
            currentBalance > 0 -> ContextCompat.getColor(context, R.color.md_green_A400)
            else -> getThemeAttrColor(context, android.R.attr.textColorPrimary)
        }
    }

}