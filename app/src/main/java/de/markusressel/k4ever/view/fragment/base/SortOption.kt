package de.markusressel.k4ever.view.fragment.base

import android.support.annotation.StringRes

/**
 * Class specifying a single SortOption for a list view
 */
data class SortOption<in T : Any>(val id: Long, @StringRes val name: Int,
                                  val selector: (T) -> Comparable<*>?,
                                  var reversed: Boolean = false)