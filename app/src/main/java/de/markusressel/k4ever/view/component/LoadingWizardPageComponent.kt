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

package de.markusressel.k4ever.view.component

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import androidx.annotation.CallSuper
import de.markusressel.k4ever.R
import de.markusressel.k4ever.view.activity.base.WizardActivityBase

/**
 * Created by Markus on 15.02.2018.
 */
class LoadingWizardPageComponent(hostActivity: WizardActivityBase) : ActivityComponent(hostActivity) {

    protected lateinit var loadingLayout: ViewGroup

    protected var contentView: ViewGroup? = null

    fun onCreateView(container: ViewGroup?): View? {
        contentView = container

        val rootView = createWrapperLayout()
        loadingLayout = rootView.findViewById(R.id.layoutLoading)

        return rootView
    }

    private fun createWrapperLayout(): ViewGroup {
        val baseLayout = FrameLayout(context)

        // attach the original content view
        contentView?.let {
            val parent = it.parent as ViewGroup
            parent.removeView(contentView)
            baseLayout.addView(contentView)
            parent.addView(baseLayout)
        }

        // inflate "layout_loading" and attach it to a newly created layout
        val layoutInflater = LayoutInflater.from(context)
        layoutInflater.inflate(R.layout.layout_loading, baseLayout, true) as ViewGroup

        return baseLayout
    }

    /**
     * Show layout_loading animation
     */
    @CallSuper
    fun showLoading() {
        fadeView(loadingLayout, 1f)
    }

    /**
     * Show the actual page content
     */
    @CallSuper
    fun showContent(animated: Boolean = true) {
        if (animated) {
            fadeView(loadingLayout, 0f)
        } else {
            setViewVisibility(loadingLayout, View.GONE)
        }
    }

    private fun setViewVisibility(view: View, visibility: Int) {
        view.post {
            view.visibility = visibility
            view.alpha = 0f
        }
    }

    private fun fadeView(view: View, alpha: Float) {
        val interpolator = when {
            alpha > 0 -> DecelerateInterpolator()
            else -> LinearInterpolator()
        }

        val duration = when {
            alpha >= 1 -> FADE_IN_DURATION_MS
            alpha <= 0 -> FADE_OUT_DURATION_MS
            else -> FADE_DURATION_MS
        }

        view.animate().alpha(alpha).setDuration(duration).setInterpolator(interpolator).withStartAction {
            if (alpha > 0) {
                view.alpha = 0f
                view.visibility = View.VISIBLE
            }
        }.withEndAction {
            if (alpha <= 0) {
                view.visibility = View.GONE
            }
        }
    }

    companion object {
        const val FADE_DURATION_MS = 300L
        const val FADE_IN_DURATION_MS = 400L
        const val FADE_OUT_DURATION_MS = FADE_IN_DURATION_MS / 2
    }

}