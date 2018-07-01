package de.markusressel.k4ever.view.fragment.base

import android.os.Bundle
import android.support.v4.app.Fragment
import de.markusressel.k4ever.view.InstanceStateProvider

/**
 * Created by Markus on 21.02.2018.
 */
abstract class StateFragmentBase : Fragment() {

    private val stateBundle = Bundle()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            stateBundle
                    .putAll(it.getBundle(KEY_BUNDLE))
        }

        super
                .onActivityCreated(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState
                .putBundle(KEY_BUNDLE, stateBundle)

        super
                .onSaveInstanceState(outState)
    }

    /**
     * Bind a nullable property
     */
    protected fun <T> savedInstanceState() = InstanceStateProvider.Nullable<T>(stateBundle)

    /**
     * Bind a non-null property
     */
    protected fun <T> savedInstanceState(defaultValue: T) = InstanceStateProvider.NotNull(stateBundle, defaultValue)

    companion object {
        const val KEY_BUNDLE = "saved_state"
    }

}