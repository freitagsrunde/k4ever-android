package de.markusressel.k4ever.view.fragment.products

import com.airbnb.epoxy.TypedEpoxyController
import de.markusressel.k4ever.business.ShoppingCartItem
import de.markusressel.k4ever.listItemCartItem
import kotlinx.android.synthetic.main.list_item__cart_item.view.*
import nl.dionsegijn.steppertouch.OnStepCallback

class ShoppingCartEpoxyController(val presenter: ProductsFragment) : TypedEpoxyController<List<ShoppingCartItem>>() {

    // map to remember the callbacks associated with a counter
    // (needed because the view has no support for removing all or replacing a listener)
    private val callbackMap = mutableMapOf<Any, OnStepCallback>()

    override fun buildModels(data: List<ShoppingCartItem>?) {
        data?.forEach {
            listItemCartItem {
                id(it.id)
                item(it)
                presenter(presenter)
                onBind { model, view, position ->
                    val stepper = view.dataBinding.root.productAmountStepper
                    stepper.enableSideTap(true)
                    stepper.stepper.setMin(0)

                    val cartItem = model.item()
                    cartItem?.let {
                        // remove any existing callback
                        callbackMap[stepper]?.let {
                            stepper.stepper.removeStepCallback(it)
                        }

                        stepper.stepper.setValue(it.amount)

                        // create new one to use correct product item
                        val callback = object : OnStepCallback {
                            override fun onStep(value: Int, positive: Boolean) {
                                presenter.setShoppingCardItemAmount({ cartItem.product }(),
                                        cartItem.withDeposit, value, false)
                            }
                        }
                        stepper.stepper.addStepCallback(callback)
                        callbackMap[stepper] = callback

                        stepper.invalidate()
                    }
                }
            }
        }
    }
}