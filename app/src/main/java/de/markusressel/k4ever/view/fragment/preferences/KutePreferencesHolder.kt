package de.markusressel.k4ever.view.fragment.preferences

import android.content.Context
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic
import de.markusressel.k4ever.R
import de.markusressel.k4ever.view.IconHandler
import de.markusressel.kutepreferences.library.persistence.KutePreferenceDataProvider
import de.markusressel.kutepreferences.library.preference.category.KuteCategory
import de.markusressel.kutepreferences.library.preference.category.KuteDivider
import de.markusressel.kutepreferences.library.preference.select.KuteSingleSelectPreference
import de.markusressel.kutepreferences.library.preference.text.KutePasswordPreference
import de.markusressel.kutepreferences.library.preference.text.KuteTextPreference
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Holder for KutePreference items for easy access to preference values across the application
 */
@Singleton
class KutePreferencesHolder @Inject constructor(private val context: Context, private val iconHelper: IconHandler, private val dataProvider: KutePreferenceDataProvider) {

    val connectionCategory by lazy {
        KuteCategory(key = R.string.category_connection_key, icon = iconHelper.getPreferenceIcon(MaterialDesignIconic.Icon.gmi_wifi), title = context.getString(R.string.connection_title), description = context.getString(R.string.connection_summary), children = listOf(connectionUriPreference, KuteDivider(key = R.string.divider_authentication_key, title = context.getString(R.string.divider_authentication_title)), authUserPreference, authPasswordPreference))
    }

    val connectionUriPreference by lazy {
        KuteTextPreference(key = R.string.connection_host_key, icon = iconHelper.getPreferenceIcon(MaterialDesignIconic.Icon.gmi_battery), title = context.getString(R.string.connection_host_title), defaultValue = "https://k4ever.freitagsrunde.org/api/v1", dataProvider = dataProvider, onPreferenceChangedListener = { old, new ->
            //            Bus
            //                    .send(HostChangedEvent(new))
        })

    }

    val authUserPreference by lazy {
        KuteTextPreference(key = R.string.connection_user_key, title = context.getString(R.string.connection_auth_user_title), defaultValue = "", dataProvider = dataProvider, onPreferenceChangedListener = { old, new ->
            //            Bus
            //                    .send(BasicAuthUserChangedEvent(new))
        })
    }

    val authPasswordPreference by lazy {
        KutePasswordPreference(key = R.string.connection_password_key, title = context.getString(R.string.connection_auth_password_title), defaultValue = "", dataProvider = dataProvider, onPreferenceChangedListener = { old, new ->
            //            Bus
            //                    .send(BasicAuthPasswordChangedEvent(new))
        })
    }

    val themePreference by lazy {
        KuteSingleSelectPreference(context = context, key = R.string.theme_key, icon = iconHelper.getPreferenceIcon(MaterialDesignIconic.Icon.gmi_colorize), title = context.getString(R.string.theme_title), possibleValues = mapOf(R.string.theme_dark_value to R.string.theme_dark_value_name, R.string.theme_light_value to R.string.theme_light_value_name), defaultValue = R.string.theme_dark_value, dataProvider = dataProvider, onPreferenceChangedListener = { old, new ->
            //            Bus
            //                    .send(ThemeChangedEvent(new))
        })
    }

}

