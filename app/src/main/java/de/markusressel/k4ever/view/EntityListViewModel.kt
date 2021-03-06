package de.markusressel.k4ever.view

import androidx.lifecycle.ViewModel

/**
 * Base class for implementing a ViewModel for item lists
 */
abstract class EntityListViewModel : ViewModel() {

    /**
     * Override this if you want to use a different page size
     */
    open fun getPageSize(): Int {
        return DEFAULT_PAGING_SIZE
    }

    companion object {
        private const val DEFAULT_PAGING_SIZE = 100
    }

}