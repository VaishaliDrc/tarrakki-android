package com.tarrakki.module.goal


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuInflater
import android.widget.TextView
import com.tarrakki.App
import com.tarrakki.R
import com.tarrakki.databinding.FragmentGoalBinding
import com.tarrakki.databinding.RowGoalListItemBinding
import com.tarrakki.module.yourgoal.KEY_GOAL
import com.tarrakki.module.yourgoal.YourGoalFragment
import kotlinx.android.synthetic.main.fragment_goal.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setUpRecyclerView
import org.supportcompact.ktx.startFragment
import org.supportcompact.widgets.ItemOffsetDecoration


/**
 * A simple [Fragment] subclass.
 * Use the [GoalFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */

const val canBack = "canBack"

class GoalFragment : CoreFragment<GoalVM, FragmentGoalBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.set_a_goal)

    override fun getLayout(): Int {
        return R.layout.fragment_goal
    }

    override fun createViewModel(): Class<out GoalVM> {
        return GoalVM::class.java
    }

    override fun setVM(binding: FragmentGoalBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        rvGoals.isFocusable = false
        rvGoals.isNestedScrollingEnabled = false
        rvGoals.addItemDecoration(ItemOffsetDecoration(rvGoals.context, R.dimen.space_4))
        rvGoals.setUpRecyclerView(R.layout.row_goal_list_item, getViewModel().goals) { item: Goal, binder: RowGoalListItemBinding, position ->
            binder.goal = item
            binder.executePendingBindings()
            binder.root.setOnClickListener {
                startFragment(YourGoalFragment.newInstance(Bundle().apply { putSerializable(KEY_GOAL, item) }), R.id.frmContainer)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        arguments?.let {
            coreActivityVM?.isBackEnabled?.value = it.getBoolean(canBack, true)
        }
        coreActivityVM?.isBackEnabled?.value?.let {
            setHasOptionsMenu(!it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.home_menu, menu)
        val tvCartCount = menu?.findItem(R.id.itemHome)?.actionView?.findViewById<TextView>(R.id.tvCartCount)
        App.INSTANCE.cartCount.observe(this, Observer {
            tvCartCount?.text = it.toString()
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket Parameter 1.
         * @return A new instance of fragment GoalFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = GoalFragment().apply { arguments = basket }
    }
}
