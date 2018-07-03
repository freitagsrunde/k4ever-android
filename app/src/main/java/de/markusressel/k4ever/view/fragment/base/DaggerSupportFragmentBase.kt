package de.markusressel.k4ever.view.fragment.base

import android.content.Context
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import de.markusressel.k4ever.navigation.Navigator
import de.markusressel.k4ever.view.IconHandler
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
        AndroidSupportInjection
                .inject(this)
        super
                .onAttach(context)
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

    /**
     * The layout resource for this Activity
     */
    @get:LayoutRes
    protected abstract val layoutRes: Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val newContainer = inflater.inflate(layoutRes, container, false) as ViewGroup

        val alternative = super
                .onCreateView(inflater, newContainer, savedInstanceState)

        return alternative ?: newContainer
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super
                .onViewCreated(view, savedInstanceState)
    }

}