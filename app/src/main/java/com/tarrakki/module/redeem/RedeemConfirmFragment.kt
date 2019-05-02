package com.tarrakki.module.redeem


import android.arch.lifecycle.Observer
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import com.tarrakki.R
import com.tarrakki.api.model.UserPortfolioResponse
import com.tarrakki.databinding.FragmentRedeemConfirmBinding
import com.tarrakki.instaRedeemPortfolio
import com.tarrakki.module.webview.WebViewFragment
import com.tarrakki.redeemPortfolio
import kotlinx.android.synthetic.main.fragment_redeem_confirm.*
import org.greenrobot.eventbus.Subscribe
import org.supportcompact.CoreFragment
import org.supportcompact.events.Event
import org.supportcompact.ktx.color
import org.supportcompact.ktx.startFragment


class RedeemConfirmFragment : CoreFragment<RedeemConfirmVM, FragmentRedeemConfirmBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.redemption_confim)

    override fun getLayout(): Int {
        return R.layout.fragment_redeem_confirm
    }

    override fun createViewModel(): Class<out RedeemConfirmVM> {
        return RedeemConfirmVM::class.java
    }

    override fun setVM(binding: FragmentRedeemConfirmBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        getViewModel().directRedeemFund.observe(this, Observer {
            it?.let { fund ->
                tvName?.text = fund.fundName
                tvTitle?.setText(if (fund.isInstaRedeem) R.string.redeemed_amount else R.string.number_of_unites_redeemed)
                tvUnits?.text = fund.redeemUnits
                tvExit?.text = fund.exitLoad
                getBinding().bank = fund.bank
                getBinding().executePendingBindings()
            }
        })
        getViewModel().goalBasedRedeemFund.observe(this, Observer {
            it?.let { fund ->
                tvName?.text = fund.fundName
                tvTitle?.setText(if (fund.isInstaRedeem) R.string.redeemed_amount else R.string.number_of_unites_redeemed)
                tvUnits?.text = fund.redeemUnits
                tvExit?.text = fund.exitLoad
                getBinding().bank = fund.bank
                getBinding().executePendingBindings()
            }
        })
        getViewModel().tarrakkiZyaadaRedeemFund.observe(this, Observer {
            it?.let { fund ->
                tvName?.text = fund.fundName
                tvTitle?.setText(if (fund.isInstaRedeem) R.string.redeemed_amount else R.string.number_of_unites_redeemed)
                tvUnits?.text = fund.redeemUnits
                tvExit?.text = fund.exitLoad
                getBinding().bank = fund.bank
                getBinding().executePendingBindings()
            }
        })
        btnProceed?.setOnClickListener {
            getViewModel().directRedeemFund.value?.let { fund ->
                fund.redeemRequest?.let { json ->
                    if (fund.isInstaRedeem) {
                        instaRedeemPortfolio(json).observe(this, Observer {
                            fund.redeemedStatus = it
                            startFragment(RedemptionStatusFragment.newInstance(), R.id.frmContainer)
                            postSticky(fund)
                        })
                    } else {
                        redeemPortfolio(json).observe(this, Observer {
                            fund.redeemedStatus = it
                            startFragment(RedemptionStatusFragment.newInstance(), R.id.frmContainer)
                            postSticky(fund)
                        })
                    }
                }
            }
            getViewModel().goalBasedRedeemFund.value?.let { fund ->
                fund.redeemRequest?.let { json ->
                    if (fund.isInstaRedeem) {
                        instaRedeemPortfolio(json).observe(this, Observer {
                            fund.redeemedStatus = it
                            startFragment(RedemptionStatusFragment.newInstance(), R.id.frmContainer)
                            postSticky(fund)
                        })
                    } else {
                        redeemPortfolio(json).observe(this, Observer {
                            fund.redeemedStatus = it
                            startFragment(RedemptionStatusFragment.newInstance(), R.id.frmContainer)
                            postSticky(fund)
                        })
                    }
                }
            }

            getViewModel().tarrakkiZyaadaRedeemFund.value?.let { fund ->
                fund.redeemRequest?.let { json ->
                    if (fund.isInstaRedeem) {
                        instaRedeemPortfolio(json).observe(this, Observer {
                            fund.redeemedStatus = it
                            startFragment(RedemptionStatusFragment.newInstance(), R.id.frmContainer)
                            postSticky(fund)
                        })
                    } else {
                        redeemPortfolio(json).observe(this, Observer {
                            fund.redeemedStatus = it
                            startFragment(RedemptionStatusFragment.newInstance(), R.id.frmContainer)
                            postSticky(fund)
                        })
                    }
                }
            }

        }

        val privacyPolicyClickSpan = object : ClickableSpan() {

            override fun onClick(widget: View) {
                startFragment(WebViewFragment.newInstance(), R.id.frmContainer)
                postSticky(Event.PRIVACY_PAGE)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                context?.color(R.color.colorAccent)?.let { ds.color = it }
            }
        }

        val termsAndCondditionClickSpan = object : ClickableSpan() {

            override fun onClick(widget: View) {
                startFragment(WebViewFragment.newInstance(), R.id.frmContainer)
                postSticky(Event.TERMS_AND_CONDITIONS_PAGE)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                context?.color(R.color.colorAccent)?.let { ds.color = it }
            }
        }

        makeLinks(
                tvDisclaimer,
                arrayOf("Disclaimers, Privacy Policy", "Terms and Conditions"),
                arrayOf(privacyPolicyClickSpan, termsAndCondditionClickSpan)
        )

    }

    private fun makeLinks(textView: TextView, links: Array<String>, clickableSpans: Array<ClickableSpan>) {
        val spannableString = SpannableString(textView.text)
        for (i in links.indices) {
            val clickableSpan = clickableSpans[i]
            val link = links[i]
            val startIndexOfLink = textView.text.toString().indexOf(link)
            spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + link.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        textView.highlightColor = Color.TRANSPARENT // prevent TextView change background when highlight
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.setText(spannableString, TextView.BufferType.SPANNABLE)
    }

    @Subscribe(sticky = true)
    fun onReemFund(item: UserPortfolioResponse.Data.DirectInvestment) {
        if (getViewModel().directRedeemFund.value == null) {
            getViewModel().directRedeemFund.value = item
        }
    }

    @Subscribe(sticky = true)
    fun onReemFund(item: UserPortfolioResponse.Data.GoalBasedInvestment.Fund) {
        if (getViewModel().goalBasedRedeemFund.value == null) {
            getViewModel().goalBasedRedeemFund.value = item
        }
    }

    @Subscribe(sticky = true)
    fun onReemFund(item: UserPortfolioResponse.Data.TarrakkiZyaadaInvestment) {
        if (getViewModel().tarrakkiZyaadaRedeemFund.value == null) {
            getViewModel().tarrakkiZyaadaRedeemFund.value = item
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle.
         * @return A new instance of fragment RedeemConfirmFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = RedeemConfirmFragment().apply { arguments = basket }
    }
}
