package com.tarrakki.module.learn


import android.os.Bundle
import android.support.v4.app.Fragment
import com.tarrakki.R
import com.tarrakki.databinding.FragmentLearnBinding
import org.supportcompact.CoreFragment


/**
 * A simple [Fragment] subclass.
 * Use the [LearnFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class LearnFragment : CoreFragment<LearnVM, FragmentLearnBinding>() {

    override val isBackEnabled: Boolean
        get() = false
    override val title: String
        get() = getString(R.string.learn)

    override fun getLayout(): Int {
        return R.layout.fragment_learn
    }

    override fun createViewModel(): Class<out LearnVM> {
        return LearnVM::class.java
    }

    override fun setVM(binding: FragmentLearnBinding) {
        binding.vm = getViewModel()
    }

    override fun createReference() {

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @param basket As Bundle.
         * @return A new instance of fragment LearnFragment.
         */
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = LearnFragment().apply { arguments = basket }
    }
}
