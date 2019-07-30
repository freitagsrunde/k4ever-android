package de.markusressel.k4ever.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.github.paolorotolo.appintro.AppIntroFragment
import com.github.paolorotolo.appintro.model.SliderPage
import de.markusressel.k4ever.R
import de.markusressel.k4ever.view.activity.base.WizardActivityBase
import de.markusressel.k4ever.view.fragment.wizard.LoginPage


class WizardActivity : WizardActivityBase() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        skipButtonEnabled = false
        pager.isNextPagingEnabled = false

        // welcome page
        val sliderPage = SliderPage()
        sliderPage.title = getString(R.string.wizard_page_welcome_title)
        sliderPage.description = getString(R.string.wizard_page_welcome_description)
        sliderPage.imageDrawable = R.drawable.ic_launcher_foreground
        addSlide(AppIntroFragment.newInstance(sliderPage))

        // login page
        val loginPage = LoginPage()
        addSlide(loginPage)

        // TODO: theme page
        // TODO: camera page (when barscanner is implemented)
    }

    companion object {
        fun getLaunchIntent(context: Context): Intent {
            return Intent(context, WizardActivity::class.java)
        }
    }

}