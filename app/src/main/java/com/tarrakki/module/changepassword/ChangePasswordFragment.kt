package com.tarrakki.module.changepassword


import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.View
import com.tarrakki.R
import com.tarrakki.databinding.FragmentChangePasswordBinding
import com.tarrakki.module.login.LoginActivity
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

    var isResetPassword: Boolean = false

    override val isBackEnabled: Boolean
        get() = true
    override val title: String
        get() = if (isResetPassword) {
            getString(R.string.reset_password)
        } else {
            getString(R.string.change_password)
        }

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
        isResetPassword = arguments?.getBoolean("isResetPassword")!!
        if (isResetPassword) {
            getViewModel().token.set(arguments?.getString("token")!!)
            edtCPassword?.visibility = View.GONE
            btnSave?.text = getString(R.string.submit)
            coreActivityVM?.footerVisibility?.set(View.GONE)
        }

        btnSave?.setOnClickListener {
            if (isResetPassword) {
                if (validation()) {
                    getViewModel().resetPassword().observe(this, Observer { apiResponse ->

                        context?.simpleAlert(apiResponse?.status?.message.toString()) {
                            val intent = Intent(activity, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                    })
                }
            } else {

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

    private fun validation(): Boolean {
        when {
            getViewModel().newPassword.get()?.length == 0 -> {
                context?.simpleAlert("Please enter password") {
                    edtNPassword?.requestFocus()
                }
                return false
            }
            getViewModel().confirmPassword.get()?.length == 0 -> {
                context?.simpleAlert("Please enter confirm password") {
                    edtCNPassword?.requestFocus()
                }
                return false
            }
            !getViewModel().newPassword.get().equals(getViewModel().confirmPassword.get()) -> {
                context?.simpleAlert("New Password & Confirm Password doesn't match.") {
                    edtCNPassword?.requestFocus()
                }
                return false
            }
            else -> {
                return true
            }
        }
    }
}
