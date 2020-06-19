package com.tarrakki.module.debitcart


import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.gocashfree.cashfreesdk.CFPaymentService
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.TarrakkiSingleton
import com.tarrakki.api.model.FolioData
import com.tarrakki.databinding.FragmentApplyForDebitCartBinding
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import kotlinx.android.synthetic.main.fragment_apply_for_debit_cart.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
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
class ApplyForDebitCartFragment : CoreFragment<DebitCartInfoVM, FragmentApplyForDebitCartBinding>()/*, PaymentResponseListener*/ {

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

        if (TarrakkiSingleton.getInstance().debitCardAddress.isNotEmpty()) {
            val address = TarrakkiSingleton.getInstance().debitCardAddress
            val addressSplit = address.split(" ")
            getViewModel().formattedAddress.set("${addressSplit[0]} ${addressSplit[1]} ${addressSplit[2]} XXXX")
        }

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
            // Call Payment Token API
            if (isValid()) {
//                val stage = "PROD"
                val stage = "TEST"
                getViewModel().getPaymentTokenAPI().observe(this, androidx.lifecycle.Observer {
                    val params: MutableMap<String, String> = HashMap()

                    params[CFPaymentService.PARAM_APP_ID] = "7996f54418f5378b2f70668f6997"  // STG APP ID
//                    params[CFPaymentService.PARAM_APP_ID] = "23824e9bcfb6946347bb6c9de42832"  // LIVE APP ID
                    params[CFPaymentService.PARAM_ORDER_ID] = it.data.orderId
                    params[CFPaymentService.PARAM_ORDER_AMOUNT] = it.data.amount
                    params[CFPaymentService.PARAM_ORDER_NOTE] = "orderNote"
                    params[CFPaymentService.PARAM_CUSTOMER_NAME] = "Sajan Gandhi"
                    params[CFPaymentService.PARAM_CUSTOMER_PHONE] = "${App.INSTANCE.getMobile()}"
                    params[CFPaymentService.PARAM_CUSTOMER_EMAIL] = "${App.INSTANCE.getEmail()}"
                    params[CFPaymentService.PARAM_NOTIFY_URL] = it.data.callbackUrl
                    params[CFPaymentService.PARAM_PAYMENT_OPTION] = ""
                    params[CFPaymentService.PARAM_PAYMENT_MODES] = ""

                    val cfPaymentService = CFPaymentService.getCFPaymentServiceInstance()
                    cfPaymentService.setOrientation(0)
                    cfPaymentService.doPayment(
                            requireActivity(),
                            params,
                            it.data.cftoken,
                            stage,
                            "#" + Integer.toHexString(ContextCompat.getColor(App.INSTANCE, R.color.colorPrimary)),
                            "#" + Integer.toHexString(ContextCompat.getColor(App.INSTANCE, R.color.colorPrimaryDark)),
                            false
                    )
                })
            }
            //TODO : Call below code after successfull payment
//            if (isValid()) {
//                getViewModel().applyForDebitCart().observe(this, androidx.lifecycle.Observer {
//                    it?.let { apiResponse ->
//                        context?.simpleAlert(App.INSTANCE.getString(R.string.success_), App.INSTANCE.getString(R.string.debit_cart_request_sent)) {
//                            onBack(2)
//                        }
//                    }
//                })
//            }
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

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onEvent(event: Bundle) {
        if (event.getString("txStatus").equals("SUCCESS", true)) {
//            getViewModel().applyForDebitCart().observe(this, androidx.lifecycle.Observer {
//                it?.let { apiResponse ->
//                    context?.simpleAlert(App.INSTANCE.getString(R.string.success_), App.INSTANCE.getString(R.string.debit_cart_request_sent)) {
//                        onBack(2)
//                    }
//                }
//            })
            postError("SUCCESS")
        } else {
            postError(event.getString("txMsg"))
        }
        removeStickyEvent(event)
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
