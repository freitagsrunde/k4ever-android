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

package de.markusressel.k4ever.extensions.common.android.gui

import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.view.View

/**
 * Creates and instantly shows a snackbar
 *
 * @param text the toast message
 * @param duration the length to show the toast, one of Snackbar.LENGTH_SHORT, Snackbar.LENGTH_LONG
 * @param actionTitle the title of the action button (or null)
 * @param action the action for the action button
 */
fun View.snack(text: String, duration: Int = Snackbar.LENGTH_SHORT, actionTitle: String? = null,
               action: ((View) -> Unit)? = null) {
    val snackbar = Snackbar.make(this, text, duration)
    if (actionTitle != null && action != null) {
        snackbar.setAction(actionTitle, action)
    }
    snackbar.show()
}

/**
 * Creates and instantly shows a snackbar
 *
 * @param text the toast message
 * @param duration the length to show the toast, one of Snackbar.LENGTH_SHORT, Snackbar.LENGTH_LONG
 * @param actionTitle the title of the action button (or null)
 * @param action the action for the action button
 */
fun View.snack(@StringRes text: Int, duration: Int = Snackbar.LENGTH_SHORT,
               actionTitle: String? = null, action: ((View) -> Unit)? = null) {
    snack(context.getString(text), duration, actionTitle, action)
}

/**
 * Creates and instantly shows a snackbar
 *
 * @param text the toast message
 * @param duration the length to show the toast, one of Snackbar.LENGTH_SHORT, Snackbar.LENGTH_LONG
 * @param actionTitle the title of the action button
 * @param action the action for the action button
 */
fun View.snack(@StringRes text: Int,
               duration: Int = Snackbar.LENGTH_SHORT, @StringRes actionTitle: Int,
               action: ((View) -> Unit)) {
    snack(context.getString(text), duration, context.getString(actionTitle), action)
}