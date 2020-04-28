package com.tarrakki.module.risk_profile

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.Fragment
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.databinding.FragmentRiskProfileBinding
import com.tarrakki.databinding.RowSpeedometerRiskProfileBinding
import com.tarrakki.speedometer.SpeedView
import com.tarrakki.speedometer.components.Section
import com.tarrakki.speedometer.components.indicators.ImageIndicator
import kotlinx.android.synthetic.main.fragment_risk_profile.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter
import org.supportcompact.ktx.color
import org.supportcompact.ktx.convertToPx
import org.supportcompact.ktx.startFragment

/**
 * A simple [Fragment] subclass.
 * Use the [RiskProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RiskProfileFragment : CoreFragment<RiskProfileVM, FragmentRiskProfileBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.your_risk_profile)

    override fun getLayout(): Int {
        return R.layout.fragment_risk_profile
    }

    override fun createViewModel(): Class<out RiskProfileVM> {
        return RiskProfileVM::class.java
    }

    override fun setVM(binding: FragmentRiskProfileBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        rvRiskProfile?.setUpMultiViewRecyclerAdapter(getViewModel().data) { item: WidgetsViewModel, binder: ViewDataBinding, position: Int ->
            binder.setVariable(BR.widget, item)
            binder.setVariable(BR.onAdd, View.OnClickListener {
                startFragment(StartAssessmentFragment.newInstance(), R.id.frmContainer)
            })
            binder.executePendingBindings()
            if (binder is RowSpeedometerRiskProfileBinding) {
                setUpSpeedView(binder.speedView)
            }
        }
    }

    private fun setUpSpeedView(speedView: SpeedView) {
        speedView.layoutParams.height = ((App.INSTANCE.resources.displayMetrics.widthPixels - 48f.convertToPx()) / 2).toInt()
        speedView.requestLayout()
        speedView.markWidth = 30.toFloat()
        ContextCompat.getDrawable(App.INSTANCE, R.drawable.indicator)?.let {
            val imageIndicator = ImageIndicator(App.INSTANCE, it)
            speedView.indicator = imageIndicator
        }
        speedView.sections.clear()
        speedView.addSections(Section(0f, .2f, App.INSTANCE.color(R.color.conservative), speedView.dpTOpx(30f))
                , Section(.2f, .4f, App.INSTANCE.color(R.color.moderately_conservative), speedView.dpTOpx(30f))
                , Section(.4f, .6f, App.INSTANCE.color(R.color.balanced), speedView.dpTOpx(30f))
                , Section(.6f, .8f, App.INSTANCE.color(R.color.moderately_aggressive), speedView.dpTOpx(30f))
                , Section(.8f, 1f, App.INSTANCE.color(R.color.aggressive), speedView.dpTOpx(30f)))
        speedView.setSpeedAt(90f)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle.
         * @return A new instance of fragment RiskProfileFragment.
         */
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = RiskProfileFragment().apply { arguments = basket }
    }

}
