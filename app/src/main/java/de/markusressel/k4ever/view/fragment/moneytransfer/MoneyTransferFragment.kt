package de.markusressel.k4ever.view.fragment.moneytransfer

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.annotation.CallSuper
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import de.markusressel.k4ever.R
import de.markusressel.k4ever.view.component.OptionsMenuComponent
import de.markusressel.k4ever.view.fragment.base.DaggerSupportFragmentBase
import kotlinx.android.synthetic.main.fragment__money_transfer.*
import java.text.DecimalFormat


/**
 * Server Status fragment
 *
 * Created by Markus on 07.01.2018.
 */
class MoneyTransferFragment : DaggerSupportFragmentBase() {

    override val layoutRes: Int
        get() = R.layout.fragment__money_transfer

    private val optionsMenuComponent: OptionsMenuComponent by lazy {
        OptionsMenuComponent(this, optionsMenuRes = R.menu.options_menu_none)
    }

    override fun initComponents(context: Context) {
        super
                .initComponents(context)
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

    @SuppressLint("ClickableViewAccessibility")
    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super
                .onViewCreated(view, savedInstanceState)

        money_amount_edittext.setText("0,00")
        money_amount_edittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // TODO: doesn't work as expected

                val cursorIndexBeforeChange = money_amount_edittext.selectionStart

                val df = DecimalFormat("####0.00")
                val sourceAsDouble = s.toString().replace(",", ".").toDouble()
                val result = df.format(sourceAsDouble).replace(".", ",")

                money_amount_edittext
                        .removeTextChangedListener(this)
                money_amount_edittext.setText(result)
                money_amount_edittext.setSelection(cursorIndexBeforeChange.coerceIn(0, result.length))
                money_amount_edittext
                        .addTextChangedListener(this)
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

}
