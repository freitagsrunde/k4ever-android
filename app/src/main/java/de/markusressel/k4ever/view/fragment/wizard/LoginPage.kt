package de.markusressel.k4ever.view.fragment.wizard

import android.net.Uri
import android.os.Bundle
import android.view.View
import com.github.ajalt.timberkt.Timber
import de.markusressel.k4ever.R
import de.markusressel.k4ever.rest.BasicAuthConfig
import de.markusressel.k4ever.view.fragment.base.WizardPageBase
import kotlinx.android.synthetic.main.wizard_page_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginPage : WizardPageBase() {

    override val layoutRes: Int
        get() = R.layout.wizard_page_login

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        text_input_url.editText?.setText(preferencesHolder.connectionUriPreference.persistedValue)
    }

    private val url
        get() = text_input_url.editText!!.text.toString()
    private val username
        get() = text_input_username.editText!!.text.toString()
    private val password
        get() = text_input_password.editText!!.text.toString()

    override suspend fun validate(): Boolean {
        var errorMessage: String? = null

        if (!isUrlValid(url)) {
            errorMessage = "Invalid URL"
        }
        withContext(Dispatchers.Main) {
            text_input_url.error = errorMessage
        }
        if (errorMessage != null) return false

        if (!isUsernameValid(username)) {
            errorMessage = "Invalid username"
        }
        withContext(Dispatchers.Main) {
            text_input_username.error = errorMessage
        }
        if (errorMessage != null) return false

        if (!checkLogin()) {
            errorMessage = "Invalid password"
        }
        withContext(Dispatchers.Main) {
            text_input_password.error = errorMessage
        }
        return errorMessage == null
    }

    private suspend fun checkLogin(): Boolean {
        restClient.setHostname(url)
        val valid = restClient.checkLogin(username, password)
        return valid
    }

    private suspend fun isUrlValid(url: String): Boolean {
        return try {
            Uri.parse(url)

            restClient.setHostname(url)
            val version = restClient.getVersion()
            Timber.d { "Backend version: ${version.version} (Branch: ${version.branch}, Commit: ${version.commit}, Build Time: ${version.build_time})" }
            return true
        } catch (ex: Exception) {
            false
        }
    }

    private fun isUsernameValid(username: String): Boolean {
        return username.isNotBlank()
    }

    override fun save() {
        restClient.setHostname(url)
        restClient.setBasicAuthConfig(
                BasicAuthConfig(username, password)
        )

        preferencesHolder.connectionUriPreference.persistedValue = url
        preferencesHolder.authUserPreference.persistedValue = username
        preferencesHolder.authPasswordPreference.persistedValue = password
    }

}