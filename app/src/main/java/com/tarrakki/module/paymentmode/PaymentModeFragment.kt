package com.tarrakki.module.paymentmode

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import com.tarrakki.R
import com.tarrakki.api.AES
import com.tarrakki.api.model.*
import com.tarrakki.databinding.FragmentPaymentModeBinding
import com.tarrakki.databinding.RowListPaymentFundsItemBinding
import com.tarrakki.module.bankmandate.AddBankMandateFragment
import com.tarrakki.module.netbanking.NET_BANKING_PAGE
import com.tarrakki.module.netbanking.NetBankingFragment
import com.tarrakki.module.transactionConfirm.TransactionConfirmFragment
import com.tarrakki.module.upi.UPIStepFragment
import com.tarrakki.module.webviewActivity.WebviewActivity
import kotlinx.android.synthetic.main.fragment_payment_mode.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONArray
import org.json.JSONObject
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.ChoiceMode
import org.supportcompact.adapters.KSelectionAdapter
import org.supportcompact.adapters.setUpAdapter
import org.supportcompact.events.Event
import org.supportcompact.ktx.*
import org.supportcompact.utilise.DividerItemDecoration
import org.supportcompact.utilise.ResourceUtils
import java.math.BigDecimal
import java.math.BigInteger

const val BANKACCOUNTNUMBER = "bankaccountnumber"
const val ISFROMPAYMENTMODE = "isFromPaymentMode"
const val SUCCESSTRANSACTION = "successtransactions"
const val SUCCESS_ORDERS = "success_orders"
const val ISFROMTRANSACTIONMODE = "isFromTransactionMode"
const val PAYMENT_MODE_NEFT_RTGS = "payment_mode_NEFT/RTGS"
const val TRANSACTION_IDS = "transaction_ids"

class PaymentModeFragment : CoreFragment<PaymentModeVM, FragmentPaymentModeBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.payment_mode)

    var isFromTransaction: Boolean? = false


    override fun getLayout(): Int {
        return R.layout.fragment_payment_mode
    }

    var confirmOrderAdapter: KSelectionAdapter<ConfirmTransactionResponse.Data.Order, RowListPaymentFundsItemBinding>? = null

    override fun createReference() {
        context?.let { DividerItemDecoration(it) }?.let { rvPaymentOrderItems?.addItemDecoration(it) }
        setHasOptionsMenu(true)
        getBinding().root.isFocusableInTouchMode = true
        getBinding().root.requestFocus()
        getBinding().root.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                onBackPress()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        getViewModel().isNetBanking.observe {
            if (it) {
                tvIntro.text = "Your transaction will now be processed by Bombay Stock Exchange - Star platform."
            }
        }
        getViewModel().isNEFTRTGS.observe {
            if (it) {
                val spannableString = SpannableString("Please follow the instructions listed in this link to initiate an NEFT/RTGS transfer and generate the UTR number. Then enter the UTR number below to proceed.")
                val end = 30
                val ssText = SpannableString(spannableString)
                val clickableSpan = object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        startActivity<WebviewActivity>()

                        //startFragment(WebViewFragment.newInstance(), R.id.frmContainer)
                        postSticky(Event.NEFTRTGS)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.color = getColor(R.color.colorAccent)!!
                        ds.isUnderlineText = false
                    }
                }
                ssText.setSpan(clickableSpan, 0, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                tvIntro.text = ssText
                tvIntro.movementMethod = LinkMovementMethod.getInstance()
                tvIntro.highlightColor = Color.TRANSPARENT
                tvIntro.isEnabled = true
            }
        }

        getViewModel().confirmOrder.observe(this, Observer { response ->
            getViewModel().totalOrder.set((response?.data?.totalPayableAmount))
            if (response?.data?.orders?.isNotEmpty() == true) {
                setOrderItemsAdapter(response.data.orders)
            }
        })

        edtHowToPay?.setOnClickListener {
            context?.showListDialog("", getViewModel().paymentType) { item ->

                if (getViewModel().availablePaymentMethodList.size <= 0) {
                    context?.simpleAlert("Selected payment method not available for all orders. Please select an alternate payment method or place both the orders separately.")
                } else {
                    when (item) {
                        ResourceUtils.getString(R.string.UPI) -> {
                            val order_ids: ArrayList<String> = arrayListOf()
                            getViewModel().validatePaymentData.value?.data?.validPaymentMethods?.forEach { validPaymentMethods ->
                                validPaymentMethods.paymentMethod.let {
                                    val list = it.filter { it.equals("UPI", true) }
                                    if (list.size <= 0) {
                                        order_ids.add(validPaymentMethods.fundName.toString())
                                        return@let
                                    }
                                }
                            }
/*
                        if (order_ids.size <= 0) {
                            getViewModel().isUPI.set(true)
                            getViewModel().isNetBanking.set(false)
                            getViewModel().isNEFTRTGS.set(false)
                            getViewModel().selectedPaymentType.set(item)
                        } else if (order_ids.size <= 2) {
                            context?.simpleAlert("Your order has been mapped to Netbanking. Kindly place a new order to pay via UPI.")
                        } else {
                            context?.simpleAlert("The below mentioned orders have been mapped to Netbanking. Kindly place a new order to pay via UPI.\n\n" + TextUtils.join("\n", order_ids))
                        }
*/

                            if (getViewModel().availablePaymentMethodList.size == 1) {
                                if (getViewModel().availablePaymentMethodList.contains("UPI")) {
                                    getViewModel().isUPI.set(true)
                                    getViewModel().isNetBanking.set(false)
                                    getViewModel().isNEFTRTGS.set(false)
                                    getViewModel().selectedPaymentType.set(item)
                                } else {
                                    if (getViewModel().validatePaymentData.value?.data?.validPaymentMethods?.size!! <= 1) {
//                                    context?.simpleAlert("Your order has been mapped to ${if (getViewModel().availablePaymentMethodList.get(0).equals("DIRECT")) "Netbanking." else getViewModel().availablePaymentMethodList.get(0)}. Kindly place a new order to pay via  ${if (getViewModel().availablePaymentMethodList.get(0).equals("DIRECT")) "Netbanking." else getViewModel().availablePaymentMethodList.get(0)}.")
                                        context?.simpleAlert("Your order has been mapped to NEFT/RTGS. Kindly place a new order to pay via UPI.")
                                    } else {
                                        context?.simpleAlert("The below mentioned orders have been mapped to NEFT/RTGS. Kindly place a new order to pay via UPI.\n\n ${TextUtils.join("\n", order_ids)}")
//                                    context?.simpleAlert("The below mentioned orders have been mapped to ${if (getViewModel().availablePaymentMethodList.get(0).equals("DIRECT")) "Netbanking." else getViewModel().availablePaymentMethodList.get(0)}. Kindly place a new order to pay via  ${if (getViewModel().availablePaymentMethodList.get(0).equals("DIRECT")) "Netbanking." else getViewModel().availablePaymentMethodList.get(0)}.\n\n ${TextUtils.join("\n", order_ids)}")
                                    }

                                }
                            } else if (getViewModel().availablePaymentMethodList.size == 2) {
                                if (getViewModel().availablePaymentMethodList.contains("UPI")) {
                                    getViewModel().isUPI.set(true)
                                    getViewModel().isNetBanking.set(false)
                                    getViewModel().isNEFTRTGS.set(false)
                                    getViewModel().selectedPaymentType.set(item)
                                } else {

                                    if (getViewModel().validatePaymentData.value?.data?.validPaymentMethods?.size!! <= 1) {
//                                    context?.simpleAlert("Your order has been mapped to ${if (getViewModel().availablePaymentMethodList.get(0).equals("DIRECT")) "Netbanking." else getViewModel().availablePaymentMethodList.get(0)}. or ${if (getViewModel().availablePaymentMethodList.get(1).equals("DIRECT")) "Netbanking." else getViewModel().availablePaymentMethodList.get(1)}. Kindly place a new order to pay via  ${if (getViewModel().availablePaymentMethodList.get(0).equals("DIRECT")) "Netbanking." else getViewModel().availablePaymentMethodList.get(0)} or ${if (getViewModel().availablePaymentMethodList.get(1).equals("DIRECT")) "Netbanking." else getViewModel().availablePaymentMethodList.get(1)}.")
                                        context?.simpleAlert("Your order has been mapped to NEFT/RTGS. Kindly place a new order to pay via UPI.")
                                    } else {
//                                    context?.simpleAlert("The below mentioned orders have been mapped to ${if (getViewModel().availablePaymentMethodList.get(0).equals("DIRECT")) "Netbanking." else getViewModel().availablePaymentMethodList.get(0)}. or ${if (getViewModel().availablePaymentMethodList.get(1).equals("DIRECT")) "Netbanking." else getViewModel().availablePaymentMethodList.get(1)}. Kindly place a new order to pay via  ${if (getViewModel().availablePaymentMethodList.get(0).equals("DIRECT")) "Netbanking." else getViewModel().availablePaymentMethodList.get(0)} or ${if (getViewModel().availablePaymentMethodList.get(1).equals("DIRECT")) "Netbanking." else getViewModel().availablePaymentMethodList.get(1)}.\n\n ${TextUtils.join("\n", order_ids)}")
                                        context?.simpleAlert("The below mentioned orders have been mapped to NEFT/RTGS. Kindly place a new order to pay via UPI.\n\n ${TextUtils.join("\n", order_ids)}")
                                    }


                                }

                            } else {
                                getViewModel().isUPI.set(true)
                                getViewModel().isNetBanking.set(false)
                                getViewModel().isNEFTRTGS.set(false)
                                getViewModel().selectedPaymentType.set(item)
                            }


                        }
                        ResourceUtils.getString(R.string.net_banking) -> {
                            val order_ids: ArrayList<String> = arrayListOf()

                            getViewModel().validatePaymentData.value?.data?.validPaymentMethods?.forEach { validPaymentMethods ->
                                validPaymentMethods.paymentMethod.let {
                                    val list = it.filter { it.equals("DIRECT", true) }
                                    if (list.size <= 0) {
                                        order_ids.add(validPaymentMethods.fundName.toString())
                                        return@let
                                    }
                                }
                            }
/*
                        if (order_ids.size <= 0) {
                            getViewModel().isUPI.set(false)
                            getViewModel().isNetBanking.set(true)
                            getViewModel().isNEFTRTGS.set(false)
                            getViewModel().selectedPaymentType.set(item)
                        } else if (order_ids.size <= 2) {
                            context?.simpleAlert("Your order has been mapped to NEFT/RTGS. Kindly place a new order to pay via UPI.")
                        } else {
                            context?.simpleAlert("The below mentioned orders have been mapped to NEFT/RTGS. Kindly place a new order to pay via UPI.\n\n" + TextUtils.join("\n", order_ids))
                        }
*/
                            if (getViewModel().availablePaymentMethodList.size == 1) {
                                if (getViewModel().availablePaymentMethodList.contains("DIRECT")) {
                                    getViewModel().isUPI.set(false)
                                    getViewModel().isNetBanking.set(true)
                                    getViewModel().isNEFTRTGS.set(false)
                                    getViewModel().selectedPaymentType.set(item)
                                } else {


                                    if (getViewModel().validatePaymentData.value?.data?.validPaymentMethods?.size!! <= 1) {
                                        context?.simpleAlert("Your order has been mapped to NEFT/RTGS. Kindly place a new order to pay via Net Banking.")
//                                    context?.simpleAlert("Your order has been mapped to NEFT/RTGS. Kindly place a new order to pay via  ${if (getViewModel().availablePaymentMethodList.get(0).equals("DIRECT")) "Netbanking." else getViewModel().availablePaymentMethodList.get(0)}.")
                                    } else {
                                        context?.simpleAlert("The below mentioned orders have been mapped to NEFT/RTGS. Kindly place a new order to pay via Net Banking.\n\n ${TextUtils.join("\n", order_ids)}")
//                                    context?.simpleAlert("The below mentioned orders have been mapped to NEFT/RTGS. Kindly place a new order to pay via  ${if (getViewModel().availablePaymentMethodList.get(0).equals("DIRECT")) "Netbanking." else getViewModel().availablePaymentMethodList.get(0)}.\n\n ${TextUtils.join("\n", order_ids)}")
                                    }

                                }
                            } else if (getViewModel().availablePaymentMethodList.size == 2) {
                                if (getViewModel().availablePaymentMethodList.contains("DIRECT")) {
                                    getViewModel().isUPI.set(false)
                                    getViewModel().isNetBanking.set(true)
                                    getViewModel().isNEFTRTGS.set(false)
                                    getViewModel().selectedPaymentType.set(item)
                                } else {

                                    if (getViewModel().validatePaymentData.value?.data?.validPaymentMethods?.size!! <= 1) {
//                                    if (getViewModel().availablePaymentMethodList.contains(ResourceUtils.getString(R.string.UPI))) {
//                                        val list = getViewModel().availablePaymentMethodList.filter { it.equals("UPI") }
//                                        context?.simpleAlert("Your order has been mapped to NEFT/RTGS. Kindly place a new order to pay via ${list.get(0)}.")
//                                    } else {
//                                        context?.simpleAlert("Your order has been mapped to NEFT/RTGS. Kindly place a new order to pay via  ${if (getViewModel().availablePaymentMethodList.get(0).equals("DIRECT")) "Netbanking." else getViewModel().availablePaymentMethodList.get(0)} or   ${if (getViewModel().availablePaymentMethodList.get(1).equals("DIRECT")) "Netbanking." else getViewModel().availablePaymentMethodList.get(1)}.")
//                                    }

                                        context?.simpleAlert("Your order has been mapped to NEFT/RTGS. Kindly place a new order to pay via Net Banking.")
                                    } else {
//                                    if (getViewModel().availablePaymentMethodList.contains(ResourceUtils.getString(R.string.UPI))) {
//                                        val list = getViewModel().availablePaymentMethodList.filter { it.equals("UPI") }
//                                        context?.simpleAlert("The below mentioned orders have been mapped to NEFT/RTGS. Kindly place a new order to pay via ${list.get(0)}.\n\n ${TextUtils.join("\n", order_ids)}")
//                                    } else {
//                                        context?.simpleAlert("The below mentioned orders have been mapped to NEFT/RTGS. Kindly place a new order to pay via ${if (getViewModel().availablePaymentMethodList.get(0).equals("DIRECT")) "Netbanking." else getViewModel().availablePaymentMethodList.get(0)} or ${if (getViewModel().availablePaymentMethodList.get(1).equals("DIRECT")) "Netbanking." else getViewModel().availablePaymentMethodList.get(1)}.\n\n ${TextUtils.join("\n", order_ids)}")
//                                    }

                                        context?.simpleAlert("The below mentioned orders have been mapped to NEFT/RTGS. Kindly place a new order to pay via Net Banking.\n\n ${TextUtils.join("\n", order_ids)}")
                                    }


                                }

                            } else {
                                getViewModel().isUPI.set(false)
                                getViewModel().isNetBanking.set(true)
                                getViewModel().isNEFTRTGS.set(false)
                                getViewModel().selectedPaymentType.set(item)
                            }


                        }
                        ResourceUtils.getString(R.string.neft_rtgs) -> {

                            val order_ids: ArrayList<String> = arrayListOf()
                            getViewModel().validatePaymentData.value?.data?.validPaymentMethods?.forEach { validPaymentMethods ->
                                validPaymentMethods.paymentMethod.let {
                                    val list = it.filter { it.equals("NEFT/RTGS", true) }
                                    if (list.size <= 0) {
                                        order_ids.add(validPaymentMethods.fundName.toString())
                                        return@let
                                    }


                                }
                            }
/*
                        if (order_ids.size <= 0) {
                            getViewModel().isUPI.set(false)
                            getViewModel().isNetBanking.set(false)
                            getViewModel().isNEFTRTGS.set(true)
                            getViewModel().selectedPaymentType.set(item)
                        } else if (order_ids.size <= 2) {
                            context?.simpleAlert("Your order has been mapped to Netbanking. Kindly place a new order to pay via UPI.")
                        } else {
                            context?.simpleAlert("The below mentioned orders have been mapped to Netbanking. Kindly place a new order to pay via UPI.\n\n" + TextUtils.join("\n", order_ids))
                        }
*/


                            if (getViewModel().availablePaymentMethodList.size == 1) {
                                if (getViewModel().availablePaymentMethodList.contains("NEFT/RTGS")) {
                                    getViewModel().isUPI.set(false)
                                    getViewModel().isNetBanking.set(false)
                                    getViewModel().isNEFTRTGS.set(true)
                                    getViewModel().selectedPaymentType.set(item)
                                } else {
                                    if (getViewModel().validatePaymentData.value?.data?.validPaymentMethods?.size!! <= 1) {
                                        context?.simpleAlert("Your order has been mapped to Net Banking/UPI. Kindly place a new order to pay via NEFT.")
                                    } else {
                                        context?.simpleAlert("The below mentioned orders have been mapped to Net Banking/UPI. Kindly place a new order to pay via NEFT.\n\n ${TextUtils.join("\n", order_ids)}")
                                    }

                                }
                            } else if (getViewModel().availablePaymentMethodList.size == 2) {
                                if (getViewModel().availablePaymentMethodList.contains("NEFT/RTGS")) {
                                    getViewModel().isUPI.set(false)
                                    getViewModel().isNetBanking.set(false)
                                    getViewModel().isNEFTRTGS.set(true)
                                    getViewModel().selectedPaymentType.set(item)
                                } else {

                                    if (getViewModel().validatePaymentData.value?.data?.validPaymentMethods?.size!! <= 1) {
                                        /*  if (getViewModel().availablePaymentMethodList.contains(ResourceUtils.getString(R.string.UPI))) {
                                              val list = getViewModel().availablePaymentMethodList.filter { it.equals("UPI") }
                                              context?.simpleAlert("Your order has been mapped to Netbanking. Kindly place a new order to pay via ${list.get(0)}.")
                                          } else {
                                              context?.simpleAlert("Your order has been mapped to Netbanking. Kindly place a new order to pay via  ${if (getViewModel().availablePaymentMethodList.get(0).equals("DIRECT")) "Netbanking." else getViewModel().availablePaymentMethodList.get(0)} or ${if (getViewModel().availablePaymentMethodList.get(1).equals("DIRECT")) "Netbanking." else getViewModel().availablePaymentMethodList.get(1)}.")
                                          }*/

                                        context?.simpleAlert("Your order has been mapped to Net Banking/UPI. Kindly place a new order to pay via NEFT.")
                                    } else {
/*
                                    if (getViewModel().availablePaymentMethodList.contains(ResourceUtils.getString(R.string.UPI))) {
                                        val list = getViewModel().availablePaymentMethodList.filter { it.equals("UPI") }
                                        context?.simpleAlert("The below mentioned orders have been mapped to Netbanking. Kindly place a new order to pay via ${list.get(0)}.\n\n ${TextUtils.join("\n", order_ids)}")
                                    } else {
                                        context?.simpleAlert("The below mentioned orders have been mapped to Netbanking. Kindly place a new order to pay via ${if (getViewModel().availablePaymentMethodList.get(0).equals("DIRECT")) "Netbanking." else getViewModel().availablePaymentMethodList.get(0)} or ${if (getViewModel().availablePaymentMethodList.get(1).equals("DIRECT")) "Netbanking." else getViewModel().availablePaymentMethodList.get(1)}.\n\n ${TextUtils.join("\n", order_ids)}")
                                    }
*/
                                        context?.simpleAlert("The below mentioned orders have been mapped to Net Banking/UPI. Kindly place a new order to pay via NEFT.\n\n ${TextUtils.join("\n", order_ids)}")
                                    }


                                }

                            } else {
                                getViewModel().isUPI.set(false)
                                getViewModel().isNetBanking.set(false)
                                getViewModel().isNEFTRTGS.set(true)
                                getViewModel().selectedPaymentType.set(item)
                            }

                        }
                    }

                }


            }

        }

        fun redirectToPayment() {

            val items = confirmOrderAdapter?.getAllItems()
            if (items?.isNotEmpty() == true) {
                if (!getViewModel().accountNumber.get().isNullOrEmpty()) {

                    val transaction = arrayListOf<Int>()
                    val transaction1 = arrayListOf<Int>()
                    for (funds in items) {
                        if (funds.lumpsumTransactionId != 0) {
                            transaction.add(funds.lumpsumTransactionId)
                            transaction1.add(funds.lumpsumTransactionId)
                        }
                        if (funds.sipTransactionId != 0) {
                            transaction.add(funds.sipTransactionId)
                            if ("Y".equals(funds.isFirstSIP, true)) {
                                transaction1.add(funds.sipTransactionId)
                            }
                        }
                    }
                    val response = getViewModel().confirmOrder.value
                    val json = JSONObject()
                    json.put("user_id", context?.getUserId())
                    json.put("total_payable_amount", response?.data?.totalPayableAmount.toString())
                    json.put("account_number", "${getViewModel().accountNumber.get()}")
                    json.put("transaction_ids", JSONArray(transaction1))
                    if (getViewModel().isNetBanking.get() == true) {
                        json.put("payment_mode", "DIRECT")
                        val authData = AES.encrypt(json.toString())
                        getViewModel().paymentOrder(authData).observe(this, Observer {
                            it?.let { response ->
                                val jsonObject = JSONObject("${response.data?.toDecrypt()}")
                                startFragment(NetBankingFragment.newInstance(Bundle().apply {
                                    putSerializable(NET_BANKING_PAGE, jsonObject.optString("data"))
                                    putString(SUCCESSTRANSACTION, transaction.toString())
                                    putString(SUCCESS_ORDERS, items.toJson())
                                    isFromTransaction?.let { it1 -> putBoolean(ISFROMTRANSACTIONMODE, it1) }
                                }), R.id.frmContainer)
                            }
                        })
                    } else if (getViewModel().isUPI.get() == true) {
                        if (!TextUtils.isEmpty(getViewModel().upiName.get())) {
                            json.put("payment_mode", "UPI")
                            json.put("upi_id", getViewModel().upiName.get())
                            val authData = AES.encrypt(json.toString())
                            getViewModel().paymentOrder(authData).observe(this, Observer {
                                it?.printResponse()
                                val bundle = Bundle().apply {
                                    if (isFromTransaction!!) {
                                        putString(TRANSACTION_IDS, JSONArray(getViewModel().order_ids).toString())
                                    } else {
                                        putString(TRANSACTION_IDS, JSONArray(transaction1).toString())
                                    }
                                    putString(SUCCESS_ORDERS, items.toJson())
                                    putString(SUCCESSTRANSACTION, transaction.toString())
                                    isFromTransaction?.let { it1 -> putBoolean(ISFROMTRANSACTIONMODE, it1) }
                                }
                                startFragment(UPIStepFragment.newInstance(bundle), R.id.frmContainer)
                            })
                        } else {
                            context?.simpleAlert(getString(R.string.alert_req_upi))
                        }
                    } else {
                        if (!TextUtils.isEmpty(getViewModel().utrNumber.get())) {
                            json.put("payment_mode", "NEFT/RTGS")
                            json.put("utr_number", getViewModel().utrNumber.get())
                            val authData = AES.encrypt(json.toString())
                            getViewModel().paymentOrder(authData).observe(this, Observer {
                                it?.printResponse()
                                val bundle = Bundle().apply {
                                    putBoolean(PAYMENT_MODE_NEFT_RTGS, true)
                                    putString(SUCCESS_ORDERS, items.toJson())
                                    putString(SUCCESSTRANSACTION, transaction.toString())
                                    isFromTransaction?.let { it1 -> putBoolean(ISFROMTRANSACTIONMODE, it1) }
                                }
                                startFragment(TransactionConfirmFragment.newInstance(bundle), R.id.frmContainer)
                            })
                        } else {
                            context?.simpleAlert(getString(R.string.alert_req_utr))
                        }
                    }
                    e("Plain Data=>", json.toString())
                } else {
                    context?.simpleAlert(getString(R.string.alert_req_bank))
                }
            }


        }

        btnPayNow?.setOnClickListener {

            val order_ids: ArrayList<String> = arrayListOf()
            getViewModel().validatePaymentData.value?.data?.validPaymentMethods?.forEach { validPaymentMethods ->
                validPaymentMethods.paymentMethod.let {
                    val list = it.filter { it.equals("UPI", true) }
                    if (list.size <= 0) {
                        order_ids.add(validPaymentMethods.fundName.toString())
                        return@let
                    }

                }
            }
            if (getViewModel().availablePaymentMethodList.size <= 0) {
                context?.simpleAlert("Selected payment method not available for all orders. Please select an alternate payment method or place both the orders separately.")
            } /*else if (getViewModel().availablePaymentMethodList.size == 1) {
                if (getViewModel().availablePaymentMethodList.contains("UPI")) {
                    redirectToPayment()
                } else {
                    if (getViewModel().validatePaymentData.value?.data?.validPaymentMethods?.size!! <= 1) {
                        context?.simpleAlert("Your order has been mapped to ${if (getViewModel().availablePaymentMethodList.get(0).equals("DIRECT")) "Netbanking." else getViewModel().availablePaymentMethodList.get(0)}. Kindly place a new order to pay via  ${if (getViewModel().availablePaymentMethodList.get(0).equals("DIRECT")) "Netbanking." else getViewModel().availablePaymentMethodList.get(0)}.")
                    } else {
                        context?.simpleAlert("The below mentioned orders have been mapped to ${if (getViewModel().availablePaymentMethodList.get(0).equals("DIRECT")) "Netbanking." else getViewModel().availablePaymentMethodList.get(0)}. Kindly place a new order to pay via  ${if (getViewModel().availablePaymentMethodList.get(0).equals("DIRECT")) "Netbanking." else getViewModel().availablePaymentMethodList.get(0)}.\n\n ${TextUtils.join("\n", order_ids)}")
                    }

                }
            } else if (getViewModel().availablePaymentMethodList.size == 2) {
                if (getViewModel().availablePaymentMethodList.contains("UPI")) {
                    redirectToPayment()
                } else {

                    if (getViewModel().validatePaymentData.value?.data?.validPaymentMethods?.size!! <= 1) {
                        context?.simpleAlert("Your order has been mapped to ${if (getViewModel().availablePaymentMethodList.get(0).equals("DIRECT")) "Netbanking." else getViewModel().availablePaymentMethodList.get(0)}. or ${if (getViewModel().availablePaymentMethodList.get(1).equals("DIRECT")) "Netbanking." else getViewModel().availablePaymentMethodList.get(1)}. Kindly place a new order to pay via  ${if (getViewModel().availablePaymentMethodList.get(0).equals("DIRECT")) "Netbanking." else getViewModel().availablePaymentMethodList.get(0)} or ${if (getViewModel().availablePaymentMethodList.get(1).equals("DIRECT")) "Netbanking." else getViewModel().availablePaymentMethodList.get(1)}.")
                    } else {
                        context?.simpleAlert("The below mentioned orders have been mapped to ${if (getViewModel().availablePaymentMethodList.get(0).equals("DIRECT")) "Netbanking." else getViewModel().availablePaymentMethodList.get(0)}. or ${if (getViewModel().availablePaymentMethodList.get(1).equals("DIRECT")) "Netbanking." else getViewModel().availablePaymentMethodList.get(1)}. Kindly place a new order to pay via  ${if (getViewModel().availablePaymentMethodList.get(0).equals("DIRECT")) "Netbanking." else getViewModel().availablePaymentMethodList.get(0)} or ${if (getViewModel().availablePaymentMethodList.get(1).equals("DIRECT")) "Netbanking." else getViewModel().availablePaymentMethodList.get(1)}.\n\n ${TextUtils.join("\n", order_ids)}")
                    }


                }

            }*/
            else if (getViewModel().selectedPaymentType.get().equals(ResourceUtils.getString(R.string.select))) {
                context?.simpleAlert(getString(R.string.please_select_payment_mode))
            } else {
                redirectToPayment()
            }


        }

        tvChangeBank?.setOnClickListener {
            val bundle = Bundle().apply {
                putBoolean(ISFROMPAYMENTMODE, true)
                putString(BANKACCOUNTNUMBER, getViewModel().accountNumber.get())
            }
            startFragment(AddBankMandateFragment.newInstance(bundle), R.id.frmContainer)
        }

        tvSelectBank?.setOnClickListener {
            val bundle = Bundle().apply {
                putBoolean(ISFROMPAYMENTMODE, true)
                putString(BANKACCOUNTNUMBER, getViewModel().accountNumber.get())
            }
            startFragment(AddBankMandateFragment.newInstance(bundle), R.id.frmContainer)
        }

        tvClearUPI?.setOnClickListener {
            getViewModel().upiName.set("")
        }

        rb_netbanking?.isChecked = true

    }

    override fun createViewModel(): Class<out PaymentModeVM> {
        return PaymentModeVM::class.java
    }

    override fun setVM(binding: FragmentPaymentModeBinding) {
        getBinding().vm = getViewModel()
        getBinding().executePendingBindings()
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = PaymentModeFragment().apply { arguments = basket }
    }

    fun getNotAvailablePaymentMode(availableMethod: ArrayList<String>) {
        if (availableMethod.size > 0) {
            when (availableMethod.size) {
                1 -> {
                    getViewModel().notAvailablePaymentMethodList.clear()

                    if (availableMethod.get(0).equals(ResourceUtils.getString(R.string.UPI)))
                        getViewModel().notAvailablePaymentMethodList.addAll(getViewModel().totalPaymentModeList.filter { !it.equals(ResourceUtils.getString(R.string.UPI), true) })
                    else if (availableMethod.get(0).equals(ResourceUtils.getString(R.string.direct)))
                        getViewModel().notAvailablePaymentMethodList.addAll(getViewModel().totalPaymentModeList.filter { !it.equals(ResourceUtils.getString(R.string.direct), true) })
                    else if (availableMethod.get(0).equals(ResourceUtils.getString(R.string.NEFT_rtgs)))
                        getViewModel().notAvailablePaymentMethodList.addAll(getViewModel().totalPaymentModeList.filter { !it.equals(ResourceUtils.getString(R.string.NEFT_rtgs), true) })

                }

                2 -> {
                    getViewModel().notAvailablePaymentMethodList.clear()


                }

                3 -> {
                    getViewModel().notAvailablePaymentMethodList.clear()
                }
            }
        }
    }

    fun getAvailablePaymentMode(notAvailableMethod: ArrayList<String>) {
        if (notAvailableMethod.size > 0) {
            when (notAvailableMethod.size) {
                1 -> {
                    getViewModel().availablePaymentMethodList.clear()
                    if (notAvailableMethod.get(0).equals(ResourceUtils.getString(R.string.UPI)))
                        getViewModel().availablePaymentMethodList.addAll(getViewModel().totalPaymentModeList.filter { !it.equals(ResourceUtils.getString(R.string.UPI), true) })
                    else if (notAvailableMethod.get(0).equals(ResourceUtils.getString(R.string.direct)))
                        getViewModel().availablePaymentMethodList.addAll(getViewModel().totalPaymentModeList.filter { !it.equals(ResourceUtils.getString(R.string.direct), true) })
                    else if (notAvailableMethod.get(0).equals(ResourceUtils.getString(R.string.NEFT_rtgs)))
                        getViewModel().availablePaymentMethodList.addAll(getViewModel().totalPaymentModeList.filter { !it.equals(ResourceUtils.getString(R.string.NEFT_rtgs), true) })

                }

                2 -> {
                    getViewModel().availablePaymentMethodList.clear()


                }

                3 -> {
                    getViewModel().availablePaymentMethodList.clear()
                }
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onReceive(data: ConfirmTransactionResponse) {
        getViewModel().confirmOrder.value = data

        if (getViewModel().confirmOrder.value?.data?.orders?.size!! > 0) {
            val transaction = arrayListOf<String>()
            val transaction1 = arrayListOf<String>()

            for (funds in getViewModel().confirmOrder.value?.data?.orders!!) {
                if (funds.lumpsumTransactionId != 0) {
                    transaction.add(funds.lumpsumTransactionId.toString())
                    transaction1.add(funds.lumpsumTransactionId.toString())
                }
                if (funds.sipTransactionId != 0) {
                    transaction.add(funds.sipTransactionId.toString())
                    if ("Y".equals(funds.isFirstSIP, true)) {
                        transaction1.add(funds.sipTransactionId.toString())
                    }
                }
            }

            getViewModel().order_ids.addAll(transaction1)

        }

        getViewModel().getOrderPaymentValidation().observe(this, Observer {
            it?.data?.let { bank ->
                //
//                val list: ArrayList<String> = arrayListOf()
//                list.add("DIRECT")
////                list.add("NEFT/RTGS")
//
//                it.data.validPaymentMethods.get(0).paymentMethod = list


                getViewModel().validatePaymentData.value = it

                getViewModel().validatePaymentData.value?.data?.validPaymentMethods?.forEach { validPaymentMethods ->
                    validPaymentMethods.paymentMethod.let { paymentMode ->
                        if (paymentMode.size > 0) {
                            when (paymentMode.size) {
                                1 -> {
                                    getViewModel().notAvailablePaymentMethodList.clear()
                                    getViewModel().availablePaymentMethodList.clear()
                                    getViewModel().availablePaymentMethodList.add(paymentMode.get(0))
                                    getNotAvailablePaymentMode(getViewModel().availablePaymentMethodList)
                                }
                                2 -> {
                                    getViewModel().notAvailablePaymentMethodList.clear()
                                    getViewModel().availablePaymentMethodList.clear()
                                    val listUPI = getViewModel().totalPaymentModeList.filter { paymentMode.get(0).equals("UPI") || paymentMode.get(1).equals("UPI") }
                                    val listDirect = getViewModel().totalPaymentModeList.filter { paymentMode.get(0).equals("DIRECT") || paymentMode.get(1).equals("DIRECT") }
                                    val listNEFT = getViewModel().totalPaymentModeList.filter { paymentMode.get(0).equals("NEFT/RTGS") || paymentMode.get(1).equals("NEFT/RTGS") }
                                    if (listUPI.size == 0) {
                                        getViewModel().notAvailablePaymentMethodList.add(ResourceUtils.getString(R.string.UPI))
                                    } else if (listDirect.size == 0) {
                                        getViewModel().notAvailablePaymentMethodList.add(ResourceUtils.getString(R.string.direct))
                                    } else if (listNEFT.size == 0) {
                                        getViewModel().notAvailablePaymentMethodList.add(ResourceUtils.getString(R.string.NEFT_rtgs))
                                    }
                                    getAvailablePaymentMode(getViewModel().notAvailablePaymentMethodList)
                                }
                                3 -> {
                                    getViewModel().availablePaymentMethodList.clear()
                                    getViewModel().notAvailablePaymentMethodList.clear()
                                    getViewModel().availablePaymentMethodList.addAll(paymentMode)
                                }

                            }
                        }
                    }
                }



                getViewModel().accountNumber.set(bank.bankDetails.accountNumber)
                getViewModel().branchName.set(bank.bankDetails.branchName)
                getViewModel().upiName.set(bank.vpaId)
            }
        })
        removeStickyEvent(data)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onReceive(data: BankDetail) {
        getViewModel().accountNumber.set(data.accountNumber)
        getViewModel().branchName.set(data.branchBankIdBankName)
        removeStickyEvent(data)
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onReceive(data: TransactionApiResponse.Transaction) {
        isFromTransaction = true

        val orderList: MutableList<ConfirmTransactionResponse.Data.Order> = mutableListOf()
        val failedTransactions = arrayListOf<TransactionStatus>()

        val sipAmount: String = if (data.type == "SIP") {
            data.amount.toString()
        } else {
            ""
        }
        val lumpsumAmount: String = if (data.type == "Lumpsum") {
            data.amount.toString()
        } else {
            ""
        }
        val sipTransactionId: Int = if (data.type == "SIP") {
            data.id ?: 0
        } else {
            0
        }
        val lumpsumTransactionId: Int = if (data.type == "Lumpsum") {
            data.id ?: 0
        } else {
            0
        }

        val id: String = if (data.id.toString().isNotEmpty()) {
            data.id.toString() ?: "0"
        } else {
            "0"
        }

        val transaction = ConfirmTransactionResponse.Data.Order(
                sipTransactionId = sipTransactionId, schemeName = data.name, lumpsumTransactionId = lumpsumTransactionId
                , lumpsum_amount = lumpsumAmount, sip_amount = sipAmount, order_id = id
        )
        transaction.isFirstSIP = if (sipTransactionId != 0) "Y" else ""
        orderList.add(transaction)

        val ConfirmResponseData = data.amount?.let { BigDecimal.valueOf(it).toBigInteger() }?.let {
            ConfirmTransactionResponse.Data(0,
                    "", "", orderList, it, failedTransactions)
        }

        val confirmTransactionResponse = ConfirmResponseData?.let { ConfirmTransactionResponse(it) }
        getViewModel().confirmOrder.value = confirmTransactionResponse
        if (confirmTransactionResponse?.data?.orders?.size!! > 0) {
            confirmTransactionResponse.data.orders.forEach { item ->
                getViewModel().order_ids.add(item.order_id.toString())
            }
        }
        getViewModel().getOrderPaymentValidation().observe(this, Observer {
            it?.data?.let { bank ->
                getViewModel().validatePaymentData.value = it

                getViewModel().validatePaymentData.value?.data?.validPaymentMethods?.forEach { validPaymentMethods ->
                    validPaymentMethods.paymentMethod.let { paymentMode ->
                        if (paymentMode.size > 0) {
                            when (paymentMode.size) {
                                1 -> {
                                    getViewModel().notAvailablePaymentMethodList.clear()
                                    getViewModel().availablePaymentMethodList.clear()
                                    getViewModel().availablePaymentMethodList.add(paymentMode.get(0))
                                    getNotAvailablePaymentMode(getViewModel().availablePaymentMethodList)
                                }
                                2 -> {
                                    getViewModel().notAvailablePaymentMethodList.clear()
                                    getViewModel().availablePaymentMethodList.clear()
                                    val listUPI = getViewModel().totalPaymentModeList.filter { paymentMode.get(0).equals("UPI") || paymentMode.get(1).equals("UPI") }
                                    val listDirect = getViewModel().totalPaymentModeList.filter { paymentMode.get(0).equals("DIRECT") || paymentMode.get(1).equals("DIRECT") }
                                    val listNEFT = getViewModel().totalPaymentModeList.filter { paymentMode.get(0).equals("NEFT/RTGS") || paymentMode.get(1).equals("NEFT/RTGS") }
                                    if (listUPI.size == 0) {
                                        getViewModel().notAvailablePaymentMethodList.add(ResourceUtils.getString(R.string.UPI))
                                    } else if (listDirect.size == 0) {
                                        getViewModel().notAvailablePaymentMethodList.add(ResourceUtils.getString(R.string.direct))
                                    } else if (listNEFT.size == 0) {
                                        getViewModel().notAvailablePaymentMethodList.add(ResourceUtils.getString(R.string.NEFT_rtgs))
                                    }
                                    getAvailablePaymentMode(getViewModel().notAvailablePaymentMethodList)
                                }
                                3 -> {
                                    getViewModel().availablePaymentMethodList.clear()
                                    getViewModel().notAvailablePaymentMethodList.clear()
                                    getViewModel().availablePaymentMethodList.addAll(paymentMode)
                                }

                            }
                        }
                    }
                }

                getViewModel().accountNumber.set(bank.bankDetails.accountNumber)
                getViewModel().branchName.set(bank.bankDetails.branchName)
                getViewModel().upiName.set(bank.vpaId)
            }
        })

//        getViewModel().accountNumber.set("")
//        getViewModel().branchName.set("")
        removeStickyEvent(data)
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onReceive(items: ArrayList<TransactionApiResponse.Transaction>) {
        isFromTransaction = true
        val orderList: MutableList<ConfirmTransactionResponse.Data.Order> = mutableListOf()
        val failedTransactions = arrayListOf<TransactionStatus>()
        var totalPayableAmount: BigInteger = BigInteger.ZERO
        items.forEach { data ->
            val sipAmount: String = if (data.type == "SIP") {
                data.amount.toString()
            } else {
                ""
            }
            val lumpsumAmount: String = if (data.type == "Lumpsum") {
                data.amount.toString()
            } else {
                ""
            }
            val sipTransactionId: Int = if (data.type == "SIP") {
                data.id ?: 0
            } else {
                0
            }
            val lumpsumTransactionId: Int = if (data.type == "Lumpsum") {
                data.id ?: 0
            } else {
                0
            }
            val id: String = if (data.id.toString().isNotEmpty()) {
                data.id.toString() ?: "0"
            } else {
                "0"
            }

            val transaction = ConfirmTransactionResponse.Data.Order(
                    sipTransactionId = sipTransactionId, schemeName = data.name, lumpsumTransactionId = lumpsumTransactionId
                    , lumpsum_amount = lumpsumAmount, sip_amount = sipAmount, order_id = id
            )
            transaction.isFirstSIP = if (sipTransactionId != 0) "Y" else ""
            orderList.add(transaction)
            data.amount?.let { BigDecimal.valueOf(it).toBigInteger() }?.let {
                totalPayableAmount += it
            }
        }
        getViewModel().confirmOrder.value = ConfirmTransactionResponse(ConfirmTransactionResponse.Data(orderList.size, "", "", orderList, totalPayableAmount, failedTransactions))
        if (getViewModel().confirmOrder.value?.data?.orders?.size!! > 0) {
            getViewModel().confirmOrder.value?.data?.orders?.forEach { item ->
                getViewModel().order_ids.add(item.order_id.toString())
            }
        }
        getViewModel().getOrderPaymentValidation().observe(this, Observer {
            it?.data?.let { bank ->
                getViewModel().validatePaymentData.value = it
                val paymentMethod: ArrayList<String> = arrayListOf()

                getViewModel().totalPaymentModeList.forEach payment@{ item ->
                    var isAvailable: Boolean = false
                    getViewModel().validatePaymentData.value?.data?.validPaymentMethods?.forEach { validPaymentMethod ->
                        if (validPaymentMethod.paymentMethod.contains(item)) {
                            isAvailable = true
                        } else {
                            isAvailable = false
                            return@payment
                        }
                    }

                    if (isAvailable) {
                        paymentMethod.add(item)
                    }
                }

//                val paymentMethod = StorageUtils.mostCommon(getViewModel().validatePaymentData.value?.data?.validPaymentMethods).paymentMethod


//                getViewModel().validatePaymentData.value?.data?.validPaymentMethods?.forEach { validPaymentMethods ->
                paymentMethod.let { paymentMode ->
                    if (paymentMode.size > 0) {
                        when (paymentMode.size) {
                            1 -> {
                                getViewModel().notAvailablePaymentMethodList.clear()
                                getViewModel().availablePaymentMethodList.clear()
                                getViewModel().availablePaymentMethodList.add(paymentMode.get(0))
                                getNotAvailablePaymentMode(getViewModel().availablePaymentMethodList)
                            }
                            2 -> {
                                getViewModel().notAvailablePaymentMethodList.clear()
                                getViewModel().availablePaymentMethodList.clear()
                                val listUPI = getViewModel().totalPaymentModeList.filter { paymentMode.get(0).equals("UPI") || paymentMode.get(1).equals("UPI") }
                                val listDirect = getViewModel().totalPaymentModeList.filter { paymentMode.get(0).equals("DIRECT") || paymentMode.get(1).equals("DIRECT") }
                                val listNEFT = getViewModel().totalPaymentModeList.filter { paymentMode.get(0).equals("NEFT/RTGS") || paymentMode.get(1).equals("NEFT/RTGS") }
                                if (listUPI.size == 0) {
                                    getViewModel().notAvailablePaymentMethodList.add(ResourceUtils.getString(R.string.UPI))
                                } else if (listDirect.size == 0) {
                                    getViewModel().notAvailablePaymentMethodList.add(ResourceUtils.getString(R.string.direct))
                                } else if (listNEFT.size == 0) {
                                    getViewModel().notAvailablePaymentMethodList.add(ResourceUtils.getString(R.string.NEFT_rtgs))
                                }
                                getAvailablePaymentMode(getViewModel().notAvailablePaymentMethodList)
                            }
                            3 -> {
                                getViewModel().availablePaymentMethodList.clear()
                                getViewModel().notAvailablePaymentMethodList.clear()
                                getViewModel().availablePaymentMethodList.addAll(paymentMode)
                            }

                        }
                    }
//                    }
                }

                getViewModel().accountNumber.set(bank.bankDetails.accountNumber)
                getViewModel().branchName.set(bank.bankDetails.branchName)
                getViewModel().upiName.set(bank.vpaId)
            }
        })

        removeStickyEvent(items)
    }

    private fun setOrderItemsAdapter(list: List<ConfirmTransactionResponse.Data.Order>) {
        confirmOrderAdapter = setUpAdapter(list as MutableList<ConfirmTransactionResponse.Data.Order>,
                ChoiceMode.NONE,
                R.layout.row_list_payment_funds_item,
                { item, binder: RowListPaymentFundsItemBinding?, position, adapter ->
                    binder?.widget = item
                    binder?.executePendingBindings()

                }, { item, position, adapter ->

        }, false)
        rvPaymentOrderItems?.adapter = confirmOrderAdapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPress()
                return super.onOptionsItemSelected(item)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun onBackPress() {
        if (isFromTransaction == true) {
            onBack(1)
        } else {
            onBack(2)
        }
    }
}
