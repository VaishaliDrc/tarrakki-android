package com.tarrakki.module.portfolio

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.tarrakki.R
import com.tarrakki.api.model.UserProfileResponse
import com.tarrakki.databinding.FragmentImportFundsBinding
import com.tarrakki.databinding.FragmentImportPortfolioBinding
import com.tarrakki.module.tarrakkipro.TarrakkiProBenefitsFragment
import com.tarrakki.module.webview.WebViewFragment
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_import_funds.*
import kotlinx.android.synthetic.main.fragment_import_portfolio.*
import kotlinx.android.synthetic.main.fragment_my_profile.*
import org.supportcompact.CoreFragment
import org.supportcompact.events.Event
import org.supportcompact.ktx.makeSpannableLinks
import org.supportcompact.ktx.startFragment


class ImportFundsFragment : CoreFragment<PortfolioDetailsVM, FragmentImportFundsBinding>() {


    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.import_funds)

    override fun getLayout(): Int {
        return R.layout.fragment_import_funds
    }

    override fun createViewModel(): Class<out PortfolioDetailsVM> {
        return PortfolioDetailsVM::class.java
    }

    override fun setVM(binding: FragmentImportFundsBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {

        tvStepCAS?.setOnClickListener {
            startFragment(ImportPortfolioFragment.newInstance(), R.id.frmContainer)
        }

        tvVisitCAMS.setOnClickListener {
            startFragment(WebViewFragment.newInstance(), R.id.frmContainer)
            postSticky(Event.CAMS_WEBSITE)
        }

        tvForwardEmail.makeSpannableLinks(resources.getColor(R.color.equity_fund_color), false,
                Pair("cas@tarrakki.com", View.OnClickListener {
                    val cm: ClipboardManager = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    cm.text = "cas@tarrakki.com"
                    Toast.makeText(context, "Email Copied", Toast.LENGTH_SHORT).show()
                }))


        val profileObserver: androidx.lifecycle.Observer<UserProfileResponse> = androidx.lifecycle.Observer { response ->
            response?.let {
                tvEmail.text = response.data.email
                response.data.kycDetail.pan?.let {
                    tvPan.text = it
                    tvPassword.text = it
                }
            }
        }


        getViewModel().getUserProfile().observe(this, profileObserver)
    }


    companion object {
        @JvmStatic
        fun newInstance() =
                ImportFundsFragment().apply {}
    }


}