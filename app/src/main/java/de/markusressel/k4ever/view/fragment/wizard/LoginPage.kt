package de.markusressel.k4ever.view.fragment.wizard

import de.markusressel.k4ever.R
import de.markusressel.k4ever.rest.BasicAuthConfig
import de.markusressel.k4ever.view.fragment.base.WizardPageBase
import kotlinx.android.synthetic.main.wizard_page_login.*
import kotlinx.coroutines.runBlocking

class LoginPage : WizardPageBase() {

    override val layoutRes: Int
        get() = R.layout.wizard_page_login

    override fun isValid(): Boolean {
        val username = text_input_username.editText!!.text.toString()
        val password = text_input_username.editText!!.text.toString()

        restClient.setBasicAuthConfig(
                BasicAuthConfig(username, password)
        )

        val result = runBlocking {
            restClient.getVersion()
        }

        return result.isNotEmpty()
    }

}