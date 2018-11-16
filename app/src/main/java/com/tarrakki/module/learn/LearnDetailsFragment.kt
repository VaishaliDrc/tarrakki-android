package com.tarrakki.module.learn


import android.os.Bundle
import android.support.v4.app.Fragment
import com.tarrakki.R
import com.tarrakki.databinding.FragmentLearnDetailsBinding
import org.supportcompact.CoreFragment

/**
 * A simple [Fragment] subclass.
 * Use the [LearnDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class LearnDetailsFragment : CoreFragment<LearnVM, FragmentLearnDetailsBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.learn)

    override fun getLayout(): Int {
        return R.layout.fragment_learn_details
    }

    override fun createViewModel(): Class<out LearnVM> {
        return LearnVM::class.java
    }

    override fun setVM(binding: FragmentLearnDetailsBinding) {
        arguments?.let {
            binding.article = it.getSerializable(ARTICLE) as Article?
            binding.executePendingBindings()
        }
    }

    override fun createReference() {

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment LearnDetailsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = LearnDetailsFragment().apply { arguments = basket }
    }
}
