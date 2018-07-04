package de.markusressel.k4ever.view.fragment.products

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.design.widget.BottomSheetBehavior
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.github.nitrico.lastadapter.LastAdapter
import com.jakewharton.rxbinding2.view.RxView
import de.markusressel.k4ever.BR
import de.markusressel.k4ever.R
import de.markusressel.k4ever.business.ShoppingCart
import de.markusressel.k4ever.business.getPrice
import de.markusressel.k4ever.data.persistence.base.PersistenceManagerBase
import de.markusressel.k4ever.data.persistence.product.ProductEntity
import de.markusressel.k4ever.data.persistence.product.ProductPersistenceManager
import de.markusressel.k4ever.databinding.ListItemProductBinding
import de.markusressel.k4ever.rest.products.model.ProductModel
import de.markusressel.k4ever.view.fragment.base.ListFragmentBase
import de.markusressel.k4ever.view.fragment.base.SortOption
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment__recyclerview.*
import kotlinx.android.synthetic.main.layout__bottom_sheet__shopping_cart.*
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
    lateinit var shoppingCart: ShoppingCart

    private lateinit var shoppingCartBottomSheetBehaviour: BottomSheetBehavior<View>

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

        shoppingCartBottomSheetBehaviour = BottomSheetBehavior
                .from<View>(shoppingCartLayout)
        setCardViewPeekHeight()

        updateShoppingCart()

        shoppingCartBottomSheetBehaviour.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
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

    private fun setCardViewPeekHeight() {
        val elevation = shoppingCartCardView.maxCardElevation
        val radius = shoppingCartCardView.radius
        val cos45 = Math.cos(Math.toRadians(45.0))

        val horizontalPadding = (elevation + (1 - cos45) * radius).toInt()
//        val verticalPadding = (elevation * 1.5 + (1 - cos45) * radius).toInt()

        shoppingCartBottomSheetBehaviour.peekHeight =
                (resources.getDimension(R.dimen.shopping_bag__peek_height) +
                        horizontalPadding).toInt()
    }

    private fun updateShoppingCart(updateVisibility: Boolean = true) {
        if (updateVisibility) {
            updateShoppingCartVisibility()
        }
        updateShoppingCartContent()
    }

    private fun updateShoppingCartVisibility() {
        if (shoppingCart.items.isEmpty()) {
            setShoppingCartVisible(false)
        } else {
            setShoppingCartVisible(true)
        }
    }

    private fun setShoppingCartVisible(visible: Boolean) {
        shoppingCartBottomSheetBehaviour.isHideable = !visible

        if (visible) {
            // only open bottom sheet if is currently invisible
            // otherwise keep the current state
            if (shoppingCartBottomSheetBehaviour.state == BottomSheetBehavior.STATE_HIDDEN
                    || shoppingCartBottomSheetBehaviour.state == BottomSheetBehavior.STATE_EXPANDED
                    || shoppingCartBottomSheetBehaviour.state == BottomSheetBehavior.STATE_SETTLING) {
                shoppingCartBottomSheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        } else {
            shoppingCartBottomSheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun updateShoppingCartContent() {
        totalItemCountAndCost.text = getString(R.string.shopping_cart__total_items_and_cost,
                shoppingCart.getTotalItemCount(),
                shoppingCart.getTotalPrice())
        inflateShoppingCartItems()
    }

    private fun inflateShoppingCartItems() {
        // remove old views
        shoppingCartItemsLayout.removeAllViews()

        shoppingCart.items.forEach { shoppingBagItem ->
            val layoutInflater = LayoutInflater.from(context)
            val itemLayout = layoutInflater.inflate(R.layout.layout__cart_item, shoppingCartItemsLayout, false) as ViewGroup

            // TODO: Set item image
            val itemImage = itemLayout.findViewById(R.id.itemImage) as ImageView
//            itemImage.setImageDrawable()

            val itemCount = itemLayout.findViewById(R.id.itemCount) as TextView
            itemCount.text = "${shoppingBagItem.amount} Stück"

            val itemName = itemLayout.findViewById(R.id.itemName) as TextView
            itemName.text = shoppingBagItem.product.name

            val itemPrice = itemLayout.findViewById(R.id.itemPrice) as TextView
            itemPrice.text = getString(R.string.shopping_cart__item_cost, shoppingBagItem.getPrice())

            val buttonPlus = itemLayout.findViewById(R.id.buttonPlus) as Button
            RxView.clicks(buttonPlus)
                    .subscribeBy(onNext = {
                        addItemToShoppingCart(shoppingBagItem.product, shoppingBagItem.withDeposit, false)
                    })

            val buttonMinus = itemLayout.findViewById(R.id.buttonMinus) as Button
            RxView.clicks(buttonMinus)
                    .subscribeBy(onNext = {
                        removeItemFromShoppingCart(shoppingBagItem.product, shoppingBagItem.withDeposit, false)
                    })

            shoppingCartItemsLayout.addView(itemLayout)
        }
    }

    /**
     * Shows a detail view of the specified product
     */
    fun openDetailView(productEntity: ProductEntity) {
        // TODO:
    }

    /**
     * Adds the specified item to the shopping cart
     *
     * @param productEntity the product to add
     * @param withDeposit true, if deposit should be added to price
     * @param updateCartVisibility if set to true, the shopping cart bottom sheet visibility will be updated
     */
    fun addItemToShoppingCart(productEntity: ProductEntity, withDeposit: Boolean, updateCartVisibility: Boolean = true) {
        shoppingCart.add(productEntity, 1, withDeposit)
        updateShoppingCart(updateCartVisibility)
    }

    /**
     * Removes the specified item from the shopping cart
     *
     * @param productEntity the product to remove
     * @param withDeposit true, if deposit was added to price
     * @param updateCartVisibility if set to true, the shopping cart bottom sheet visibility will be updated
     */
    fun removeItemFromShoppingCart(productEntity: ProductEntity, withDeposit: Boolean, updateCartVisibility: Boolean = true) {
        shoppingCart.remove(productEntity, 1, withDeposit)
        updateShoppingCart(updateCartVisibility)
    }

}