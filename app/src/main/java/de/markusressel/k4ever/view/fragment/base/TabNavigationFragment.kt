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

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import de.markusressel.k4ever.R

/**
 * Created by Markus on 14.02.2018.
 */
abstract class TabNavigationFragment : DaggerSupportFragmentBase() {

    override val layoutRes: Int
        get() = R.layout.fragment__tab_navigation

    private lateinit var viewPager: ViewPager
    private lateinit var tabNavigation: TabLayout

    private var currentPage: Int by savedInstanceState(0)

    // pair of StringRes Int -> function that creates a tab instance
    abstract val tabItems: List<Pair<Int, () -> DaggerSupportFragmentBase>>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = view.findViewById(R.id.viewPager) as ViewPager
        tabNavigation = view.findViewById(R.id.tabBar) as TabLayout

        setupViewPager()

        // set initial page
        viewPager.currentItem = currentPage
    }

    private fun setupViewPager() {
        viewPager.adapter = object : FragmentStatePagerAdapter(childFragmentManager) {
            override fun getItem(position: Int): Fragment {
                // get fragment and create a new instance
                return tabItems[position].second()
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return getString(tabItems[position].first)
            }

            override fun getCount(): Int {
                return tabItems.size
            }
        }

        viewPager.offscreenPageLimit = tabItems.size

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) = Unit

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit

            override fun onPageSelected(position: Int) {
                currentPage = position
            }

        })
    }

    override fun onResume() {
        super.onResume()

        createTabBar()
    }

    private fun createTabBar() {
//        tabNavigation.setupWithViewPager(viewPager)

        tabNavigation.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                // TODO:
//                onTabItemSelected(, true)
            }

        })
    }

    /**
     * Called when a tab navigation item was selected
     */
    @CallSuper
    open fun onTabItemSelected(position: Int, wasSelected: Boolean) {
        // not yet implemented
    }

}