package com.tarrakki.module.changepassword


import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import com.tarrakki.R
import com.tarrakki.databinding.FragmentChangePasswordBinding
import kotlinx.android.synthetic.main.fragment_change_password.*
import org.supportcompact.CoreFragment
import org.supportcompact.ktx.simpleAlert

/**
 * A simple [Fragment] subclass.
 * Use the [ChangePasswordFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ChangePasswordFragment : CoreFragment<ChangePasswordVM, FragmentChangePasswordBinding>() {

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = getString(R.string.change_password)

    override fun getLayout(): Int {
        return R.layout.fragment_change_password
    }

    override fun createViewModel(): Class<out ChangePasswordVM> {
        return ChangePasswordVM::class.java
    }

    override fun setVM(binding: FragmentChangePasswordBinding) {
        binding.vm = getViewModel()
        binding.executePendingBindings()
    }

    override fun createReference() {
        btnSave?.setOnClickListener {
            when {
                TextUtils.isEmpty(getViewModel().currentPassword.get()) ->
                    context?.simpleAlert("Please enter current password") {
                        edtCPassword.requestFocus()
                    }
                TextUtils.isEmpty(getViewModel().newPassword.get()) ->
                    context?.simpleAlert("Please enter new password") {
                        edtNPassword.requestFocus()
                    }
                TextUtils.isEmpty(getViewModel().confirmPassword.get()) ->
                    context?.simpleAlert("Please enter confirm new password") {
                        edtCNPassword.requestFocus()
                    }
                getViewModel().newPassword.get() != getViewModel().confirmPassword.get() ->
                    context?.simpleAlert("New Password and confirm new password miss match")
                else -> context?.simpleAlert("Password has been changed successfully") {
                    activity?.onBackPressed()
                }
            }
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param basket As Bundle.
         * @return A new instance of fragment ChangePasswordFragment.
         */
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = ChangePasswordFragment().apply { arguments = basket }
    }
}
