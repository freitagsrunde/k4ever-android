package de.markusressel.k4ever.view.activity.base

import android.os.Bundle
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.AppIntro2
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasFragmentInjector
import dagger.android.support.HasSupportFragmentInjector
import de.markusressel.k4ever.R
import de.markusressel.k4ever.view.ThemeHandler
import de.markusressel.k4ever.view.component.LoadingWizardPageComponent
import de.markusressel.k4ever.view.fragment.base.WizardPageBase
import de.markusressel.k4ever.view.fragment.preferences.KutePreferencesHolder
import de.markusressel.kutepreferences.core.persistence.KutePreferenceDataProvider
import kotlinx.coroutines.*
import javax.inject.Inject

abstract class WizardActivityBase : AppIntro2(), HasFragmentInjector, HasSupportFragmentInjector {

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

    private val loadingWizardPageComponent by lazy {
        LoadingWizardPageComponent(this)
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)

        // set Theme before anything else in onCreate();
        initTheme()

        super.onCreate(savedInstanceState)

        val rootView: ViewGroup = findViewById(android.R.id.content)
        loadingWizardPageComponent.onCreateView(rootView)
        loadingWizardPageComponent.showContent(false)
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
        val oldIndex = slides.indexOf(oldFragment)
        val newIndex = slides.indexOf(newFragment)

        if (oldIndex == -1) return
        if (newIndex == -1) return

        loadingWizardPageComponent.showLoading()
        GlobalScope.launch {
            try {
                if (oldIndex < newIndex) {
                    if (oldFragment is WizardPageBase) {
                        val valid = runBlocking {
                            oldFragment.validate()
                        }
                        if (valid) {
                            oldFragment.save()
                        } else {
                            return@launch
                        }
                    }
                }

                super.onSlideChanged(oldFragment, newFragment)
            } finally {
                withContext(Dispatchers.Main) {
                    loadingWizardPageComponent.showContent(true)
                }
            }
        }
    }

    override fun onDonePressed(currentFragment: Fragment) {
        loadingWizardPageComponent.showLoading()

        super.onDonePressed(currentFragment)
        val page = currentFragment as WizardPageBase

        GlobalScope.launch {
            val valid = page.validate()
            if (valid) {
                page.save()
                preferencesHolder.shouldShowWizard.persistedValue = false
                finish()
            } else {
                withContext(Dispatchers.Main) {
                    loadingWizardPageComponent.showContent(true)
                }
            }
        }
    }

}