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
import com.mikepenz.iconics.typeface.library.materialdesigniconic.MaterialDesignIconic
import de.markusressel.k4ever.R
import de.markusressel.k4ever.dagger.module.Implementation
import de.markusressel.k4ever.dagger.module.ImplementationTypeEnum
import de.markusressel.k4ever.rest.K4EverRestApiClient
import de.markusressel.k4ever.view.IconHandler
import de.markusressel.k4ever.view.activity.WizardActivity
import de.markusressel.kutepreferences.core.persistence.KutePreferenceDataProvider
import de.markusressel.kutepreferences.core.preference.action.KuteAction
import de.markusressel.kutepreferences.core.preference.category.KuteCategory
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Holder for KutePreference items for easy access to preference values across the application
 */
@Singleton
class KuteDebugPreferencesHolder @Inject constructor(private val context: Context,
                                                     private val iconHelper: IconHandler,
                                                     private val dataProvider: KutePreferenceDataProvider,
                                                     @param:Implementation(ImplementationTypeEnum.REAL)
                                                     private val restClient: K4EverRestApiClient) {

    val debugCategory by lazy {
        KuteCategory(key = R.string.category_debug_key,
                icon = iconHelper.getPreferenceIcon(MaterialDesignIconic.Icon.gmi_developer_board),
                title = context.getString(R.string.debug_title),
                description = context.getString(R.string.debug_summary),
                children = listOf(openWizardAction)
        )
    }

    val openWizardAction by lazy {
        KuteAction(key = R.string.open_wizard_key,
                icon = iconHelper.getPreferenceIcon(MaterialDesignIconic.Icon.gmi_help),
                title = context.getString(R.string.open_wizard_title),
                description = context.getString(R.string.open_wizard_summary),
                onClickAction = { context, action ->
                    context.startActivity(WizardActivity.getLaunchIntent(context))
                }
        )
    }

}

