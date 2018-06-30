package de.markusressel.k4ever.view.fragment.preferences

import de.markusressel.k4ever.view.fragment.preferences.base.LifecyclePreferenceFragmentBase
import de.markusressel.kutepreferences.library.preference.KutePreferencesTree
import javax.inject.Inject

class PreferencesFragment : LifecyclePreferenceFragmentBase() {

    @Inject
    lateinit var preferenceHolder: KutePreferencesHolder

    override fun initPreferenceTree(): KutePreferencesTree {
        return KutePreferencesTree(preferenceHolder.connectionCategory, preferenceHolder.themePreference)
    }

}