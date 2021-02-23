package com.tarrakki.module.prime_investor

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.Observer
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.model.Fundd
import com.tarrakki.databinding.FragmentPrimeInvestorMutualFundListReviewBinding
import com.tarrakki.databinding.RowPrimeInvestorMutualFundListReviewBinding
import com.tarrakki.module.tarrakkipro.TarrakkiProBenefitsFragment
import kotlinx.android.synthetic.main.fragment_prime_investor_mutual_fund_list_review.*
import org.greenrobot.eventbus.EventBus
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.BaseAdapter
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.*

class PrimeInvestorMutualFundListReviewFragment : CoreFragment<PrimeInvestorMutualFundListReviewVM, FragmentPrimeInvestorMutualFundListReviewBinding>() {


    private var adapter: BaseAdapter<Fundd, RowPrimeInvestorMutualFundListReviewBinding>? = null

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.tarrakki_mf_assistant)

    override fun getLayout(): Int {
        return R.layout.fragment_prime_investor_mutual_fund_list_review
    }

    override fun createViewModel(): Class<out PrimeInvestorMutualFundListReviewVM> {
        return PrimeInvestorMutualFundListReviewVM::class.java
    }

    override fun setVM(binding: FragmentPrimeInvestorMutualFundListReviewBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {

        App.INSTANCE.primeInvestorList.sortBy { it?.name }

        txtSchemeName.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                val DRAWABLE_RIGHT = 2
                if (event.getAction() === MotionEvent.ACTION_DOWN) {
                    if (event.getRawX() >= txtSchemeName.getRight() - txtSchemeName.getCompoundDrawables().get(DRAWABLE_RIGHT).getBounds().width()) {

                        if (getViewModel().isASC) {
                            getViewModel().isASC = false
                            App.INSTANCE.primeInvestorList.sortByDescending { it?.name }
                        } else {
                            getViewModel().isASC = true
                            App.INSTANCE.primeInvestorList.sortBy { it?.name }
                        }
                        adapter?.notifyDataSetChanged()
                        return true
                    }
                }
                return false
            }

        })

        getViewModel().isLimitExceed.observe(this, Observer {
            if (it.isNullOrEmpty() == false) {
                context?.limitExceed(getString(R.string.app_name), it, positiveButton = {
                    startFragment(TarrakkiProBenefitsFragment.newInstance(), R.id.frmContainer)
                   // chatWhatsapp()
                }, btnTitle = getString(R.string.subscribe_now))
            }
        })

        adapter = rvScheme.setUpRecyclerView(
                R.layout.row_prime_investor_mutual_fund_list_review,
                App.INSTANCE.primeInvestorList) { item: Fundd?, binder: RowPrimeInvestorMutualFundListReviewBinding, position ->
            binder.setVariable(BR.vm, item)

            if (position % 2 == 0) {
                binder.llMutualFundList.setBackgroundColor(Color.WHITE)
            } else {
                binder.llMutualFundList.setBackgroundColor(Color.parseColor("#f9f9f9"))
            }

            binder.txtAdd.setOnClickListener {

                context?.confirmationDialog(
                        title = getString(R.string.scheme_name),
                        msg = getString(R.string.are_you_sure_you_want_to_remove) + " " + item?.name + "?",
                        btnPositive = getString(R.string.yes),
                        btnNegative = getString(R.string.no),
                        btnPositiveClick = {
                            App.INSTANCE.primeInvestorList.remove(item)
                            adapter?.notifyDataSetChanged()

                            if (App.INSTANCE.primeInvestorList.isEmpty()) {
                                getViewModel().tvNoDataFoundVisibility.set(View.VISIBLE)
                            }
                        },
                )

//                if (item?.isAdded == false) {
//                    getViewModel().schemaList.get(position)?.isAdded = true
//                    adapter?.notifyItemChanged(position)
//                }
            }

            binder.executePendingBindings()
        }

        llSaveForReview.setOnClickListener {
            if (!App.INSTANCE.primeInvestorList.isEmpty()) {
                getViewModel().getFundsReview().observe(this, Observer {
                    it?.let { response ->
                        response.data?.let { list ->
                            startFragment(PrimeInvestorMutualFundListRatingFragment.newInstance(Bundle().apply { putString("title", getString(R.string.tarrakki_mf_assistant)) }), R.id.frmContainer)
                            EventBus.getDefault().postSticky(list)
                        }
                    }
                })
            }
        }
    }

    private fun chatWhatsapp() {
        if (App.INSTANCE.getWhatsAppURI()?.isNotEmpty() == true) {
            try {
                val packageManager = requireActivity().packageManager
                val isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp")
                if (isWhatsappInstalled) {
                    try {
                        val i = Intent(Intent.ACTION_VIEW, Uri.parse(App.INSTANCE.getWhatsAppURI()))
                        i.setPackage("com.whatsapp")
                        if (i.resolveActivity(packageManager) != null) {
                            context?.startActivity(i)
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                } else {
                    val uri = Uri.parse("market://details?id=com.whatsapp")
                    val goToMarket = Intent(Intent.ACTION_VIEW, uri)
                    Toast.makeText(activity, "WhatsApp not Installed",
                            Toast.LENGTH_SHORT).show()
                    startActivity(goToMarket)
                }
            } catch (e: Exception) {
            }
        } else {
            toast("Please reload the home screen once to use this feature.")
        }
    }

    private fun whatsappInstalledOrNot(uri: String): Boolean {
        val pm: PackageManager? = activity?.getPackageManager()
        var appInstalled = false
        appInstalled = try {
            pm?.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
        return appInstalled
    }

//    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
//    fun onReceive(data: ListReview) {
//        getViewModel().schemaList.clear()
//        getViewModel().schemaList.addAll(data.list)
//        getViewModel().schemaList.sortBy { it?.name }
////        adapter?.notifyDataSetChanged()
//        EventBus.getDefault().removeStickyEvent(data)
//    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = PrimeInvestorMutualFundListReviewFragment().apply { arguments = basket }
    }

    class ListReview {
        var list: ArrayList<Fundd?> = ArrayList()
    }
}
