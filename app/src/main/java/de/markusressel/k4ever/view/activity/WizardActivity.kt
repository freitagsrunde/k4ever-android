package de.markusressel.k4ever.view.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.AppIntroFragment
import com.github.paolorotolo.appintro.model.SliderPage
import de.markusressel.k4ever.R
import de.markusressel.k4ever.view.activity.base.WizardActivityBase
import de.markusressel.k4ever.view.fragment.base.WizardPageBase
import de.markusressel.k4ever.view.fragment.wizard.LoginPage


class WizardActivity : WizardActivityBase() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // welcome page
        val sliderPage = SliderPage()
        sliderPage.title = getString(R.string.wizard_page_welcome_title)
        sliderPage.description = getString(R.string.wizard_page_welcome_description)
        sliderPage.imageDrawable = R.drawable.ic_launcher_foreground
        sliderPage.bgColor = Color.DKGRAY
        addSlide(AppIntroFragment.newInstance(sliderPage))

        // login page
        val loginPage = LoginPage()
        addSlide(loginPage)

        // TODO: theme page
        // TODO: camera page (when barscanner is implemented)
    }

    override fun onDonePressed(currentFragment: Fragment) {
        super.onDonePressed(currentFragment)
        val page = currentFragment as WizardPageBase
        if (page.isValid()) {
            finish()
        }
    }

    companion object {
        fun getLaunchIntent(context: Context): Intent {
            return Intent(context, WizardActivity::class.java)
        }
    }

}