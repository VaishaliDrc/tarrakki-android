package com.tarrakki.module.bankmandate


import android.Manifest
import androidx.lifecycle.Observer
import android.os.Bundle
import android.view.View
import com.google.gson.Gson
import com.tarrakki.*
import com.tarrakki.api.model.*
import com.tarrakki.databinding.FragmentBankMandateBinding
import com.tarrakki.databinding.RowBankMandateListItemBinding
import com.tarrakki.databinding.RowUserBankListMandateBinding
import com.tarrakki.module.bankaccount.AddBankAccountFragment
import kotlinx.android.synthetic.main.fragment_bank_mandate.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.ChoiceMode
import org.supportcompact.adapters.KSelectionAdapter
import org.supportcompact.adapters.setUpAdapter
import org.supportcompact.events.Event
import org.supportcompact.events.ShowError
import org.supportcompact.ktx.checkSelfPermissions
import org.supportcompact.ktx.confirmationDialog
import org.supportcompact.ktx.simpleAlert
import org.supportcompact.ktx.startFragment

const val ISFROMCONFIRMORDER = "isfromconfirmOrder"
const val ISFROMDIRECTBANKMANDATE = "isfromdirectbankmandate"
const val MANDATEID = "mandateid"
const val MANDATE_ID = "mandate_id"

class BankMandateFragment : CoreFragment<BankMandateVM, FragmentBankMandateBinding>() {

    private var isMandate: Boolean? = null
    private var isConfirmOrder: Boolean? = null

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.bank_mandate)

    var mandateBankAdapter: KSelectionAdapter<UserBankMandateResponse.Data, RowBankMandateListItemBinding>? = null
    var userBankAdapter: KSelectionAdapter<BankDetail, RowUserBankListMandateBinding>? = null

    override fun getLayout(): Int {
        return R.layout.fragment_bank_mandate
    }

    override fun createViewModel(): Class<out BankMandateVM> {
        return BankMandateVM::class.java
    }

    override fun setVM(binding: FragmentBankMandateBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        isConfirmOrder = arguments?.getBoolean(ISFROMCONFIRMORDER, false)

        App.INSTANCE.isRefreshing.observe(this, Observer {
            it?.let { isRefreshing ->
                mRefresh?.isRefreshing = false
                App.INSTANCE.isRefreshing.value = null
            }
        })

        mRefresh?.setOnRefreshListener {
            getBanksData(true)
        }

        btnNext?.setOnClickListener {
            if (getViewModel().isMandateBankList.get() != true) {
                if (userBankAdapter?.selectedItemViewCount ?: 0 > 0) {
                    startFragment(AutoDebitFragment.newInstance(), R.id.frmContainer)
                    postSticky(userBankAdapter?.getSelectedItems()?.get(0) as BankDetail)
                    postSticky(Event.ISFROMBANKMANDATE)
                } else {
                    if (userBankAdapter?.items?.havingVerifiedBanks() == false) {
                        context?.simpleAlert(getString(R.string.alert_account_not_verified))
                        return@setOnClickListener
                    }
                    context?.simpleAlert(getString(R.string.alert_req_bank))
                }
            }
        }
        btnAdd?.setOnClickListener {
            if (isMandate == false) {
                startFragment(AddBankAccountFragment.newInstance(Bundle().apply {
                    putSerializable(IS_FROM_BANK_ACCOUNT, false)
                }), R.id.frmContainer)
            } else {
                startFragment(AddBankMandateFragment.newInstance(), R.id.frmContainer)
            }
        }

        btnSelectBankMandate?.setOnClickListener {
            if (mandateBankAdapter?.selectedItemViewCount ?: 0 > 0) {
                onBack()
                mandateBankAdapter?.getSelectedItems()?.get(0)?.let { it1 -> postSticky(it1) }
            } else {
                context?.simpleAlert(getString(R.string.alert_req_bank))
            }
        }
    }

    private fun getBanksData(isRefreshing: Boolean = false) {
        getViewModel().getAllMandateBanks(isRefreshing).observe(this, Observer {
            if (it?.data?.isNotEmpty() == true) {
                isMandate = true
                getViewModel().isNoBankAccount.set(false)
                getViewModel().isNextVisible.set(false)
                getViewModel().isAddVisible.set(true)
                setUserBankMandateAdapter(it.data)
            } else {
                isMandate = false
                getUserBankAPI(isRefreshing)
            }
        })
    }

    fun getUserBankAPI(isRefreshing: Boolean = false) {
        getViewModel().getAllBanks().observe(this, Observer { it1 ->
            getViewModel().isAddVisible.set(true)
            if (it1?.data?.bankDetails?.isNotEmpty() == true) {
                if (it1.data.bankDetails.size > 5) {
                    btnAdd?.visibility = View.GONE
                }
                btnAdd?.text = getString(R.string.add_new_bank_account)
                getViewModel().isNoBankAccount.set(false)
                getViewModel().isNextVisible.set(true)
                setUserBankAdapter(it1)
            } else {
                btnAdd?.text = getString(R.string.add_new_bank_account)
                getViewModel().isNoBankAccount.set(true)
                getViewModel().isNextVisible.set(false)
                getViewModel().isSelectBankVisible.set(false)
            }
        })
    }

    private fun setUserBankMandateAdapter(bankDetails: List<UserBankMandateResponse.Data>) {
        getViewModel().isMandateBankList.set(true)
        mandateBankAdapter = setUpAdapter(bankDetails as MutableList<UserBankMandateResponse.Data>,
                ChoiceMode.SINGLE,
                R.layout.row_bank_mandate_list_item,
                { item, binder: RowBankMandateListItemBinding?, position, adapter ->
                    binder?.widget = item

                    if (isConfirmOrder == true) {
                        binder?.tvDefault?.visibility = View.VISIBLE
                    } else {
                        binder?.tvDefault?.visibility = View.GONE
                    }

                    binder?.isSelected = adapter.isItemViewToggled(position)
                    binder?.tvPending?.setBackgroundResource(item.statuscolor)
                    binder?.btnUploadSanned?.setOnClickListener {
                        if (item.mandateType.equals("I")) { // is ISIP mandate
                            getViewModel().getISIPMandateData(item.id.toString()).observe(this, Observer {
                                it?.let {
                                    val data = JSONObject(it.data?.toDecrypt())
                                    val bundle = Bundle().apply {
                                        putBoolean(ISIPMANDATE, true)
                                        putBoolean(ISFROMDIRECTBANKMANDATE, true)
                                        putSerializable(IMANDATEDATA, data.optString("data"))
                                    }
                                    startFragment(BankMandateFormFragment.newInstance(bundle), R.id.frmContainer)
                                }
                            })
                            return@setOnClickListener
                        }else if(item.mandateType.equals("N")){
                            getViewModel().getMandateUserAuth(item.id.toString()).observe(this, Observer {
                                it?.let {
                                    val data = JSONObject(it.data?.toDecrypt())
                                    if (it.status?.code == 1) {
                                        context?.confirmationDialog(getString(R.string.send_authentication_email_success_msg), btnPositiveClick = {

                                        })
                                    }else
                                        EventBus.getDefault().post(ShowError(App.INSTANCE.getString(R.string.try_again_to)))
                                }
                            })
                            return@setOnClickListener
                        }

                        val bundle = Bundle().apply {
                            putBoolean(ISFROMDIRECTBANKMANDATE, true)
                            putString(MANDATEID, item.id.toString())
                            putString(MANDATE_ID, "${item.mandateId}")
                        }
                        startFragment(BankMandateFormFragment.newInstance(bundle), R.id.frmContainer)
                        repostSticky(item.bankDetails)
                    }

                    binder?.executePendingBindings()
                }, { item, position, adapter ->

        })
        rvBankMandate?.adapter = mandateBankAdapter
        if (isConfirmOrder == true) {
            getViewModel().isSelectBankVisible.set(true)
            val bankIndex = bankDetails.indexOfFirst { "${arguments?.getString(MANDATEID)}".equals("${it.id}", true) }
            mandateBankAdapter?.toggleItemView(bankIndex)
            mandateBankAdapter?.notifyItemChanged(bankIndex)
        } else {
            mandateBankAdapter?.toggleItemView(0)
            mandateBankAdapter?.notifyItemChanged(0)
        }
    }

    private fun setUserBankAdapter(banRes: UserBanksResponse) {
        val bankDetails: List<BankDetail> = banRes.data.bankDetails
        getViewModel().isMandateBankList.set(false)
        userBankAdapter = setUpAdapter(bankDetails as MutableList<BankDetail>,
                ChoiceMode.SINGLE,
                R.layout.row_user_bank_list_mandate,
                { item, binder: RowUserBankListMandateBinding?, position, adapter ->
                    binder?.widget = item
                    binder?.executePendingBindings()
                    binder?.isSelected = adapter.isItemViewToggled(position)
                    binder?.setVariable(BR.onUpdate, View.OnClickListener {
                        val bundle = Bundle()
                        banRes.data.bankDetail = item
                        bundle.putString("userBankData", Gson().toJson(banRes))
                        startFragment(AddBankAccountFragment.newInstance(bundle.apply { putSerializable(IS_FROM_BANK_ACCOUNT, true) }), R.id.frmContainer)
                    })
                },
                { item, position, adapter ->
                    if (item.checkBoxVisibility == View.VISIBLE) {
                        adapter.toggleItemView(position)
                    }
                },
                false
        )
        rvBankMandate?.adapter = userBankAdapter
        val default_position = bankDetails.indexOfFirst { it.isValid }
        if (default_position != -1) {
            userBankAdapter?.toggleItemView(default_position)
            userBankAdapter?.notifyItemChanged(default_position)
        } /*else {
            userBankAdapter?.toggleItemView(0)
            userBankAdapter?.notifyItemChanged(0)
        }*/
    }

    override fun onResume() {
        getBanksData()
        super.onResume()
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = BankMandateFragment().apply { arguments = basket }
    }
}
