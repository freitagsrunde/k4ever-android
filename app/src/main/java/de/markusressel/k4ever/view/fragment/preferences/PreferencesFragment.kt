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

package de.markusressel.k4ever.view.fragment.preferences

import de.markusressel.k4ever.view.fragment.preferences.base.LifecyclePreferenceFragmentBase
import de.markusressel.kutepreferences.library.preference.KutePreferencesTree
import javax.inject.Inject

class PreferencesFragment : LifecyclePreferenceFragmentBase() {

    @Inject
    lateinit var preferenceHolder: KutePreferencesHolder

    override fun initPreferenceTree(): KutePreferencesTree {
        return KutePreferencesTree(preferenceHolder.connectionCategory,
                preferenceHolder.productsCategory, preferenceHolder.themePreference)
    }

}