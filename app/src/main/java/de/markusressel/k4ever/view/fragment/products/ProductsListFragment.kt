package de.markusressel.k4ever.view.fragment.products

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.design.widget.BottomSheetBehavior
import android.view.View
import com.github.nitrico.lastadapter.LastAdapter
import de.markusressel.k4ever.BR
import de.markusressel.k4ever.R
import de.markusressel.k4ever.business.ShoppingBag
import de.markusressel.k4ever.data.persistence.base.PersistenceManagerBase
import de.markusressel.k4ever.data.persistence.base.entity.ProductEntity
import de.markusressel.k4ever.data.persistence.base.manager.ProductPersistenceManager
import de.markusressel.k4ever.databinding.ListItemProductBinding
import de.markusressel.k4ever.rest.model.ProductModel
import de.markusressel.k4ever.view.fragment.base.ListFragmentBase
import de.markusressel.k4ever.view.fragment.base.SortOption
import de.markusressel.k4ever.view.fragment.preferences.KutePreferencesHolder
import io.reactivex.Single
import kotlinx.android.synthetic.main.bottom_sheet__shopping_bag.*
import kotlinx.android.synthetic.main.fragment__recyclerview.*
import javax.inject.Inject


/**
 * Server Status fragment
 *
 * Created by Markus on 07.01.2018.
 */
class ProductsListFragment : ListFragmentBase<ProductModel, ProductEntity>() {

    @Inject
    lateinit var persistenceManager: ProductPersistenceManager

    override fun getPersistenceHandler(): PersistenceManagerBase<ProductEntity> = persistenceManager

    override fun createAdapter(): LastAdapter {
        return LastAdapter(listValues, BR.item)
                .map<ProductEntity, ListItemProductBinding>(R.layout.list_item__product) {
                    onCreate {
                        it
                                .binding
                                .presenter = this@ProductsListFragment
                    }
                    onClick {
                        openDetailView(listValues[it.adapterPosition])
                    }
                }
                .into(recyclerView)
    }

    override fun loadListDataFromSource(): Single<List<ProductModel>> {
        // TODO:
        return Single.fromCallable { emptyList<ProductModel>() }
    }

    override fun mapToEntity(it: ProductModel): ProductEntity {
        return ProductEntity(0, 0, "", "", 0.0, 0.0)
    }

    override fun getAllSortCriteria(): List<SortOption<ProductEntity>> {
        // TODO
        return listOf()
    }

    override val layoutRes: Int
        get() = R.layout.fragment__products

    @Inject
    lateinit var preferencesHolder: KutePreferencesHolder

    @Inject
    lateinit var shoppingBag: ShoppingBag

    override fun onCreate(savedInstanceState: Bundle?) {
        super
                .onCreate(savedInstanceState)

        val host = preferencesHolder
                .connectionUriPreference
                .persistedValue
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

    fun openDetailView(productEntity: ProductEntity) {
        // TODO:
    }

}
