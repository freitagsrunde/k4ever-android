package de.markusressel.k4ever.view.fragment.products

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.github.nitrico.lastadapter.LastAdapter
import de.markusressel.k4ever.BR
import de.markusressel.k4ever.R
import de.markusressel.k4ever.business.ShoppingCart
import de.markusressel.k4ever.data.persistence.base.PersistenceManagerBase
import de.markusressel.k4ever.data.persistence.product.ProductEntity
import de.markusressel.k4ever.data.persistence.product.ProductPersistenceManager
import de.markusressel.k4ever.databinding.ListItemProductBinding
import de.markusressel.k4ever.rest.products.model.ProductModel
import de.markusressel.k4ever.view.fragment.base.ListFragmentBase
import de.markusressel.k4ever.view.fragment.base.SortOption
import io.reactivex.Single
import kotlinx.android.synthetic.main.fragment__recyclerview.*
import kotlinx.android.synthetic.main.layout__bottom_sheet__shopping_cart.*
import nl.dionsegijn.steppertouch.OnStepCallback
import nl.dionsegijn.steppertouch.StepperTouch
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

        val p1 = ProductModel(0, "Mio Mate", "Getränk der Studenten", 1.0, 0.2, true)
        val p2 = ProductModel(1, "Club Mate", "Getränk der Studenten", 0.8, 0.2, true)
        val p3 = ProductModel(2, "Cola", "Zucker", 1.0, 0.2, false)
        val p4 = ProductModel(3, "Spezi", "Zucker ^2", 1.0, 0.2, false)
        val p5 = ProductModel(4, "Snickers", "Zucker ^5", 1.0, 0.0, false)

        // TODO:
        return Single
                .just(listOf(p1, p2, p3, p4, p5).shuffled())
    }

    override fun mapToEntity(it: ProductModel): ProductEntity {
        return ProductEntity(0, it.id, it.name, it.description, it.price, it.deposit, it.isFavorite)
    }

    override fun getAllSortCriteria(): List<SortOption<ProductEntity>> {
        // TODO sort options need to be persistable
        return listOf(SortOption(0, R.string.favorite, { t -> !t.isFavorite }, false), SortOption(0, R.string.name, { t -> t.name }, false))
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
                .from<View>(shoppingCartCardView)
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
        if (shoppingCart.isEmpty() || updateVisibility) {
            updateShoppingCartVisibility()
        }
        updateShoppingCartContent()
    }

    private fun updateShoppingCartVisibility() {
        if (shoppingCart.items.isEmpty()) {
            setShoppingCartVisibility(false)
        } else {
            setShoppingCartVisibility(true)
        }
    }

    private fun setShoppingCartVisibility(visible: Boolean) {
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

    /**
     * Returns a nice text representation of the price of a product
     * @param product the product to use
     * @param withDeposit true, if deposit should be included in the text
     */
    fun getPriceString(product: ProductEntity, withDeposit: Boolean): String {
        return when {
            withDeposit -> getString(R.string.shopping_cart__item_cost_with_deposit, product.price, product.deposit)
            else -> getString(R.string.shopping_cart__item_cost, product.price)
        }
    }

    /**
     * Returns the visibility of the "buy with deposit" gui elements, based on a product
     */
    fun getBuyWithDepositVisibility(product: ProductEntity): Int {
        return when {
            product.deposit > 0 -> View.VISIBLE
            else -> View.GONE
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

            // TODO: Set real item image
            val itemImage = itemLayout.findViewById(R.id.itemImage) as ImageView
            itemImage.setImageDrawable(ContextCompat.getDrawable(context as Context, R.drawable.club_mate_0_5l))

            val itemName = itemLayout.findViewById(R.id.itemName) as TextView
            itemName.text = shoppingBagItem.product.name

            val itemPrice = itemLayout.findViewById(R.id.itemPrice) as TextView
            itemPrice.text = getPriceString(shoppingBagItem.product, shoppingBagItem.withDeposit)

            val itemAmountStepper = itemLayout.findViewById(R.id.itemAmountStepper) as StepperTouch
            itemAmountStepper.stepper.setMin(0)
            itemAmountStepper.stepper.setValue(shoppingBagItem.amount)
            itemAmountStepper.enableSideTap(true)
            itemAmountStepper.stepper.addStepCallback(object : OnStepCallback {
                override fun onStep(value: Int, positive: Boolean) {
                    setShoppingCardItemAmount(shoppingBagItem.product, shoppingBagItem.withDeposit, value, false)
                }
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
     * Adds the specified item to the shopping cart
     *
     * @param productEntity the product to add
     * @param withDeposit true, if deposit should be added to price
     * @param updateCartVisibility if set to true, the shopping cart bottom sheet visibility will be updated
     */
    fun setShoppingCardItemAmount(productEntity: ProductEntity, withDeposit: Boolean, amount: Int, updateCartVisibility: Boolean = true) {
        shoppingCart.set(productEntity, amount, withDeposit)
        updateShoppingCart(updateCartVisibility)
    }

    /**
     * Toggles the "favorite" state of a product
     */
    fun toggleFavorite(product: ProductEntity) {
        product.isFavorite = !product.isFavorite
        persistenceManager.standardOperation().put(product)

        updateListFromPersistence()

        // TODO: eventually send this to the server
    }

}