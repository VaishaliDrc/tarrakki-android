package com.tarrakki.module.risk_assesment

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.api.model.RiskAssessmentQuestionsApiResponse
import com.tarrakki.databinding.*
import com.tarrakki.module.risk_assessment_agree.AssessmentDeclartionFragment
import com.tarrakki.setDividerVertical
import kotlinx.android.synthetic.main.fragment_assessment_q.*
import org.greenrobot.eventbus.Subscribe
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.drawable
import org.supportcompact.ktx.simpleAlert
import org.supportcompact.ktx.startFragment
import org.supportcompact.ktx.toBigIntDefaultZero
import org.supportcompact.utilise.EqualSpacingItemDecoration
import org.supportcompact.utilise.GridSpacingItemDecoration
import java.math.BigInteger
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

    var totalQuestions: Int? = 0
    var currentPage: Int? = 0

    override fun getLayout() = R.layout.fragment_assessment_q

    override fun createViewModel(): Class<out AssessmentQVM> {
        return AssessmentQVM::class.java
    }

    override fun setVM(binding: FragmentAssessmentQBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        setHasOptionsMenu(true)
        getViewModel().questions.observe(this, Observer { it ->
            it?.let { data ->

                totalQuestions = data.data?.size
                currentPage = data.page
                val questionNo = "${data.page}"
                val total = "${"/"}${totalQuestions}  ${"Question"}"
                getViewModel().questionNo.set(questionNo)
                getViewModel().questionTotal.set(total)
                val question = data.data?.get(data.page - 1)
                val options = question?.option
                if (!options.isNullOrEmpty()) {
                    getViewModel().question.set(question.question)
                    setOptionsData(question, options as ArrayList<RiskAssessmentQuestionsApiResponse.Data.Option>)
                }

                btnPrevious?.setOnClickListener {
                    onBack()
                }

                btnNext?.setOnClickListener {
                    if (currentPage == totalQuestions) {
                        startFragment(AssessmentDeclartionFragment.newInstance(), R.id.frmContainer)
                        postSticky(data)
                    } else {
                        if (question != null && isValid(question)) {
                            startFragment(newInstance(), R.id.frmContainer)
                            data.page++
                            postSticky(data)
                        }
                    }
                }

                requireActivity().onBackPressedDispatcher.addCallback(this@AssessmentQFragment, object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        data.page--
                        onBack(1)
                    }
                })
            }
        })

    }

    fun isValid(question: RiskAssessmentQuestionsApiResponse.Data): Boolean {
        val options = question.option
        val optionsDataByCategory = options?.groupBy { it.optionCategory }
        val type = question.option?.firstOrNull()?.optionType?.toLowerCase(Locale.US)
        var isSelected = true
        when (type) {
            "slider" -> {
                if (optionsDataByCategory != null) {
                    for (item in optionsDataByCategory.entries) {
                        val category = item.key
                        val count = options.count { it.isSelected && it.optionCategory == category }
                        if (count == 0) {
                            if (TextUtils.isEmpty(category)) {
                                context?.simpleAlert("PLease select any one option to proceed next")
                            } else {
                                context?.simpleAlert("PLease select any one option from $category to proceed next")
                            }
                            isSelected = false
                            break
                        }
                    }
                }
            }
            "checkbox" -> {
                val count = options?.count { it.isSelected }
                if (count == 0) {
                    context?.simpleAlert("PLease select any one option to proceed next")
                    isSelected = false
                } else if (toBigIntDefaultZero(question.totalValue) == BigInteger.ZERO) {
                    context?.simpleAlert("PLease enter estimated total value to proceed next")
                    isSelected = false
                }
            }
            "radio", "radio_emoji", "radio_returns" -> {
                val count = options?.count { it.isSelected }
                if (count == 0) {
                    context?.simpleAlert("PLease select any one option to proceed next")
                    isSelected = false
                }
            }
            "checkbox_goal" -> {
                val count = options?.count { it.isSelected }
                if (count == 0) {
                    context?.simpleAlert("PLease select any one option to proceed next")
                    isSelected = false
                } else {
                    options?.filter { it.isSelected }?.forEach {
                        if (TextUtils.isEmpty(it.targetYear)) {
                            context?.simpleAlert("PLease select target year for ${it.optionValue} to proceed next")
                            isSelected = false
                            return isSelected
                        } else if (toBigIntDefaultZero(it.goalAmount) == BigInteger.ZERO) {
                            context?.simpleAlert("PLease enter amount for ${it.optionValue} to proceed next")
                            isSelected = false
                            return isSelected
                        }
                    }
                }
            }
        }
        return isSelected
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setOptionsData(question: RiskAssessmentQuestionsApiResponse.Data, options: ArrayList<RiskAssessmentQuestionsApiResponse.Data.Option>) {

        val optionsData = options.groupBy { it.optionCategory }
        val type = question.option?.firstOrNull()?.optionType?.toLowerCase(Locale.US)
        when (type) {
            "slider" -> {
                setSliderData(optionsData, options)
            }
            "checkbox" -> {
                if (optionsData.entries.size == 1) {
                    setCheckboxData(options)
                    clEstimatedValue?.visibility = View.VISIBLE
                    getBinding().item = question
                    getBinding().executePendingBindings()
                }
            }
            "radio" -> {
                if (optionsData.containsKey("Salary")) {
                    setRadioData(options.groupBy { it.optionValue }, options)
                } else {
                    setRadioData(optionsData, options)
                }
            }
            "radio_emoji" -> {
                setRadioEmojiData(optionsData, options)
            }
            "checkbox_goal" -> {
                setCheckboxGoalData(options)
            }
            "radio_returns" -> {
                setRadioReturnsData(optionsData, options)
            }
        }
    }

    private fun setCheckboxGoalData(options: java.util.ArrayList<RiskAssessmentQuestionsApiResponse.Data.Option>) {
        rvQuestions?.let {
            setDividerVertical(it, App.INSTANCE.drawable(R.drawable.shape_transferant_divider_16dp))
        }
        rvQuestions?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        rvQuestions?.setUpRecyclerView(R.layout.row_checkbox_goal_item, options) { item: RiskAssessmentQuestionsApiResponse.Data.Option, binder: RowCheckboxGoalItemBinding, position: Int ->
            binder.item = item
            binder.executePendingBindings()
            binder.root.setOnClickListener {
                item.isSelected = !item.isSelected
            }
        }
    }

    private fun setCheckboxData(options: ArrayList<RiskAssessmentQuestionsApiResponse.Data.Option>) {
        rvQuestions?.addItemDecoration(GridSpacingItemDecoration(3, resources.getDimensionPixelSize(R.dimen.space_12), true))
        rvQuestions?.layoutManager = GridLayoutManager(activity, 3)
        rvQuestions?.setUpRecyclerView(R.layout.row_risk_assessment_checkbox_item, options) { item: RiskAssessmentQuestionsApiResponse.Data.Option, binder: RowRiskAssessmentCheckboxItemBinding, position: Int ->
            binder.item = item
            binder.executePendingBindings()
            binder.root.setOnClickListener {
                item.isSelected = !item.isSelected
            }
        }
    }

    private fun setRadioEmojiData(data: Map<String?, List<RiskAssessmentQuestionsApiResponse.Data.Option>>, options: ArrayList<RiskAssessmentQuestionsApiResponse.Data.Option>) {
        val optionsItems = ArrayList<OptionsItem>()
        for (item in data.entries) {
            val category = item.key
            val itemOptions = item.value as ArrayList<RiskAssessmentQuestionsApiResponse.Data.Option>
            optionsItems.add(OptionsItem(category, itemOptions))
        }

        rvQuestions?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        if (data.entries.size == 1) {
            rvQuestions?.addItemDecoration(EqualSpacingItemDecoration(resources.getDimensionPixelSize(R.dimen.space_16)))
            rvQuestions?.setUpRecyclerView(R.layout.row_risk_assessment_radio_emoji_item, options) { item: RiskAssessmentQuestionsApiResponse.Data.Option, binder: RowRiskAssessmentRadioEmojiItemBinding, position: Int ->
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
    }

    private fun setRadioReturnsData(data: Map<String?, List<RiskAssessmentQuestionsApiResponse.Data.Option>>, options: ArrayList<RiskAssessmentQuestionsApiResponse.Data.Option>) {

        rvQuestions?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        rvQuestions?.addItemDecoration(EqualSpacingItemDecoration(resources.getDimensionPixelSize(R.dimen.space_16)))
        rvQuestions?.setUpRecyclerView(R.layout.row_risk_assessment_radio_return_item, options) { item: RiskAssessmentQuestionsApiResponse.Data.Option, binder: RowRiskAssessmentRadioReturnItemBinding, position: Int ->
            binder.item = item
            binder.executePendingBindings()

            val splitData = item.optionValue?.split(",")
            if (splitData?.isEmpty() == false) {
                val splitData1 = splitData[0]
                val splitData2 = splitData[1]
                val splitData3 = splitData[2]
                if (splitData1.isNotEmpty()) {
                    val splitDataOption1 = splitData1.split(":")
                    binder.option1Key = splitDataOption1[1].replace(" ", "")
                    binder.option1Value = splitDataOption1[0]
                }
                if (splitData2.isNotEmpty()) {
                    val splitDataOption2 = splitData2.split(":")
                    binder.option2Key = splitDataOption2[1].replace(" ", "")
                    binder.option2Value = splitDataOption2[0]
                }
                if (splitData3.isNotEmpty()) {
                    val splitDataOption3 = splitData3.split(":")
                    binder.option3Key = splitDataOption3[1].replace(" ", "")
                    binder.option3Value = splitDataOption3[0]
                }
            }

            binder.optionTitle = getCharForNumber(position)

            binder.root.setOnClickListener {
                options.forEachIndexed { index, it1 ->
                    it1.isSelected = false
                }
                item.isSelected = true
            }
        }
    }

    private fun setRadioData(data: Map<String?, List<RiskAssessmentQuestionsApiResponse.Data.Option>>, options: ArrayList<RiskAssessmentQuestionsApiResponse.Data.Option>) {
        val optionsItems = ArrayList<OptionsItem>()
        for (item in data.entries) {
            val category = item.key
            val itemOptions = item.value as ArrayList<RiskAssessmentQuestionsApiResponse.Data.Option>
            optionsItems.add(OptionsItem(category, itemOptions))
        }
        rvQuestions?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        if (data.entries.size == 1) {
            rvQuestions?.addItemDecoration(EqualSpacingItemDecoration(resources.getDimensionPixelSize(R.dimen.space_16)))
            rvQuestions?.setUpRecyclerView(R.layout.row_risk_assessment_radio_item, options) { item: RiskAssessmentQuestionsApiResponse.Data.Option, binder: RowRiskAssessmentRadioItemBinding, position: Int ->
                binder.item = item
                binder.executePendingBindings()
                binder.optionTitle = getCharForNumber(position)
                binder.root.setOnClickListener {
                    options.forEachIndexed { index, it1 ->
                        it1.isSelected = false
                    }
                    item.isSelected = true
                }
            }
        } else {
            optionsItems.sortByDescending { it.category }
            rvQuestions?.setUpRecyclerView(R.layout.row_risk_assessment_radio, optionsItems) { item: OptionsItem, binder: RowRiskAssessmentRadioBinding, position: Int ->
                binder.item = item
                binder.executePendingBindings()
                item.options?.let { options ->
                    binder.rvOptions.addItemDecoration(GridSpacingItemDecoration(3, resources.getDimensionPixelSize(R.dimen.space_12), true))
                    binder.rvOptions.setUpRecyclerView(R.layout.row_risk_assessment_checkbox_item, options) { item1: RiskAssessmentQuestionsApiResponse.Data.Option, binder1: RowRiskAssessmentCheckboxItemBinding, position1: Int ->
                        binder1.item = item1
                        binder1.executePendingBindings()
                        binder1.tvTitle.text = item1.optionCategory
                        binder1.root.setOnClickListener {
                            options.filter { it.optionId != item1.optionId }.forEach {
                                it.isSelected = false
                            }
                            item1.isSelected = !item1.isSelected
                        }
                    }
                }
            }
        }
    }

    private fun setSliderData(data: Map<String?, List<RiskAssessmentQuestionsApiResponse.Data.Option>>, options: ArrayList<RiskAssessmentQuestionsApiResponse.Data.Option>) {
        val optionsItems = ArrayList<OptionsItem>()
        for (item in data.entries) {
            val category = item.key
            val itemOptions = item.value as ArrayList<RiskAssessmentQuestionsApiResponse.Data.Option>
            optionsItems.add(OptionsItem(category, itemOptions))
        }

        if (getViewModel().questions.value?.isReassessment == true) {
            optionsItems.forEach end@{
                val selectedIndex = it.options?.indexOfFirst { it.isSelected }
                it.options?.forEachIndexed { index, option ->
                    if (index == selectedIndex) {
                        return@end
                    }
                    option.isMovedOver = true
                }
            }
        }

        val layoutManager = PeekingLinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        if (data.entries.size == 1) {

            val displaymetrics = DisplayMetrics()
            activity?.windowManager?.defaultDisplay?.getMetrics(displaymetrics)
            val width = displaymetrics.widthPixels * 55 / 100
            rvQuestions?.layoutParams?.width = width

            layoutManager.isSingleItem(true)

        } else {
            rvQuestions?.addItemDecoration(EqualSpacingItemDecoration(resources.getDimensionPixelSize(R.dimen.space_20)))
            layoutManager.isSingleItem(false)
        }
        rvQuestions?.layoutManager = layoutManager
        rvQuestions?.setUpRecyclerView(R.layout.row_risk_assessment_slider, optionsItems) { item: OptionsItem, binder: RowRiskAssessmentSliderBinding, position: Int ->
            binder.item = item
            binder.executePendingBindings()

            binder.isEnd = position % 2 != 0

            item.options?.let { options ->
                binder.rvOptions.setUpRecyclerView(R.layout.row_risk_assessment_slider_item_start, options) { item1: RiskAssessmentQuestionsApiResponse.Data.Option, binder1: RowRiskAssessmentSliderItemStartBinding, position1: Int ->
                    binder1.item = item1
                    binder1.executePendingBindings()

                    binder1.isEnd = position % 2 != 0

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
    }

    @Subscribe(sticky = true)
    fun onReceive(data: RiskAssessmentQuestionsApiResponse) {
        if (getViewModel().questions.value == null) {
            getViewModel().questions.value = data
        }
    }

    private fun getCharForNumber(i: Int): String? {
        return "ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(i, i + 1);
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
                when (orientation) {
                    HORIZONTAL -> width = (horizontalSpace * getRatio()).toInt()
                    VERTICAL -> height = (verticalSpace * getRatio()).toInt()
                }
            }

    private val horizontalSpace get() = width - paddingStart - paddingEnd

    private val verticalSpace get() = height - paddingTop - paddingBottom

    fun getRatio(): Float {
        return if (isSingleItem) {
            0.85f
        } else {
            0.40f
        }
    }

    var isSingleItem = false;

    fun isSingleItem(isSingleItem: Boolean) {
        this.isSingleItem = isSingleItem;
    }
}