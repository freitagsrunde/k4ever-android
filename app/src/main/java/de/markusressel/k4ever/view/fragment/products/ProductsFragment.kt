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

package de.markusressel.k4ever.view.fragment.products

import android.animation.FloatEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.TypedValue
import android.view.View
import com.github.nitrico.lastadapter.LastAdapter
import de.markusressel.k4ever.BR
import de.markusressel.k4ever.R
import de.markusressel.k4ever.business.ShoppingCart
import de.markusressel.k4ever.business.ShoppingCartItem
import de.markusressel.k4ever.data.persistence.base.PersistenceManagerBase
import de.markusressel.k4ever.data.persistence.product.ProductEntity
import de.markusressel.k4ever.data.persistence.product.ProductPersistenceManager
import de.markusressel.k4ever.databinding.ListItemCartItemBinding
import de.markusressel.k4ever.databinding.ListItemProductBinding
import de.markusressel.k4ever.extensions.common.context
import de.markusressel.k4ever.extensions.common.pxToSp
import de.markusressel.k4ever.extensions.data.toEntity
import de.markusressel.k4ever.rest.products.model.ProductModel
import de.markusressel.k4ever.view.fragment.base.PersistableListFragmentBase
import de.markusressel.k4ever.view.fragment.base.SortOption
import io.reactivex.Single
import kotlinx.android.synthetic.main.fragment__recyclerview.*
import kotlinx.android.synthetic.main.layout__bottom_sheet__shopping_cart.*
import kotlinx.android.synthetic.main.list_item__cart_item.view.*
import kotlinx.android.synthetic.main.list_item__product.view.*
import nl.dionsegijn.steppertouch.OnStepCallback
import javax.inject.Inject


/**
 * Created by Markus on 07.01.2018.
 */
class ProductsFragment : PersistableListFragmentBase<ProductModel, ProductEntity>() {

    override val layoutRes: Int
        get() = R.layout.fragment__products

    @Inject
    lateinit var persistenceManager: ProductPersistenceManager

    override fun getPersistenceHandler(): PersistenceManagerBase<ProductEntity> = persistenceManager

    override fun createAdapter(): LastAdapter {
        return LastAdapter(listValues, BR.item).map<ProductEntity, ListItemProductBinding>(
                R.layout.list_item__product) {
            onCreate {
                it.binding.presenter = this@ProductsFragment
            }
            onBind {
                val productItem = it.binding.item

                val productImage = it.binding.root.productImage
                val favoriteButton = it.binding.root.favoriteButton

                productItem?.let {
                    productImage.setOnClickListener {
                        favoriteButton.setChecked(!productItem.isFavorite, true)
                    }
                    favoriteButton.setOnCheckStateChangeListener { view, checked ->
                        setFavorite(productItem, checked)
                    }
                }
            }
            onClick {
                openDetailView(listValues[it.adapterPosition])
            }
        }.into(recyclerView)
    }

    override fun loadListDataFromSource(): Single<List<ProductModel>> {
        return restClient.getAllProducts()
    }

    override fun mapToEntity(it: ProductModel): ProductEntity {
        return it.toEntity()
    }

    override fun getAllSortCriteria(): List<SortOption<ProductEntity>> {
        // TODO sort options need to be persistable
        return listOf(SortOption(0, R.string.favorite, { t -> !t.isFavorite }, false),
                SortOption(0, R.string.name, { t -> t.name }, false))
    }

    @Inject
    lateinit var shoppingCart: ShoppingCart

    private lateinit var shoppingCartBottomSheetBehaviour: BottomSheetBehavior<View>

    private lateinit var shoppingCartItemsAdapter: LastAdapter

    val normalPriceSize by lazy { totalItemCountAndCost.textSize.pxToSp(context()) }

    @SuppressLint("ClickableViewAccessibility")
    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initShoppingCart()
    }

    private fun initShoppingCart() {
        shoppingCartBottomSheetBehaviour = BottomSheetBehavior.from<View>(shoppingCartCardView)
        setCardViewPeekHeight()

        initShoppingCartList()
        updateShoppingCart(oldTotalPrice = 0.0, notifyListAdapter = false)

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

        shoppingCartTitlebar.setOnClickListener {
            onShoppingCartTitlebarClicked()
        }
    }

    private fun initShoppingCartList() {
        shoppingCartItemsAdapter = LastAdapter(shoppingCart.items,
                BR.item).map<ShoppingCartItem, ListItemCartItemBinding>(
                R.layout.list_item__cart_item) {
            onCreate {
                it.binding.presenter = this@ProductsFragment

                val stepper = it.binding.root.productAmountStepper
                stepper.enableSideTap(true)
                stepper.stepper.setMin(0)
                stepper.stepper.addStepCallback(object : OnStepCallback {
                    override fun onStep(value: Int, positive: Boolean) {
                        val cartItem = it.binding.item

                        setShoppingCardItemAmount(cartItem!!.product, cartItem.withDeposit, value,
                                false)
                    }
                })
            }
            onBind { holder ->
                val cartItem = holder.binding.item

                cartItem?.let {
                    val stepper = holder.binding.root.productAmountStepper
                    stepper.stepper.setValue(it.amount)
                }
            }
            onRecycle {
                //                val stepper = it.binding.root.productAmountStepper
                //                ???
                //                stepper.stepper.removeStepCallback()
            }
        }.into(shoppingCartItemsLayout)

        val layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        shoppingCartItemsLayout.layoutManager = layoutManager
    }

    private fun setCardViewPeekHeight() {
        val elevation = shoppingCartCardView.maxCardElevation
        val radius = shoppingCartCardView.radius
        val cos45 = Math.cos(Math.toRadians(45.0))

        val horizontalPadding = (elevation + (1 - cos45) * radius).toInt()
        //        val verticalPadding = (elevation * 1.5 + (1 - cos45) * radius).toInt()

        shoppingCartBottomSheetBehaviour.peekHeight = (resources.getDimension(
                R.dimen.shopping_bag__peek_height) + horizontalPadding).toInt()
    }

    private fun updateShoppingCart(oldTotalPrice: Double, updateVisibility: Boolean = true,
                                   notifyListAdapter: Boolean = true) {
        if (shoppingCart.isEmpty() || updateVisibility) {
            updateShoppingCartVisibility()
        }

        animateTotalItemCountAndCost(oldTotalPrice)

        // TODO: update items using diff/direct animations
        if (notifyListAdapter) {
            shoppingCartItemsAdapter.notifyDataSetChanged()
        }
    }

    private fun updateShoppingCartVisibility() {
        if (shoppingCart.items.isEmpty()) {
            setShoppingCartVisibility(false)
        } else {
            setShoppingCartVisibility(true)
        }
    }

    private fun onShoppingCartTitlebarClicked() {
        if (shoppingCartBottomSheetBehaviour.isHideable) {
            // should not be possible, shopping cart is hidden
            return
        }

        when (shoppingCartBottomSheetBehaviour.state) {
            BottomSheetBehavior.STATE_EXPANDED -> shoppingCartBottomSheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
            BottomSheetBehavior.STATE_COLLAPSED -> shoppingCartBottomSheetBehaviour.state = BottomSheetBehavior.STATE_EXPANDED
            else -> return
        }
    }

    private fun setShoppingCartVisibility(visible: Boolean) {
        shoppingCartBottomSheetBehaviour.isHideable = !visible

        if (visible) {
            // only open bottom sheet if is currently invisible
            // otherwise keep the current state
            shoppingCartBottomSheetBehaviour.state.let {
                if (it == BottomSheetBehavior.STATE_HIDDEN || it == BottomSheetBehavior.STATE_EXPANDED || it == BottomSheetBehavior.STATE_SETTLING) {
                    shoppingCartBottomSheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
        } else {
            shoppingCartBottomSheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    /**
     * Returns a nice text representation of the price of a product
     * @param product the product to use
     * @param withDeposit true, if deposit should be included, false if not
     */
    fun getPriceString(product: ProductEntity, withDeposit: Boolean): String {
        val price = if (withDeposit) {
            product.price + product.deposit
        } else {
            product.price
        }

        return getString(R.string.shopping_cart__item_cost, price)
    }

    /**
     * Returns a nice text representation of the price of a shopping cart item
     * @param shoppingCartItem the shopping cart item
     */
    fun getShoppingCartItemPriceString(shoppingCartItem: ShoppingCartItem): String {
        return getPriceString(shoppingCartItem.product, shoppingCartItem.withDeposit)
    }

    /**
     * Returns the visibility of the "buy with deposit" gui elements, based on a product
     */
    fun getBuyWithDepositVisibility(product: ProductEntity): Int {
        if (!preferencesHolder.showPricesWithDepositPreference.persistedValue) {
            return View.GONE
        }

        return when {
            product.deposit > 0 -> View.VISIBLE
            else -> View.GONE
        }
    }

    private fun animateTotalItemCountAndCost(oldTotalPrice: Double) {
        val totalPriceAnimator = ValueAnimator.ofObject(FloatEvaluator(), oldTotalPrice.toFloat(),
                shoppingCart.getTotalPrice())
        totalPriceAnimator.duration = TOTAL_PRICE_ANIMATION_DURATION
        totalPriceAnimator.addUpdateListener { animation ->
            totalItemCountAndCost.text = getString(R.string.shopping_cart__total_items_and_cost,
                    shoppingCart.getTotalItemCount(), animation.animatedValue as Float)
        }

        val totalPriceSizeAnimator = ValueAnimator.ofFloat(
                totalItemCountAndCost.textSize.pxToSp(context()), (normalPriceSize + 4),
                normalPriceSize)
        totalPriceSizeAnimator.duration = TOTAL_PRICE_ANIMATION_DURATION
        totalPriceSizeAnimator.interpolator = FastOutSlowInInterpolator()
        totalPriceSizeAnimator.addUpdateListener { animation ->
            totalItemCountAndCost.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                    animation.animatedValue as Float)
        }

        totalPriceAnimator.start()
        totalPriceSizeAnimator.start()
    }

    /**
     * Shows a detail view of the specified product
     */
    fun openDetailView(productEntity: ProductEntity) {
        val detailFragment = ProductDetailFragment.newInstance(productId = productEntity.entityId)
        // TODO:
    }

    /**
     * Adds the specified item to the shopping cart
     *
     * @param productEntity the product to add
     * @param withDeposit true, if deposit should be added to price
     * @param updateCartVisibility if set to true, the shopping cart bottom sheet visibility will be updated
     */
    fun addItemToShoppingCart(productEntity: ProductEntity, withDeposit: Boolean,
                              updateCartVisibility: Boolean = true) {
        val oldTotalPrice = shoppingCart.getTotalPrice()
        shoppingCart.add(productEntity, 1, withDeposit)
        updateShoppingCart(oldTotalPrice = oldTotalPrice, updateVisibility = updateCartVisibility)
    }

    /**
     * Adds the specified item to the shopping cart
     *
     * @param productEntity the product to add
     * @param withDeposit true, if deposit should be added to price
     * @param updateCartVisibility if set to true, the shopping cart bottom sheet visibility will be updated
     */
    fun setShoppingCardItemAmount(productEntity: ProductEntity, withDeposit: Boolean, amount: Int,
                                  updateCartVisibility: Boolean = true) {
        val oldTotalPrice = shoppingCart.getTotalPrice()
        shoppingCart.set(productEntity, amount, withDeposit)
        updateShoppingCart(oldTotalPrice = oldTotalPrice, updateVisibility = updateCartVisibility,
                notifyListAdapter = amount == 0)
    }

    /**
     * Toggles the "favorite" state of a product
     */
    fun setFavorite(product: ProductEntity, isFavorite: Boolean) {
        product.isFavorite = isFavorite
        persistenceManager.getStore().put(product)


        // TODO: maybe wait a short period before updating the list?

        updateListFromPersistence()

        // TODO: eventually send this to the server
    }

    companion object {
        const val TOTAL_PRICE_ANIMATION_DURATION: Long = 600
    }

}