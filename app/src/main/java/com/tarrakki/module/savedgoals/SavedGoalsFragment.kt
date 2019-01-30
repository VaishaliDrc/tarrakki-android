package com.tarrakki.module.savedgoals


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.tarrakki.R
import com.tarrakki.api.model.GoalSavedResponse
import com.tarrakki.databinding.FragmentSavedGoalsBinding
import com.tarrakki.databinding.RowSavedGoalListItemBinding
import com.tarrakki.module.cart.CartFragment
import com.tarrakki.module.recommended.ISFROMGOALRECOMMEDED
import com.tarrakki.module.yourgoal.YourGoalSummaryFragment
import kotlinx.android.synthetic.main.fragment_saved_goals.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.BaseAdapter
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.confirmationDialog
import org.supportcompact.ktx.getUserId
import org.supportcompact.ktx.simpleAlert
import org.supportcompact.ktx.startFragment
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [SavedGoalsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SavedGoalsFragment : CoreFragment<SavedGoalsVM, FragmentSavedGoalsBinding>() {

    var adapter: BaseAdapter<GoalSavedResponse.Data, RowSavedGoalListItemBinding>? = null

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.saved_goals)

    override fun getLayout(): Int {
        return R.layout.fragment_saved_goals
    }

    override fun createViewModel(): Class<out SavedGoalsVM> {
        return SavedGoalsVM::class.java
    }

    override fun setVM(binding: FragmentSavedGoalsBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        getViewModel().getSavedGoals(context?.getUserId())

        getViewModel().saveGoalResponse.observe(this, Observer {
            setAdapter(it)
        })

        getViewModel().isEmpty.observe(this, Observer {
            if (it==true) {
                getBinding().root.visibility = View.GONE
                coreActivityVM?.emptyView(true)
            } else {
                coreActivityVM?.emptyView(false)
                getBinding().root.visibility = View.VISIBLE
            }
        })
    }

    private fun setAdapter(list: List<GoalSavedResponse.Data>?) {
        adapter = rvSavedGoals?.setUpRecyclerView(R.layout.row_saved_goal_list_item,
                list as ArrayList<GoalSavedResponse.Data>
        ) { item: GoalSavedResponse.Data, binder: RowSavedGoalListItemBinding, position: Int ->
            binder.goal = item
            binder.executePendingBindings()

            binder.ivDelete.setOnClickListener {
                context?.confirmationDialog(getString(R.string.saved_goal_delete), btnPositiveClick = {
                    getViewModel().deleteSavedGoals(item.userGoalId).observe(this, Observer { apiResponse ->
                        getViewModel().getSavedGoals(context?.getUserId())
                    })
                })
            }

            binder.ivEdit.setOnClickListener {
                startFragment(YourGoalSummaryFragment.newInstance(), R.id.frmContainer)
                postSticky(item)
            }

            binder.tvAddGoal.setOnClickListener {
                getViewModel().addGoalToCart(item.userGoalId.toString()).observe(this, Observer { apiResponce ->
                    context?.simpleAlert(getString(R.string.cart_goal_added)){
                        startFragment(CartFragment.newInstance(), R.id.frmContainer)
                    }
                })
            }
        }
        rvSavedGoals?.adapter = adapter
        getViewModel().isEmpty.value = false
        //updateUI()
    }

    fun updateUI() {
        if (adapter?.itemCount == 0) {
            getBinding().root.visibility = View.GONE
            coreActivityVM?.emptyView(true)
        } else {
            coreActivityVM?.emptyView(false)
            getBinding().root.visibility = View.VISIBLE
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = SavedGoalsFragment().apply { arguments = basket }
    }
}
