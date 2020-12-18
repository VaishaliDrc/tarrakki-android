package com.tarrakki.module.ekyc


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import com.tarrakki.R
import com.tarrakki.databinding.FragmentEkycconfirmationBinding
import com.tarrakki.fcm.ACTION_CLOSE_KYC_PORTAL
import com.tarrakki.fcm.IS_FROM_NOTIFICATION
import com.tarrakki.fcm.eventPanKYCNonVerified
import com.tarrakki.module.account.AccountActivity
import com.tarrakki.module.account.AccountFragment
import com.tarrakki.module.home.HomeActivity
import com.tarrakki.module.home.HomeFragment
import kotlinx.android.synthetic.main.fragment_ekycconfirmation.*
import org.greenrobot.eventbus.Subscribe
import org.supportcompact.CoreFragment
import org.supportcompact.ktx.confirmationDialog
import org.supportcompact.ktx.openPlayStore
import org.supportcompact.ktx.simpleAlert


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

        eventPanKYCNonVerified()

        context?.let {
            androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(it).registerReceiver(OnKYCSuccess, IntentFilter(ACTION_CLOSE_KYC_PORTAL))
        }
        setHasOptionsMenu(true)
        btnYes?.setOnClickListener {
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
                customTabsIntent.launchUrl(requireActivity(), Uri.parse(data.mobileAutoLoginUrl))
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

    override fun onDestroyView() {
        super.onDestroyView()
        context?.let {
            androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(it).unregisterReceiver(OnKYCSuccess)
        }
    }

    private val OnKYCSuccess = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val myIntent = Intent(context, if (activity is HomeActivity) HomeActivity::class.java else AccountActivity::class.java)
            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            myIntent.putExtra(IS_FROM_NOTIFICATION, true)
            myIntent.putExtra(ACTION_CLOSE_KYC_PORTAL, getViewModel().kycData)
            context?.startActivity(myIntent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
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
