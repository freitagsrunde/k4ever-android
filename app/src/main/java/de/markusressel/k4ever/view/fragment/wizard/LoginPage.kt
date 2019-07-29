package de.markusressel.k4ever.view.fragment.wizard

import android.os.Bundle
import android.view.View
import com.github.ajalt.timberkt.Timber
import de.markusressel.k4ever.R
import de.markusressel.k4ever.rest.BasicAuthConfig
import de.markusressel.k4ever.view.fragment.base.WizardPageBase
import kotlinx.android.synthetic.main.wizard_page_login.*

class LoginPage : WizardPageBase() {

    override val layoutRes: Int
        get() = R.layout.wizard_page_login

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        text_input_url.hint = getString(R.string.wizard_page_login_url_hint)
        text_input_url.editText?.setText(preferencesHolder.connectionUriPreference.persistedValue)
    }

    override fun isValid(): Boolean {
        val url = text_input_url.editText!!.text.toString()
        val username = text_input_username.editText!!.text.toString()
        val password = text_input_username.editText!!.text.toString()

        restClient.setHostname(url)
        restClient.setBasicAuthConfig(
                BasicAuthConfig(username, password)
        )

        return try {
            // TODO: enable when api returns a parsable date...
//            val result = runBlocking {
//                restClient.getVersion()
//            }
            true
        } catch (ex: Exception) {
            Timber.e(ex)
            false
        }
    }

    override fun save() {
        val url = text_input_url.editText!!.text.toString()
        val username = text_input_username.editText!!.text.toString()
        val password = text_input_username.editText!!.text.toString()

        preferencesHolder.connectionUriPreference.persistedValue = url
        preferencesHolder.authUserPreference.persistedValue = username
        preferencesHolder.authPasswordPreference.persistedValue = password
    }

}