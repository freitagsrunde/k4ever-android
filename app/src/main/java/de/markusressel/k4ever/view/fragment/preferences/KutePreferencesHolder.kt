/*
 * Copyright (C) 2018 Markus Ressel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.markusressel.k4ever.view.fragment.preferences

import android.content.Context
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic
import de.markusressel.k4ever.R
import de.markusressel.k4ever.dagger.module.Implementation
import de.markusressel.k4ever.dagger.module.ImplementationTypeEnum
import de.markusressel.k4ever.rest.BasicAuthConfig
import de.markusressel.k4ever.rest.K4EverRestApiClient
import de.markusressel.k4ever.view.IconHandler
import de.markusressel.kutepreferences.library.persistence.KutePreferenceDataProvider
import de.markusressel.kutepreferences.library.preference.category.KuteCategory
import de.markusressel.kutepreferences.library.preference.category.KuteDivider
import de.markusressel.kutepreferences.library.preference.select.KuteSingleSelectPreference
import de.markusressel.kutepreferences.library.preference.text.KutePasswordPreference
import de.markusressel.kutepreferences.library.preference.text.KuteTextPreference
import de.markusressel.kutepreferences.library.preference.toggle.KuteTogglePreference
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Holder for KutePreference items for easy access to preference values across the application
 */
@Singleton
class KutePreferencesHolder @Inject constructor(private val context: Context,
                                                private val iconHelper: IconHandler,
                                                private val dataProvider: KutePreferenceDataProvider,

                                                @param:Implementation(
                                                        ImplementationTypeEnum.DUMMY) private val restClient: K4EverRestApiClient) {

    val connectionCategory by lazy {
        KuteCategory(key = R.string.category_connection_key,
                icon = iconHelper.getPreferenceIcon(MaterialDesignIconic.Icon.gmi_wifi),
                title = context.getString(R.string.connection_title),
                description = context.getString(R.string.connection_summary),
                children = listOf(connectionUriPreference,
                        KuteDivider(key = R.string.divider_authentication_key,
                                title = context.getString(R.string.divider_authentication_title)),
                        authUserPreference, authPasswordPreference))
    }

    val connectionUriPreference by lazy {
        KuteTextPreference(key = R.string.connection_host_key,
                icon = iconHelper.getPreferenceIcon(MaterialDesignIconic.Icon.gmi_battery),
                title = context.getString(R.string.connection_host_title),
                defaultValue = "https://k4ever.freitagsrunde.org/api/v1",
                dataProvider = dataProvider, onPreferenceChangedListener = { old, new ->
            restClient.setHostname(new)
        })

    }

    val authUserPreference by lazy {
        KuteTextPreference(key = R.string.connection_user_key,
                title = context.getString(R.string.connection_auth_user_title), defaultValue = "",
                dataProvider = dataProvider, onPreferenceChangedListener = { old, new ->
            val previousConfig = restClient.getBasicAuthConfig()
            restClient.setBasicAuthConfig(BasicAuthConfig(new, previousConfig?.password ?: ""))
        })
    }

    val authPasswordPreference by lazy {
        KutePasswordPreference(key = R.string.connection_password_key,
                title = context.getString(R.string.connection_auth_password_title),
                defaultValue = "", dataProvider = dataProvider,
                onPreferenceChangedListener = { old, new ->
                    val previousConfig = restClient.getBasicAuthConfig()
                    restClient.setBasicAuthConfig(
                            BasicAuthConfig(previousConfig?.username ?: "", new))
                })
    }

    val productsCategory by lazy {
        KuteCategory(key = R.string.category_products_key,
                icon = iconHelper.getPreferenceIcon(MaterialDesignIconic.Icon.gmi_money),
                title = context.getString(R.string.category_products_title),
                description = context.getString(R.string.category_products_summary),
                children = listOf(showPricesWithDepositPreference))
    }

    val showPricesWithDepositPreference by lazy {
        KuteTogglePreference(key = R.string.hide_prices_with_deposit_key,
                icon = iconHelper.getPreferenceIcon(MaterialDesignIconic.Icon.gmi_money),
                title = context.getString(R.string.show_prices_with_deposit_title),
                defaultValue = true, dataProvider = dataProvider, descriptionFunction = {
            when {
                it -> context.getString(R.string.show_prices_with_deposit_description_on)
                else -> context.getString(R.string.show_prices_with_deposit_description_off)
            }
        })
    }

    val themePreference by lazy {
        KuteSingleSelectPreference(context = context, key = R.string.theme_key,
                icon = iconHelper.getPreferenceIcon(MaterialDesignIconic.Icon.gmi_colorize),
                title = context.getString(R.string.theme_title),
                possibleValues = mapOf(R.string.theme_dark_value to R.string.theme_dark_value_name,
                        R.string.theme_light_value to R.string.theme_light_value_name),
                defaultValue = R.string.theme_dark_value, dataProvider = dataProvider,
                onPreferenceChangedListener = { old, new ->
                    // TODO: restart application/activity
                    //            Bus
                    //                    .send(ThemeChangedEvent(new))
                })
    }

}

