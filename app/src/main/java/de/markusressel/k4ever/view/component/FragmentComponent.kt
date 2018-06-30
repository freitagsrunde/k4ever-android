package de.markusressel.k4ever.view.component

import android.content.Context
import de.markusressel.k4ever.view.fragment.base.LifecycleFragmentBase

abstract class FragmentComponent(private val hostFragment: LifecycleFragmentBase) {

    protected val fragment
        get() = hostFragment

    val context: Context? = hostFragment
            .context

}