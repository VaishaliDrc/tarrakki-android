package com.tarrakki.module.myprofile


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.text.TextUtils
import com.tarrakki.R
import com.tarrakki.databinding.FragmentNominationDetailsBinding
import kotlinx.android.synthetic.main.fragment_nomination_details.*
import org.supportcompact.CoreFragment
import org.supportcompact.ktx.simpleAlert

/**
 * A simple [Fragment] subclass.
 * Use the [NominationDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class NominationDetailsFragment : CoreFragment<MyProfileVM, FragmentNominationDetailsBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.my_profile)

    override fun getLayout(): Int {
        return R.layout.fragment_nomination_details
    }

    override fun createViewModel(): Class<out MyProfileVM> {
        return MyProfileVM::class.java
    }

    override fun setVM(binding: FragmentNominationDetailsBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        btnAddNominee?.setOnClickListener {
            when {
                TextUtils.isEmpty(getViewModel().nominiName.get()) ->
                    context?.simpleAlert("Please enter nominee name.") {
                        edtNominee?.requestFocus()
                        edtNominee?.length()?.let { it1 -> edtNominee?.setSelection(it1) }
                    }
                TextUtils.isEmpty(getViewModel().nominiRelationship.get()) ->
                    context?.simpleAlert("Please enter relationship.") {
                        edtRelationship?.requestFocus()
                        edtRelationship?.length()?.let { it1 -> edtRelationship?.setSelection(it1) }
                    }
                else -> context?.simpleAlert("Nominee has been added successfully")
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle.
         * @return A new instance of fragment NominationDetailsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = NominationDetailsFragment().apply { arguments = basket }
    }
}
