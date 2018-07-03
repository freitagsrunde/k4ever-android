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
import de.markusressel.k4ever.data.persistence.product.ProductEntity
import de.markusressel.k4ever.data.persistence.product.ProductPersistenceManager
import de.markusressel.k4ever.databinding.ListItemProductBinding
import de.markusressel.k4ever.rest.products.model.ProductModel
import de.markusressel.k4ever.view.fragment.base.ListFragmentBase
import de.markusressel.k4ever.view.fragment.base.SortOption
import io.reactivex.Single
import kotlinx.android.synthetic.main.fragment__recyclerview.*
import kotlinx.android.synthetic.main.layout__bottom_sheet__shopping_bag.*
import javax.inject.Inject


/**
 * Created by Markus on 07.01.2018.
 */
class ProductsFragment : ListFragmentBase<ProductModel, ProductEntity>() {

    override val layoutRes: Int
        get() = R.layout.fragment__products

    @Inject
    lateinit var persistenceManager: ProductPersistenceManager

    override fun getPersistenceHandler(): PersistenceManagerBase<ProductEntity> = persistenceManager

    override fun createAdapter(): LastAdapter {
        return LastAdapter(listValues, BR.item)
                .map<ProductEntity, ListItemProductBinding>(R.layout.list_item__product) {
                    onCreate {
                        it
                                .binding
                                .presenter = this@ProductsFragment
                    }
                    onClick {
                        openDetailView(listValues[it.adapterPosition])
                    }
                }
                .into(recyclerView)
    }

    override fun loadListDataFromSource(): Single<List<ProductModel>> {
//        return restClient.getAllProducts()

        val p1 = ProductModel(0, "Mio Mate", "Getränk der Studenten", 1.0, 0.2)
        val p2 = ProductModel(1, "Club Mate", "Getränk der Studenten", 0.8, 0.2)
        val p3 = ProductModel(2, "Cola", "Zucker", 1.0, 0.2)

        // TODO:
        return Single
                .just(listOf(p1, p2, p3))
    }

    override fun mapToEntity(it: ProductModel): ProductEntity {
        return ProductEntity(0, it.id, it.name, it.description, it.price, it.deposit)
    }

    override fun getAllSortCriteria(): List<SortOption<ProductEntity>> {
        // TODO sort options need to be persistable
        return listOf(SortOption(0, R.string.name, { t -> t.name }, false))
    }

    @Inject
    lateinit var shoppingBag: ShoppingBag

    private lateinit var shoppingBagBottomSheetBehaviour: BottomSheetBehavior<View>

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

        shoppingBagBottomSheetBehaviour = BottomSheetBehavior
                .from<View>(shoppingBagLayout)

        updateShoppingBag()

        shoppingBagBottomSheetBehaviour.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
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

    private fun updateShoppingBag() {
        updateShoppingBagVisibility()
        updateShoppingBagContent()
    }

    private fun updateShoppingBagVisibility() {
        if (shoppingBag.items.isEmpty()) {
            setShoppingBagVisible(false)
        } else {
            setShoppingBagVisible(true)
        }
    }

    private fun setShoppingBagVisible(visible: Boolean) {
        shoppingBagBottomSheetBehaviour.isHideable = !visible
        shoppingBagBottomSheetBehaviour.state = when (visible) {
            true -> BottomSheetBehavior.STATE_COLLAPSED
            false -> BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun updateShoppingBagContent() {
        totalItemCountAndCost.text = getString(R.string.shopping_bag__total_items_and_cost,
                shoppingBag.getTotalItemCount(),
                shoppingBag.getTotalPrice())

        // TODO: inflate layout for items in shopping bag
    }

    /**
     * Shows a detail view of the specified product
     */
    fun openDetailView(productEntity: ProductEntity) {
        // TODO:
    }

    /**
     * Adds the specified item to the shopping bag
     */
    fun addItemToShoppingBag(productEntity: ProductEntity, withDeposit: Boolean) {
        shoppingBag.add(productEntity, 1, withDeposit)
        updateShoppingBag()
    }

    fun removeItemFromShoppingBag(productEntity: ProductEntity, withDeposit: Boolean) {
        shoppingBag.remove(productEntity, 1, withDeposit)
        updateShoppingBag()
    }

}