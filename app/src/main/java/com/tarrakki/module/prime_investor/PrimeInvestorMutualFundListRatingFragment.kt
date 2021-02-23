package com.tarrakki.module.prime_investor

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.Observer
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.model.RatingList
import com.tarrakki.databinding.FragmentPrimeInvestorMutualFundListRatingBinding
import com.tarrakki.databinding.RowPrimeInvestorMutualFundListRatingBinding
import com.tarrakki.module.tarrakkipro.TarrakkiProBenefitsFragment
import kotlinx.android.synthetic.main.fragment_prime_investor_mutual_fund_list_rating.*
import kotlinx.android.synthetic.main.fragment_prime_investor_mutual_fund_list_review.rvScheme
import kotlinx.android.synthetic.main.row_prime_investor_mutual_fund_list_rating.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.BaseAdapter
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.events.Event
import org.supportcompact.ktx.*

class PrimeInvestorMutualFundListRatingFragment : CoreFragment<PrimeInvestorMutualFundListRatingVM, FragmentPrimeInvestorMutualFundListRatingBinding>() {


    private var isDialogVisible: Boolean = false
    private var adapter: BaseAdapter<RatingList, RowPrimeInvestorMutualFundListRatingBinding>? = null
    var isFragmentLoaded = false
    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = arguments?.getString("title")
                ?: getString(R.string.fund_details)//getString(R.string.tarrakki_mf_assistant)

    override fun getLayout(): Int {
        return R.layout.fragment_prime_investor_mutual_fund_list_rating
    }

    override fun createViewModel(): Class<out PrimeInvestorMutualFundListRatingVM> {
        return PrimeInvestorMutualFundListRatingVM::class.java
    }

    override fun setVM(binding: FragmentPrimeInvestorMutualFundListRatingBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {

        adapter = rvScheme.setUpRecyclerView(
                R.layout.row_prime_investor_mutual_fund_list_rating,
                getViewModel().schemaRatingList) { item: RatingList?, binder: RowPrimeInvestorMutualFundListRatingBinding, position ->
            binder.setVariable(BR.vm, item)
            val rating = item?.primeRating?.toFloatOrNull()
            if(rating == null){
                binder.tvRatingEmpty.visibility = View.VISIBLE
                binder.ratingBar.visibility = View.GONE
                binder.tvRatingEmpty.text = item?.primeRating
            }
            else{
                if(rating == 0.0f){
                    binder.tvRatingEmpty.visibility = View.VISIBLE
                    binder.ratingBar.visibility = View.GONE
                    binder.tvRatingEmpty.text = "Unrated"
                }
                else{
                    binder.tvRatingEmpty.visibility = View.GONE
                    binder.ratingBar.visibility = View.VISIBLE
                    binder.ratingBar.rating = rating
                }
            }

            binder.executePendingBindings()
        }

        txtTerms.setOnClickListener {
            val httpIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.primeinvestor.in/disclosures-and-disclaimers/"))
            startActivity(httpIntent)
        }

        getViewModel().isLimitExceed.observe(this, Observer {
            if (it.isNullOrEmpty() == false) {
                context?.limitExceed(getString(R.string.app_name), it, positiveButton = {
                    startFragment(TarrakkiProBenefitsFragment.newInstance(), R.id.frmContainer)
                   // chatWhatsapp()
                }, btnTitle = getString(R.string.subscribe_now))
            }
        })
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onReceive(data: ArrayList<RatingList?>) {
        getViewModel().schemaRatingList.clear()
        getViewModel().schemaRatingList.addAll(data)
//        adapter?.notifyDataSetChanged()
        EventBus.getDefault().removeStickyEvent(data)
    }

    override fun onResume() {
        super.onResume()

        if(getViewModel().schemaRatingList.isEmpty()){
            arguments?.let {

                val id = it?.getString("id")
                id?.let {
                    if (isFragmentLoaded && (context?.getTarakkiPro() == true)){
                        getViewModel()?.getFundsReview(id).observe(this, Observer {
                            it?.let { response ->
                                response.data?.let { list ->
                                    getViewModel().schemaRatingList.clear()
                                    getViewModel().schemaRatingList.addAll(list)
                                    adapter?.notifyDataSetChanged()
//                                startFragment(PrimeInvestorMutualFundListRatingFragment.newInstance(Bundle().apply { putString("title",getString(R.string.tarrakki_mf_assistant)) }), R.id.frmContainer)
//                                EventBus.getDefault().postSticky(list)
                                }
                            }
                        })
                    }
                    else{
                        if(!isDialogVisible){
                            isDialogVisible = true
                            context?.limitExceed(getString(R.string.app_name), context?.getTarakkiProMsg()?:"", positiveButton = {
                                startFragment(TarrakkiProBenefitsFragment.newInstance(), R.id.frmContainer)
                               // chatWhatsapp()
                                isDialogVisible = false
                            }, btnTitle = getString(R.string.subscribe_now),cancelButton = {
                                postSticky(Event.FIRST_TAB)
                                isDialogVisible = false
                            })

                        }
                    }
                }
            }
        }
        else{
            getViewModel().isTarrakkiPro.set(true)
        }
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        isFragmentLoaded = menuVisible
    }

    private fun chatWhatsapp() {
        if (App.INSTANCE.getWhatsAppURI()?.isNotEmpty() == true) {
            try {
                val packageManager = requireActivity().packageManager
                val isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp")
                if (isWhatsappInstalled) {
                    try {
                        val i = Intent(Intent.ACTION_VIEW,Uri.parse(App.INSTANCE.getWhatsAppURI()))
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

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = PrimeInvestorMutualFundListRatingFragment().apply { arguments = basket }
    }
}
