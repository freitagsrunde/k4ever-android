package de.markusressel.k4ever.view.activity.base

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.AppIntro
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasFragmentInjector
import dagger.android.support.HasSupportFragmentInjector
import de.markusressel.k4ever.R
import de.markusressel.k4ever.view.ThemeHandler
import de.markusressel.k4ever.view.fragment.base.WizardPageBase
import de.markusressel.k4ever.view.fragment.preferences.KutePreferencesHolder
import de.markusressel.kutepreferences.core.persistence.KutePreferenceDataProvider
import javax.inject.Inject

abstract class WizardActivityBase : AppIntro(), HasFragmentInjector, HasSupportFragmentInjector {

    @Inject
    internal lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>
    @Inject
    internal lateinit var frameworkFragmentInjector: DispatchingAndroidInjector<android.app.Fragment>

    @Inject
    lateinit var preferencesDataProvider: KutePreferenceDataProvider

    @Inject
    lateinit var themeHandler: ThemeHandler

    @Inject
    lateinit var preferencesHolder: KutePreferencesHolder

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)

        // set Theme before anything else in onCreate();
        initTheme()

        super.onCreate(savedInstanceState)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment>? {
        return supportFragmentInjector
    }

    override fun fragmentInjector(): AndroidInjector<android.app.Fragment>? {
        return frameworkFragmentInjector
    }

    private fun initTheme() {
        val theme = preferencesDataProvider.getValueUnsafe(R.string.theme_key, getString(R.string.theme_dark_value))
        themeHandler.applyTheme(this, theme)
    }

    override fun onSlideChanged(oldFragment: Fragment?, newFragment: Fragment?) {
        if (oldFragment is WizardPageBase) {
            oldFragment.save()
        }

        super.onSlideChanged(oldFragment, newFragment)
    }

    override fun onDonePressed(currentFragment: Fragment) {
        super.onDonePressed(currentFragment)
        val page = currentFragment as WizardPageBase
        if (page.isValid()) {
            preferencesHolder.shouldShowWizard.persistedValue = false
            finish()
        }
    }

}