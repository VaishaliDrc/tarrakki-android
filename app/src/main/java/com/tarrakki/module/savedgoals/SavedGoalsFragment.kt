package com.tarrakki.module.savedgoals


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.tarrakki.App
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
import org.supportcompact.ktx.*
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
        getViewModel().saveGoalResponse.observe(this, Observer {
            setAdapter(it)
        })

        mRefresh?.setOnRefreshListener {
            getViewModel().getSavedGoals(context?.getUserId(), true)
        }

        App.INSTANCE.isRefreshing.observe(this, Observer {
            it?.let { isRefreshing ->
                mRefresh?.isRefreshing = false
                App.INSTANCE.isRefreshing.value = null
            }
        })
    }

    override fun onResume() {
        getViewModel().getSavedGoals(context?.getUserId(), false)
        super.onResume()
    }

    private fun setAdapter(list: List<GoalSavedResponse.Data>?) {
        if (list != null) {

            adapter = rvSavedGoals?.setUpRecyclerView(R.layout.row_saved_goal_list_item,
                    list as ArrayList<GoalSavedResponse.Data>
            ) { item: GoalSavedResponse.Data, binder: RowSavedGoalListItemBinding, position: Int ->
                binder.goal = item
                binder.executePendingBindings()

                binder.ivDelete.setOnClickListener {
                    context?.confirmationDialog(getString(R.string.saved_goal_delete), btnPositiveClick = {
                        getViewModel().deleteSavedGoals(item.userGoalId).observe(this, Observer { apiResponse ->
                            getViewModel().getSavedGoals(context?.getUserId(), false)
                        })
                    })
                }

                binder.ivEdit.setOnClickListener {
                    startFragment(YourGoalSummaryFragment.newInstance(), R.id.frmContainer)
                    postSticky(item)
                }

                binder.tvAddGoal.setOnClickListener {
                    getViewModel().addGoalToCart(item.userGoalId.toString()).observe(this, Observer { apiResponce ->
                        context?.simpleAlert(getString(R.string.cart_goal_added)) {
                            startFragment(CartFragment.newInstance(), R.id.frmContainer)
                        }
                    })
                }
            }
            rvSavedGoals?.adapter = adapter
            updateUI()
        } else {
            rvSavedGoals.visibility = View.GONE
            context?.string(R.string.no_goals_found)?.let { coreActivityVM?.emptyView(true, it) }
        }
    }

    fun updateUI() {
        if (adapter?.itemCount == 0) {
            rvSavedGoals.visibility = View.GONE
            context?.string(R.string.no_goals_found)?.let { coreActivityVM?.emptyView(true, it) }
        } else {
            coreActivityVM?.emptyView(false)
            rvSavedGoals.visibility = View.VISIBLE
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = SavedGoalsFragment().apply { arguments = basket }
    }
}
