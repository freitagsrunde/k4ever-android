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

package de.markusressel.k4ever.view.fragment.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import de.markusressel.k4ever.navigation.Navigator
import de.markusressel.k4ever.view.IconHandler
import de.markusressel.k4ever.view.ThemeHandler
import de.markusressel.k4ever.view.fragment.preferences.KutePreferencesHolder
import javax.inject.Inject


/**
 * Base class for implementing a fragment
 *
 * Created by Markus on 07.01.2018.
 */
abstract class DaggerSupportFragmentBase : LifecycleFragmentBase(), HasSupportFragmentInjector {

    @Inject
    lateinit var childFragmentInjector: DispatchingAndroidInjector<Fragment>

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return childFragmentInjector
    }

    @Inject
    internal lateinit var navigator: Navigator

    @Inject
    lateinit var preferencesHolder: KutePreferencesHolder

    @Inject
    protected lateinit var iconHandler: IconHandler

    @Inject
    protected lateinit var themeHandler: ThemeHandler

    /**
     * The layout resource for this Activity
     */
    @get:LayoutRes
    protected abstract val layoutRes: Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val viewModel = createViewDataBinding(inflater, container, savedInstanceState)
        return if (viewModel != null) {
            viewModel.root
        } else {
            val newContainer = inflater.inflate(layoutRes, container, false) as ViewGroup

            val alternative = super
                    .onCreateView(inflater, newContainer, savedInstanceState)

            alternative
                    ?: newContainer
        }
    }

    /**
     * Optionally create and setup your ViewDataBinding and ViewModel in this method
     */
    open fun createViewDataBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): ViewDataBinding? = null

}