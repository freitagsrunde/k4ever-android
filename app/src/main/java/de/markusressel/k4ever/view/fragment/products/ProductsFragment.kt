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
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.databinding.ViewDataBinding
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.paging.PagedListEpoxyController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import de.markusressel.k4ever.ListItemProductBindingModel_
import de.markusressel.k4ever.R
import de.markusressel.k4ever.business.ShoppingCart
import de.markusressel.k4ever.business.ShoppingCartItem
import de.markusressel.k4ever.data.persistence.base.PersistenceManagerBase
import de.markusressel.k4ever.data.persistence.product.ProductEntity
import de.markusressel.k4ever.data.persistence.product.ProductPersistenceManager
import de.markusressel.k4ever.extensions.common.android.context
import de.markusressel.k4ever.extensions.common.android.pxToSp
import de.markusressel.k4ever.extensions.data.toEntity
import de.markusressel.k4ever.rest.products.model.ProductModel
import de.markusressel.k4ever.view.activity.base.DetailActivityBase
import de.markusressel.k4ever.view.fragment.base.PersistableListFragmentBase
import de.markusressel.k4ever.view.fragment.base.SortOption
import kotlinx.android.synthetic.main.layout__bottom_sheet__shopping_cart.*
import kotlinx.android.synthetic.main.list_item__product.view.*
import javax.inject.Inject
import kotlin.math.cos


class ProductsFragment : PersistableListFragmentBase<ProductModel, ProductEntity>() {

    override val layoutRes: Int
        get() = R.layout.fragment__products

    @Inject
    lateinit var persistenceManager: ProductPersistenceManager

    override fun getPersistenceHandler(): PersistenceManagerBase<ProductEntity> = persistenceManager

    private val productsViewModel: ProductsViewModel by lazy {
        ViewModelProviders.of(this).get(ProductsViewModel::class.java)
    }

    override fun createViewDataBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): ViewDataBinding? {
        productsViewModel.getListLiveData(getPersistenceHandler()).observe(this, Observer {
            epoxyController.submitList(it)
        })

        return super.createViewDataBinding(inflater, container, savedInstanceState)
    }

    override fun createEpoxyController(): PagedListEpoxyController<ProductEntity> {
        return object : PagedListEpoxyController<ProductEntity>() {
            override fun buildItemModel(currentPosition: Int, item: ProductEntity?): EpoxyModel<*> {
                return if (item == null) {
                    ListItemProductBindingModel_()
                            .id(-currentPosition)
                } else {
                    ListItemProductBindingModel_()
                            .id(item.id)
                            .item(item)
                            .presenter(this@ProductsFragment)
                            .onBind { model, view, position ->
                                val productItem = model.item()

                                val productImage = view.dataBinding.root.productImage
                                val favoriteButton = view.dataBinding.root.favoriteButton

                                productItem?.let {
                                    productImage.setOnClickListener {
                                        favoriteButton.setChecked(!productItem.isFavorite, true)
                                    }

                                    favoriteButton.setOnCheckStateChangeListener { view, checked ->
                                        setFavorite(productItem, checked)
                                    }
                                }
                            }
                            .onclick { model, parentView, clickedView, position ->
                                openDetailView(model.item())
                            }
                }
            }
        }
    }

    override suspend fun loadListDataFromSource(): List<ProductModel> {
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

    private lateinit var shoppingCartItemList: MutableList<ShoppingCartItem>

    val normalPriceSize by lazy { totalItemCountAndCost.textSize.pxToSp(context()) }

    @SuppressLint("ClickableViewAccessibility")
    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initShoppingCart()
    }

    override fun onResume() {
        super.onResume()

        updateShoppingCart(0.0)
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
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
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

    private val shoppingCartEpoxyController by lazy { ShoppingCartEpoxyController(this) }

    private fun initShoppingCartList() {
        shoppingCartItemList = shoppingCart.items.toMutableList()

        shoppingCartItemsLayout.setController(shoppingCartEpoxyController)

        val layoutManager = LinearLayoutManager(context)
        shoppingCartItemsLayout.layoutManager = layoutManager
    }

    private fun setCardViewPeekHeight() {
        val elevation = shoppingCartCardView.maxCardElevation
        val radius = shoppingCartCardView.radius
        val cos45 = cos(Math.toRadians(45.0))

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

        if (notifyListAdapter) {
            // TODO: remove this intermediate list and use the shopping cart directly using livedata
            val newData = shoppingCart.items.toList()
            shoppingCartItemList.clear()
            shoppingCartItemList.addAll(newData)
            shoppingCartEpoxyController.setData(shoppingCartItemList)
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
                if (it == BottomSheetBehavior.STATE_HIDDEN) {
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
    private fun openDetailView(productEntity: ProductEntity) {
        val detailPage = DetailActivityBase.newInstanceIntent(ProductDetailActivity::class.java,
                context(), productEntity.id)
        startActivity(detailPage)
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
        val shoppingCartChanged = shoppingCart.set(productEntity, amount, withDeposit)

        if (shoppingCartChanged) {
            updateShoppingCart(oldTotalPrice = oldTotalPrice,
                    updateVisibility = updateCartVisibility, notifyListAdapter = amount == 0)
        }
    }

    /**
     * Toggles the "favorite" state of a product
     */
    fun setFavorite(product: ProductEntity, isFavorite: Boolean) {
        // changing state on model entities can break stuff, therefore we reload the entity from db
        val productEntity = persistenceManager.getStore().get(product.id)
        productEntity.isFavorite = isFavorite
        persistenceManager.getStore().put(productEntity)

        // TODO: maybe wait a short period before updating the list?

        // TODO: eventually send this to the server
    }

    companion object {
        const val TOTAL_PRICE_ANIMATION_DURATION: Long = 600
    }

}