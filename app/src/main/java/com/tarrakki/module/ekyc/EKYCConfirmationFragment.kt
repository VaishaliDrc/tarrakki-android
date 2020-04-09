package com.tarrakki.module.ekyc


import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.tarrakki.App
import com.tarrakki.CustomTabsHelper
import com.tarrakki.IS_FROM_COMLETE_REGISTRATION
import com.tarrakki.R
import com.tarrakki.databinding.FragmentEkycconfirmationBinding
import com.tarrakki.module.account.AccountFragment
import com.tarrakki.module.bankaccount.BankAccountsFragment
import com.tarrakki.module.home.HomeActivity
import com.tarrakki.module.home.HomeFragment
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_ekycconfirmation.*
import org.greenrobot.eventbus.Subscribe
import org.supportcompact.CoreFragment
import org.supportcompact.events.Event
import org.supportcompact.ktx.*


/**
 * A simple [Fragment] subclass.
 * Use the [EKYCConfirmationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EKYCConfirmationFragment : CoreFragment<EKYCConfirmationVM, FragmentEkycconfirmationBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.video_kyc)

    override fun getLayout(): Int {
        return R.layout.fragment_ekycconfirmation
    }

    override fun createViewModel(): Class<out EKYCConfirmationVM> {
        return EKYCConfirmationVM::class.java
    }

    override fun setVM(binding: FragmentEkycconfirmationBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        setHasOptionsMenu(true)
        btnYes?.setOnClickListener {
            /*startFragment(EKYCWebViewFragment.newInstance(), R.id.frmContainer)
              getViewModel().kycData?.let { data ->
                postSticky(data)
              }
            */
            apiApplyForNewKYC().observe(this, Observer {
                it?.let {
                    getViewModel().kycData?.mobileAutoLoginUrl = it.data?.mobileAutoLoginUrl
                    openChromeTab()
                }
            })
        }

        btnNo?.setOnClickListener {
            onBack()
        }
    }

    private fun openChromeTab() {
        val intentBuilder = CustomTabsIntent.Builder()
        // Begin customizing
        // set toolbar colors
        intentBuilder.setToolbarColor(ContextCompat.getColor(App.INSTANCE, R.color.colorPrimary));
        intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(App.INSTANCE, R.color.colorPrimaryDark));
        intentBuilder.setShowTitle(true)
        // build custom tabs intent
        val customTabsIntent = intentBuilder.build()
        // launch the url
        getViewModel().kycData?.let { data ->
            try {
                // Here is a method that returns the chrome package name
                // Here is a method that returns the chrome package name
                val packageName = CustomTabsHelper.getPackageNameToUse(activity)
                if (packageName != null) {
                    customTabsIntent.intent.setPackage(packageName)
                }
                customTabsIntent.launchUrl(activity, Uri.parse(data.mobileAutoLoginUrl))
                getViewModel().kycProcessInit = true
            } catch (e: Exception) {
                context?.simpleAlert(getString(R.string.chrome_required_to_install)) {
                    context?.openPlayStore(CustomTabsHelper.STABLE_PACKAGE)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (getViewModel().kycProcessInit) {
            context?.confirmationDialog(getString(R.string.are_you_sure_you_want_to_exit),
                    btnPositiveClick = {
                        if (activity is HomeActivity) {
                            onBackExclusive(HomeFragment::class.java)
                        } else {
                            onBackExclusive(AccountFragment::class.java)
                        }
                    }
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onEvent(event: Event) {
        when (event) {
            Event.ON_KYC_SUCCESS -> {
                e("is redirect from Push.....")
                startFragment(BankAccountsFragment.newInstance(Bundle().apply {
                    putBoolean(IS_FROM_VIDEO_KYC, true)
                    putBoolean(IS_FROM_COMLETE_REGISTRATION, true)
                }), R.id.frmContainer)
                getViewModel().kycData?.let {
                    postSticky(it)
                }
            }
            else -> super.onEvent(event)
        }
    }

    @Subscribe(sticky = true)
    fun onReceive(kycData: KYCData) {
        if (getViewModel().kycData == null) {
            getViewModel().kycData = kycData
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle.
         * @return A new instance of fragment EKYCConfirmationFragment.
         */
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = EKYCConfirmationFragment().apply { arguments = basket }
    }

}
