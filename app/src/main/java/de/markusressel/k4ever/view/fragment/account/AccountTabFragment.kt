package de.markusressel.k4ever.view.fragment.account

import de.markusressel.k4ever.R
import de.markusressel.k4ever.view.fragment.base.DaggerSupportFragmentBase
import de.markusressel.k4ever.view.fragment.base.TabNavigationFragment

class AccountTabFragment : TabNavigationFragment() {
    override val tabItems: List<Pair<Int, () -> DaggerSupportFragmentBase>>
        get() = listOf(
                R.string.overview to ::AccountOverviewFragment,
                R.string.balance_history to ::BalanceHistoryFragment
        )
}