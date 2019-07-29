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

package de.markusressel.k4ever.view.activity.base

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import co.lokalise.android.sdk.core.LokaliseContextWrapper
import de.markusressel.k4ever.view.InstanceStateProvider


/**
 * Created by Markus on 21.02.2018.
 */
abstract class StateActivityBase : AppCompatActivity() {

    private val stateBundle = Bundle()

    override fun attachBaseContext(newBase: Context?) {
        // Inject the Lokalise SDK into the activity context
        super.attachBaseContext(LokaliseContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            stateBundle.putAll(savedInstanceState.getBundle(KEY_BUNDLE))
        }

        super.onCreate(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBundle(KEY_BUNDLE, stateBundle)

        super.onSaveInstanceState(outState)
    }

    /**
     * Bind a nullable property
     */
    protected fun <T> savedInstanceState() = InstanceStateProvider.Nullable<T>(stateBundle)

    /**
     * Bind a non-null property
     */
    protected fun <T> savedInstanceState(defaultValue: T) = InstanceStateProvider.NotNull(stateBundle, defaultValue)

    companion object {
        const val KEY_BUNDLE = "saved_state"
    }

}