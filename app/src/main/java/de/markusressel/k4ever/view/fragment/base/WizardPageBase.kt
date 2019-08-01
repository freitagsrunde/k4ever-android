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

package de.markusressel.k4ever.view.fragment.base

import com.github.paolorotolo.appintro.ISlidePolicy
import de.markusressel.k4ever.view.activity.base.WizardActivityBase


/**
 * Base class for implementing a fragment
 *
 * Created by Markus on 07.01.2018.
 */
abstract class WizardPageBase : DaggerSupportFragmentBase(), ISlidePolicy {

    protected val wizardActivity: WizardActivityBase
        get() = activity as WizardActivityBase

    /**
     * Checks if the user input of the current page is valid
     *
     * @return true if valid, false otherwise
     */
    abstract suspend fun validate(): Boolean

    /**
     * Saves user input of this page
     */
    abstract fun save()

    override fun isPolicyRespected(): Boolean {
        return true
    }

    override fun onUserIllegallyRequestedNextPage() {
        // do nothing
    }
}