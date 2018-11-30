package com.tarrakki.module.bankmandate

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import com.tarrakki.BR
import com.tarrakki.R
import com.tarrakki.module.bankaccount.SingleButton
import org.supportcompact.FragmentViewModel
import org.supportcompact.adapters.WidgetsViewModel

class BankMandateWayVM : FragmentViewModel() {

    val bankMandateWays = arrayListOf<WidgetsViewModel>()

    init {
        bankMandateWays.add(BankMandateWay(
                R.string.sip_mandate,
                R.string.selft_authorize_to_bank,
                R.drawable.icon_isip,
                true))

        bankMandateWays.add(BankMandateWay(
                R.string.nach_mandate,
                R.string.we_provide_easiest_way_to,
                R.drawable.icon_nach))

        bankMandateWays.add(SingleButton(R.string.txtcontinue))
    }
}

data class BankMandateWay(
        @StringRes
        var title: Int,
        @StringRes
        var description: Int,
        @DrawableRes var
        imgRes: Int,
        var _isSelected: Boolean = false
) : BaseObservable(), WidgetsViewModel {

    override fun layoutId(): Int {
        return R.layout.row_bank_mandate_way
    }

    @get:Bindable
    var isSelected: Boolean = _isSelected
        set(value) {
            field = value
            notifyPropertyChanged(BR.selected)
        }
}