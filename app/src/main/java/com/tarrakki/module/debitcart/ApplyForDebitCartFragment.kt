package com.tarrakki.module.debitcart


import android.os.Bundle
import androidx.fragment.app.Fragment
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.model.FolioData
import com.tarrakki.databinding.FragmentApplyForDebitCartBinding
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import kotlinx.android.synthetic.main.fragment_apply_for_debit_cart.*
import org.greenrobot.eventbus.Subscribe
import org.supportcompact.CoreFragment
import org.supportcompact.ktx.*
import java.text.DateFormatSymbols
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [ApplyForDebitCartFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ApplyForDebitCartFragment : CoreFragment<DebitCartInfoVM, FragmentApplyForDebitCartBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.apply_for_debit_cart)

    override fun getLayout(): Int {
        return R.layout.fragment_apply_for_debit_cart
    }

    override fun createViewModel(): Class<out DebitCartInfoVM> {
        return DebitCartInfoVM::class.java
    }

    override fun setVM(binding: FragmentApplyForDebitCartBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {

        edtChooseFolio?.setOnClickListener {
            context?.showCustomListDialog("Select Folio", getViewModel().folioData) { item ->
                getViewModel().folioNo.set(item.folioNo)
                edtChooseFolio?.text = item.folioNo
            }
        }

        edtDOB?.setOnClickListener {
            val now: Calendar = Calendar.getInstance()
            var Cdob: Calendar? = null
            var date: String? = getViewModel().dob.get()
            date?.toDate("dd/MM/yyyy")?.let { dob ->
                Cdob = dob.toCalendar()
            }
            val dPicker = SpinnerDatePickerDialogBuilder()
                    .context(context)
                    .callback { view, year, monthOfYear, dayOfMonth ->
                        date = String.format("%02d/%02d/%d", monthOfYear + 1, dayOfMonth, year)
                        getViewModel().dob.set(date)
                        edtDOB?.text = String.format("%02d %s, %d", dayOfMonth, DateFormatSymbols().months[monthOfYear].substring(0, 3), year)
                    }
                    .showTitle(true)
                    .maxDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
            if (Cdob != null) {
                Cdob?.let {
                    dPicker.defaultDate(it.get(Calendar.YEAR), it.get(Calendar.MONTH), it.get(Calendar.DAY_OF_MONTH))
                }
            } else {
                dPicker.defaultDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
            }
            dPicker.build().show()
        }

        btnApply?.setOnClickListener {
            if (isValid()) {
                getViewModel().applyForDebitCart().observe(this, androidx.lifecycle.Observer {
                    it?.let { apiResponse ->
                        context?.simpleAlert(App.INSTANCE.getString(R.string.success_), App.INSTANCE.getString(R.string.debit_cart_request_sent)) {
                            onBack(2)
                        }
                    }
                })
            }
        }
    }

    private fun isValid(): Boolean {
        return when {
            getViewModel().folioNo.isEmpty() -> {
                context?.simpleAlert(getString(R.string.alert_chhoose_folio_number))
                false
            }
            getViewModel().cardHolerName.isEmpty() -> {
                context?.simpleAlert(getString(R.string.alert_card_name_on_card))
                false
            }
            getViewModel().mothersName.isEmpty() -> {
                context?.simpleAlert(getString(R.string.alert_card_mothers_name))
                false
            }
            getViewModel().dob.isEmpty() -> {
                context?.simpleAlert(getString(R.string.alert_card_date_of_birth))
                false
            }
            else -> true
        }
    }

    @Subscribe(sticky = true)
    fun onReemFund(items: ArrayList<FolioData>) {
        if (getViewModel().folioData.isEmpty()) {
            getViewModel().folioData.addAll(items)
        }
        removeStickyEvent(items)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket as Bundle.
         * @return A new instance of fragment ApplyForDebitCartFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = ApplyForDebitCartFragment().apply { arguments = basket }
    }
}
