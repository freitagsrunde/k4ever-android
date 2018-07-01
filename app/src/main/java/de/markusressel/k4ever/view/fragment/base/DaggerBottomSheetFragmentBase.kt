package de.markusressel.k4ever.view.fragment.base

import android.content.Context
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import de.markusressel.k4ever.view.IconHandler
import de.markusressel.k4ever.view.ThemeHelper
import de.markusressel.k4ever.view.fragment.preferences.KutePreferencesHolder
import javax.inject.Inject

abstract class DaggerBottomSheetFragmentBase : BottomSheetDialogFragment(),
        HasSupportFragmentInjector {

    @Inject
    lateinit var childFragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var themeHelper: ThemeHelper

    @Inject
    lateinit var kutePreferencesHolder: KutePreferencesHolder

    override fun onAttach(context: Context) {
        AndroidSupportInjection
                .inject(this)

        themeHelper
                .applyTheme(this, kutePreferencesHolder
                        .themePreference
                        .persistedValue)

        super
                .onAttach(context)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return childFragmentInjector
    }

    @Inject
    protected lateinit var preferencesHolder: KutePreferencesHolder

    @Inject
    protected lateinit var iconHandler: IconHandler

    /**
     * The layout resource for this Activity
     */
    @get:LayoutRes
    protected abstract val layoutRes: Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val newContainer = inflater.inflate(layoutRes, container, false) as ViewGroup

        val alternative = super
                .onCreateView(inflater, newContainer, savedInstanceState)

        return alternative
                ?: newContainer
    }

}