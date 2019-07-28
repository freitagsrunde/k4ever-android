package de.markusressel.k4ever.view.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import com.github.paolorotolo.appintro.AppIntroFragment
import com.github.paolorotolo.appintro.model.SliderPage
import de.markusressel.k4ever.view.activity.base.WizardActivityBase
import de.markusressel.k4ever.view.fragment.wizard.ServerPage


class WizardActivity : WizardActivityBase() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sliderPage = SliderPage()
        sliderPage.title = "Title"
        sliderPage.description = "Description"
//        sliderPage.imageDrawable = image
        sliderPage.bgColor = Color.DKGRAY

        addSlide(AppIntroFragment.newInstance(sliderPage))

        val serverPage = ServerPage()
        addSlide(serverPage)
    }

    companion object {
        fun getLaunchIntent(context: Context): Intent {
            return Intent(context, WizardActivity::class.java)
        }
    }

}