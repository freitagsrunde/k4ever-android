package de.markusressel.k4ever.view.fragment.products

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.design.widget.BottomSheetBehavior
import android.view.*
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic
import de.markusressel.k4ever.R
import de.markusressel.k4ever.business.ShoppingBag
import de.markusressel.k4ever.view.component.LoadingComponent
import de.markusressel.k4ever.view.component.OptionsMenuComponent
import de.markusressel.k4ever.view.fragment.base.DaggerSupportFragmentBase
import de.markusressel.k4ever.view.fragment.preferences.KutePreferencesHolder
import kotlinx.android.synthetic.main.bottom_sheet__shopping_bag.*
import javax.inject.Inject


/**
 * Server Status fragment
 *
 * Created by Markus on 07.01.2018.
 */
class ProductsListFragment : DaggerSupportFragmentBase() {

    override val layoutRes: Int
        get() = R.layout.fragment__products

    @Inject
    lateinit var preferencesHolder: KutePreferencesHolder

    @Inject
    lateinit var shoppingBag: ShoppingBag

    private val loadingComponent by lazy { LoadingComponent(this) }

    private val optionsMenuComponent: OptionsMenuComponent by lazy {
        OptionsMenuComponent(this, optionsMenuRes = R.menu.options_menu_list, onCreateOptionsMenu = { menu: Menu?, menuInflater: MenuInflater? ->
            // set refresh icon
            val sortOrderIcon = iconHandler
                    .getOptionsMenuIcon(MaterialDesignIconic.Icon.gmi_sort)
            menu
                    ?.findItem(R.id.sortOrder)
                    ?.icon = sortOrderIcon

            val refreshIcon = iconHandler
                    .getOptionsMenuIcon(MaterialDesignIconic.Icon.gmi_refresh)
            menu
                    ?.findItem(R.id.refresh)
                    ?.icon = refreshIcon
        }, onOptionsMenuItemClicked = {
            when {
                it.itemId == R.id.refresh -> {

                    // TODO: disconnect and reconnect

                    true
                }
                else -> false
            }
        })
    }

    override fun initComponents(context: Context) {
        super
                .initComponents(context)
        loadingComponent
        optionsMenuComponent
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super
                .onCreateOptionsMenu(menu, inflater)
        optionsMenuComponent
                .onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (super.onOptionsItemSelected(item)) {
            return true
        }
        return optionsMenuComponent
                .onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super
                .onCreate(savedInstanceState)

        val host = preferencesHolder
                .connectionUriPreference
                .persistedValue
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val parent = super.onCreateView(inflater, container, savedInstanceState) as ViewGroup
        return loadingComponent
                .onCreateView(inflater, parent, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super
                .onActivityCreated(savedInstanceState)

        loadingComponent
                .showContent()
    }

    @SuppressLint("ClickableViewAccessibility")
    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super
                .onViewCreated(view, savedInstanceState)

        val persistentbottomSheet = shoppingBagLayout
        val behavior = BottomSheetBehavior.from<View>(persistentbottomSheet)

        behavior?.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // React to state change
                //showing the different states
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // React to dragging events
            }
        })

    }

}
