package com.tarrakki.module.risk_assesment

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tarrakki.R
import com.tarrakki.api.model.RiskAssessmentQuestionsApiResponse
import com.tarrakki.databinding.*
import kotlinx.android.synthetic.main.fragment_assessment_q.*
import kotlinx.android.synthetic.main.fragment_investment_strategies.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.simpleAlert
import org.supportcompact.ktx.startFragment
import org.supportcompact.widgets.ItemOffsetDecoration
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 * Use the [AssessmentQFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AssessmentQFragment : CoreFragment<AssessmentQVM, FragmentAssessmentQBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.risk_assessment)

    var totalQuestions : Int? = 0
    var currentPage : Int? = 0

    override fun getLayout() = R.layout.fragment_assessment_q

    override fun createViewModel(): Class<out AssessmentQVM> {
        return AssessmentQVM::class.java
    }

    override fun setVM(binding: FragmentAssessmentQBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        getViewModel().questions.observe(this, Observer { it ->
            it?.let { data ->

                totalQuestions = data.data?.size?.minus(1)
                currentPage = data.page

                val questionNo = "${data.page}${"/"}${totalQuestions}  ${"Question"}"
                getViewModel().questionNo.set(questionNo)

                val question = data.data?.get(data.page-1)
                val options = question?.option

                if (!options.isNullOrEmpty()){
                    getViewModel().question.set(question.question)
                    setOptionsData(options[0].optionType.toString(),options as ArrayList<RiskAssessmentQuestionsApiResponse.Data.Option>)
                }

                btnContinue?.setOnClickListener {
                    if (currentPage==totalQuestions){
                        context?.simpleAlert("Completed")
                    }else {
                        startFragment(AssessmentQFragment.newInstance(), R.id.frmContainer)
                        data.page++
                        postSticky(data)
                    }
                }

            }
        })

    }

    fun setOptionsData(type : String, options: ArrayList<RiskAssessmentQuestionsApiResponse.Data.Option>){
        val optionsItems = ArrayList<OptionsItem>()
        val optionsData =  options.groupBy { it.optionCategory }

        for (item in optionsData.entries){
            val category = item.key
            val itemOptions = item.value as ArrayList<RiskAssessmentQuestionsApiResponse.Data.Option>
            optionsItems.add(OptionsItem(category,itemOptions))
        }

        when (type) {
            "slider" -> {
                setSliderData(optionsItems)
            }
            "checkbox" -> {
                if (optionsData.entries.size==1) {
                    setCheckboxData(options)
                }
            }
            "radio" -> {
                if (optionsData.entries.size==1){
                    setRadioData(options)
                }else{

                }
            }
            "radio_emoji" -> {

            }
            "checkbox_goal" -> {

            }
            "radio_returns" -> {

            }
        }


    }

    fun setCheckboxData(options :  ArrayList<RiskAssessmentQuestionsApiResponse.Data.Option>){
        rvQuestions?.addItemDecoration(ItemOffsetDecoration(context!!, R.dimen.space_4))
        rvQuestions?.layoutManager = GridLayoutManager(activity,3)
        rvQuestions?.setUpRecyclerView(R.layout.row_risk_assessment_checkbox_item ,options) { item: RiskAssessmentQuestionsApiResponse.Data.Option, binder: RowRiskAssessmentCheckboxItemBinding, position: Int ->
            binder.item = item
            binder.executePendingBindings()

            binder.root.setOnClickListener {
                item.isSelected = !item.isSelected
            }
        }

    }

    fun setRadioData(options :  ArrayList<RiskAssessmentQuestionsApiResponse.Data.Option>){

        rvQuestions?.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
        rvQuestions?.setUpRecyclerView(R.layout.row_risk_assessment_radio_item ,options) { item: RiskAssessmentQuestionsApiResponse.Data.Option, binder: RowRiskAssessmentRadioItemBinding, position: Int ->
            binder.item = item
            binder.executePendingBindings()

            binder.root.setOnClickListener {
                options.forEachIndexed { index, it1 ->
                    it1.isSelected = false
                }
                item.isSelected = true
            }
        }

    }

    fun setSliderData(options : ArrayList<OptionsItem>){

        rvQuestions?.layoutManager = PeekingLinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)
        rvQuestions?.setUpRecyclerView(R.layout.row_risk_assessment_slider, options) { item: OptionsItem, binder: RowRiskAssessmentSliderBinding, position: Int ->
           binder.item = item
           binder.executePendingBindings()

            item.options?.let {
                options ->
                binder.rvOptions.setUpRecyclerView(R.layout.row_risk_assessment_slider_item_start, options) { item1: RiskAssessmentQuestionsApiResponse.Data.Option, binder1: RowRiskAssessmentSliderItemStartBinding, position1: Int ->
                    binder1.item = item1
                    binder1.executePendingBindings()
                    binder1.tvTitle.setOnClickListener {

                        options.forEachIndexed { index, it1 ->
                            it1.isSelected = false
                            it1.isMovedOver = index < position1
                        }
                        item1.isSelected = true
                    }
                }
            }
       }

        /*rvQuestions?.setUpRecyclerView(R.layout.row_risk_assessment_slider_item_start, getViewModel().sliderQuestions) { item: RiskAssessmentQuestionsApiResponse.Data.Option, binder: RowRiskAssessmentSliderItemStartBinding, position: Int ->
            binder.item = item
            binder.executePendingBindings()
            binder.tvTitle.setOnClickListener {

                getViewModel().sliderQuestions.forEachIndexed { index, it ->
                    it.isSelected = false
                    it.isMovedOver = index < position
                }
                getViewModel().sliderQuestions[position].isSelected = true
            }
        }
*/

    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onReceive(data: RiskAssessmentQuestionsApiResponse) {
        if (getViewModel().questions.value == null) {
            getViewModel().questions.value = data
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = AssessmentQFragment().apply { arguments = basket }
    }
}


class PeekingLinearLayoutManager : LinearLayoutManager {
    @JvmOverloads
    constructor(context: Context?, @RecyclerView.Orientation orientation: Int = RecyclerView.VERTICAL, reverseLayout: Boolean = false) : super(context, orientation, reverseLayout)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun generateDefaultLayoutParams() =
            scaledLayoutParams(super.generateDefaultLayoutParams())

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams?) =
            scaledLayoutParams(super.generateLayoutParams(lp))

    override fun generateLayoutParams(c: Context?, attrs: AttributeSet?) =
            scaledLayoutParams(super.generateLayoutParams(c, attrs))

    private fun scaledLayoutParams(layoutParams: RecyclerView.LayoutParams) =
            layoutParams.apply {
                when(orientation) {
                    HORIZONTAL -> width = (horizontalSpace * ratio).toInt()
                    VERTICAL -> height = (verticalSpace * ratio).toInt()
                }
            }

    private val horizontalSpace get() = width - paddingStart - paddingEnd

    private val verticalSpace get() = height - paddingTop - paddingBottom

    private val ratio = 0.5f // change to 0.7f for 70%
}