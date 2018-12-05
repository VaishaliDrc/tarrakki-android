package com.tarrakki.module.savedgoals


import android.os.Bundle
import android.support.v4.app.Fragment
import com.tarrakki.R
import com.tarrakki.databinding.FragmentSavedGoalsBinding
import com.tarrakki.databinding.RowSavedGoalListItemBinding
import kotlinx.android.synthetic.main.fragment_saved_goals.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.setUpRecyclerView

/**
 * A simple [Fragment] subclass.
 * Use the [SavedGoalsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SavedGoalsFragment : CoreFragment<SavedGoalsVM, FragmentSavedGoalsBinding>() {

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
        rvSavedGoals?.setUpRecyclerView(R.layout.row_saved_goal_list_item, getViewModel().savedGoals) { item: SavedGoal, binder: RowSavedGoalListItemBinding, position: Int ->
            binder.goal = item
            binder.executePendingBindings()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket Bundle.
         * @return A new instance of fragment SavedGoalsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = SavedGoalsFragment().apply { arguments = basket }
    }
}
