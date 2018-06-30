package de.markusressel.k4ever.view.activity.base

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.IntDef
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.Window
import android.view.WindowManager
import com.github.ajalt.timberkt.Timber
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasFragmentInjector
import dagger.android.support.HasSupportFragmentInjector
import de.markusressel.k4ever.navigation.Navigator
import de.markusressel.k4ever.view.IconHandler
import de.markusressel.k4ever.view.ThemeHelper
import de.markusressel.k4ever.view.fragment.preferences.KutePreferencesHolder
import kotlinx.android.synthetic.main.view__toolbar.*
import javax.inject.Inject

/**
 * Created by Markus on 20.12.2017.
 */
abstract class DaggerSupportActivityBase : LifecycleActivityBase(), HasFragmentInjector, HasSupportFragmentInjector {

    @Inject
    internal lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>
    @Inject
    internal lateinit var frameworkFragmentInjector: DispatchingAndroidInjector<android.app.Fragment>

    @Inject
    internal lateinit var navigator: Navigator

    @Inject
    lateinit var kutePreferencesHolder: KutePreferencesHolder

    @Inject
    lateinit var iconHandler: IconHandler

    @Inject
    lateinit var themeHelper: ThemeHelper

    /**
     * @return true if this activity should use a dialog theme instead of a normal activity theme
     */
    @get:Style
    protected abstract val style: Int

    /**
     * The layout ressource for this Activity
     */
    @get:LayoutRes
    protected abstract val layoutRes: Int

    override fun supportFragmentInjector(): AndroidInjector<Fragment>? {
        return supportFragmentInjector
    }

    override fun fragmentInjector(): AndroidInjector<android.app.Fragment>? {
        return frameworkFragmentInjector
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection
                .inject(this)

        // set Theme before anything else in onCreate();
        initTheme()

        if (style == FULLSCREEN) {
            supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            hideStatusBar()
        } else if (style == DIALOG) {
            // Hide title on dialogs to use view_toolbar instead
            supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        }

        super
                .onCreate(savedInstanceState)

        // inflate view manually so it can be altered in plugins
        val contentView = layoutInflater
                .inflate(layoutRes, null)
        setContentView(contentView)

        setSupportActionBar(toolbar)

        supportActionBar
                ?.setDisplayHomeAsUpEnabled(true)

    }

    /**
     * Show the status bar
     */
    protected fun showStatusBar() {
        window
                .clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    /**
     * Hide the status bar
     */
    protected fun hideStatusBar() {
        window
                .setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    private fun initTheme() {
        val theme = kutePreferencesHolder
                .themePreference
                .persistedValue

        if (style == DIALOG) {
            // TODO: Dialog theming
            Timber
                    .w { "Dialog Theming is not yet supported" }
            //            themeHelper
            //                    .applyDialogTheme(this, theme) //set up notitle
        } else {
            themeHelper
                    .applyTheme(this, theme)
        }
    }

    @IntDef(DEFAULT, DIALOG)
    @kotlin.annotation.Retention
    annotation class Style

    companion object {

        /**
         * Normal activity style
         */
        const val DEFAULT = 0

        /**
         * Dialog style
         */
        const val DIALOG = 1

        /**
         * Fullscreen activity style
         */
        const val FULLSCREEN = 2
    }

}
