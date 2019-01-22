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

package de.markusressel.k4ever.view.activity.base

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import com.github.florent37.materialviewpager.header.HeaderDesign
import com.google.android.material.snackbar.Snackbar
import de.markusressel.k4ever.R
import de.markusressel.k4ever.data.persistence.base.PersistenceManagerBase
import de.markusressel.k4ever.extensions.common.android.gui.snack
import de.markusressel.k4ever.view.fragment.base.DaggerSupportFragmentBase
import kotlinx.android.synthetic.main.fragment__item_detail.*
import kotlinx.android.synthetic.main.view__item_detail__logo.*

/**
 * Base class for detail screens
 */
abstract class DetailActivityBase<T : Any> : DaggerSupportActivityBase() {

    override val layoutRes: Int
        get() = R.layout.fragment__item_detail

    override val style: Int
        get() {
            return if (resources.getBoolean(R.bool.is_tablet)) {
                DIALOG
            } else {
                DEFAULT
            }
        }

    /**
     * Title text of the detail screen
     */
    protected abstract val headerTextString: String

    /**
     * List of tabbed items
     * Tab title StringRes -> Tab content fragment
     */
    abstract val tabItems: List<Pair<Int, () -> DaggerSupportFragmentBase>>

    /**
     * The current state of the item that is displayed by this detail screen
     */
    private var currentState: T? by savedInstanceState()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setToolbar()
        setAdapter()
        setHeader()
        initEntityState()
    }

    private fun setHeader() {
        headerText.text = headerTextString

        materialViewPager.setMaterialViewPagerListener {
            val pageIndex = it

            // set header image from child class implementation
            val headerImage = getHeaderImage() ?: R.drawable.item_detail__default__title_background
            HeaderDesign.fromColorResAndDrawable(R.color.primary, getDrawable(headerImage))
        }

        //        materialViewPager.setMaterialViewPagerListener {
        //            val pageIndex = it
        //
        //            val config: HeaderConfig
        //
        // instantiate this header
        //            HeaderDesign.fromColorResAndDrawable(config.colorRes,
        //                    getDrawable(config.drawableRes))
        //        }
    }

    @DrawableRes
    internal open fun getHeaderImage(): Int? {
        return null
    }

    private fun setToolbar() {
        val toolbar = materialViewPager.toolbar
        if (toolbar != null) {
            setSupportActionBar(toolbar)

            val actionBar = supportActionBar!!
            actionBar.setDisplayHomeAsUpEnabled(true)

            //            actionBar
            //                    .setDisplayShowHomeEnabled(true)

            // activity title comes from library
            actionBar.setDisplayShowTitleEnabled(false)

            actionBar.setDisplayUseLogoEnabled(false)
            actionBar.setHomeButtonEnabled(true)
        }
    }

    private fun setAdapter() {
        val viewPager = materialViewPager.viewPager
        viewPager.adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return tabItems[position].second().apply {
                    arguments = Bundle(2).apply {
                        putLong(KEY_ITEM_ID, getEntityId())
                    }
                }
            }

            override fun getCount(): Int {
                return tabItems.size
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return getString(tabItems[position].first)
            }

        }

        viewPager.offscreenPageLimit = (viewPager.adapter as FragmentStatePagerAdapter).count

        //After set an adapter to the ViewPager
        materialViewPager.pagerTitleStrip.setViewPager(materialViewPager.viewPager)

        if ((viewPager.adapter as FragmentStatePagerAdapter).count == 1) {
            materialViewPager.pagerTitleStrip.underlineColor = Color.TRANSPARENT
            materialViewPager.pagerTitleStrip.indicatorColor = Color.TRANSPARENT

            //                    materialViewPager
            //                            .pagerTitleStrip
            //                            .visibility = View
            //                            .GONE
        }
    }

    private fun initEntityState() {
        val entity = when (getEntityId()) {
            ENTITY_ID_MISSING_VALUE -> {
                throw IllegalStateException("Missing Entity ID!")
            }
            else -> {
                getEntityFromPersistence()!!
            }
        }

        currentState = entity
    }

    private fun getEntityId(): Long {
        return intent.getLongExtra(KEY_ITEM_ID, ENTITY_ID_MISSING_VALUE)
    }

    /**
     * Get the entity from persistence
     */
    private fun getEntityFromPersistence(): T? {
        return getPersistenceHandler().getStore().get(getEntityId())
    }

    /**
     * Get the item from current state holder
     */
    protected fun getItem(): T {
        if (currentState == null) {
            initEntityState()
        }

        return currentState!!
    }

    fun showSnackbar(@StringRes text: Int, duration: Int = Snackbar.LENGTH_SHORT,
                     actionTitle: String? = null, action: ((View) -> Unit)? = null) {
        itemDetailMainLayout.snack(text, duration, actionTitle, action)
    }

    fun showSnackbar(@StringRes text: Int,
                     duration: Int = Snackbar.LENGTH_SHORT, @StringRes actionTitle: Int,
                     action: ((View) -> Unit)) {
        showSnackbar(text, duration, actionTitle, action)
    }

    /**
     * Get the persistence handler for this view
     */
    protected abstract fun getPersistenceHandler(): PersistenceManagerBase<T>

    companion object {

        const val KEY_ITEM_ID = "item_id"
        const val ENTITY_ID_MISSING_VALUE = -1L

        /**
         * Create a new instance of a detail activity
         *
         * @param clazz the class to instantiate (must implements this base class)
         * @param context application context
         * @param entityId the id of the item to display details for
         */
        fun <T : Class<*>> newInstanceIntent(clazz: T, context: Context, entityId: Long?): Intent {
            val intent = Intent(context, clazz)
            entityId?.let {
                intent.apply {
                    putExtra(KEY_ITEM_ID, entityId)
                }
            }

            return intent
        }

    }

}