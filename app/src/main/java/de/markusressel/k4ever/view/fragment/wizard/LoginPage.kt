package de.markusressel.k4ever.view.fragment.wizard

import android.net.Uri
import android.os.Bundle
import android.view.View
import de.markusressel.k4ever.R
import de.markusressel.k4ever.rest.BasicAuthConfig
import de.markusressel.k4ever.view.fragment.base.WizardPageBase
import kotlinx.android.synthetic.main.wizard_page_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class LoginPage : WizardPageBase() {

    override val layoutRes: Int
        get() = R.layout.wizard_page_login

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        text_input_url.editText?.setText(preferencesHolder.connectionUriPreference.persistedValue)
    }

    override fun isValid(): Boolean {
        val url = text_input_url.editText!!.text.toString()
        val username = text_input_username.editText!!.text.toString()
        val password = text_input_username.editText!!.text.toString()

        if (!isUrlValid(url)) {
            text_input_username.error = "Invalid URL"
            return false
        } else {
            text_input_username.error = null
        }
        if (!isUsernameValid(username)) {
            text_input_username.error = "Invalid username"
            return false
        } else {
            text_input_username.error = null
        }

        return runBlocking(Dispatchers.IO) {
            restClient.setHostname(url)
            restClient.checkLogin(username, password)
        }
    }

    private fun isUrlValid(url: String): Boolean {
        return try {
            Uri.parse(url)
            true
        } catch (ex: Exception) {
            false
        }
    }

    private fun isUsernameValid(username: String): Boolean {
        return username.isNotBlank()
    }

    override fun save() {
        val url = text_input_url.editText!!.text.toString()
        val username = text_input_username.editText!!.text.toString()
        val password = text_input_username.editText!!.text.toString()

        restClient.setHostname(url)
        restClient.setBasicAuthConfig(
                BasicAuthConfig(username, password)
        )

        preferencesHolder.connectionUriPreference.persistedValue = url
        preferencesHolder.authUserPreference.persistedValue = username
        preferencesHolder.authPasswordPreference.persistedValue = password
    }

}