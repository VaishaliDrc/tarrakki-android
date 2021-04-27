package com.tarrakki.module.portfolio

import android.os.Bundle
import com.tarrakki.R
import com.tarrakki.databinding.FragmentImportPortfolioBinding
import com.tarrakki.module.webview.WebViewFragment
import kotlinx.android.synthetic.main.fragment_import_portfolio.*
import org.supportcompact.CoreFragment
import org.supportcompact.events.Event
import org.supportcompact.ktx.startFragment


class ImportPortfolioFragment : CoreFragment<PortfolioDetailsVM, FragmentImportPortfolioBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.import_portfolio)

    override fun getLayout(): Int {
        return R.layout.fragment_import_portfolio
    }

    override fun createViewModel(): Class<out PortfolioDetailsVM> {
        return PortfolioDetailsVM::class.java
    }

    override fun setVM(binding: FragmentImportPortfolioBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {

        btnCamsWebsite.setOnClickListener {
            startFragment(WebViewFragment.newInstance(), R.id.frmContainer)
            postSticky(Event.CAMS_WEBSITE)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = ImportPortfolioFragment().apply { arguments = basket }
    }

}