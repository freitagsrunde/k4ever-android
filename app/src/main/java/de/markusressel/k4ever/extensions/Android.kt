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

package de.markusressel.k4ever.extensions

import android.content.Context
import android.os.AsyncTask
import android.support.v4.app.Fragment
import de.markusressel.k4ever.R

/**
 * Returns true if the current device is considered a tablet
 */
fun Context.isTablet(): Boolean {
    return resources.getBoolean(R.bool.is_tablet)
}

fun Any.doAsync(handler: () -> Unit) {
    object : AsyncTask<Void, Void, Void?>() {
        override fun doInBackground(vararg p0: Void?): Void? {
            handler()
            return null
        }
    }.execute()
}

fun Throwable.prettyPrint(): String {
    val message = "${this.message}:\n" + "${this.stackTrace.joinToString(separator = "\n")}}"

    return message
}

fun Fragment.context(): Context {
    return this.context as Context
}

fun Float.pxToSp(context: Context): Float {
    return this / context.resources.displayMetrics.scaledDensity
}