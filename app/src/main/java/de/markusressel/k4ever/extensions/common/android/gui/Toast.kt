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

import android.content.Context
import android.support.annotation.StringRes
import android.widget.Toast

/**
 * Creates and instantly shows a toast message
 *
 * @param text the toast message
 * @param duration the length to show the toast, one of Toast.LENGTH_SHORT, Toast.LENGTH_LONG
 */
fun Context.toast(text: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, duration).show()
}

/**
 * Creates and instantly shows a toast message
 *
 * @param text the toast message
 * @param duration the length to show the toast, one of Toast.LENGTH_SHORT, Toast.LENGTH_LONG
 */
fun Context.toast(@StringRes text: Int, duration: Int = Toast.LENGTH_SHORT) {
    toast(this.getString(text), duration)
}