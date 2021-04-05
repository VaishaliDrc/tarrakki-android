package com.tarrakki.module.changepassword


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.tarrakki.R
import com.tarrakki.databinding.FragmentChangePasswordBinding
import com.tarrakki.module.login.LoginActivity
import com.tarrakki.module.login.NewLoginActivity
import kotlinx.android.synthetic.main.fragment_change_password.*
import org.supportcompact.CoreFragment
import org.supportcompact.ktx.isValidPassword
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
        isResetPassword = arguments?.getBoolean("isResetPassword") ?: false
        if (isResetPassword) {
            getViewModel().token.set(arguments?.getString("token") ?: "")
            edtCPassword?.visibility = View.GONE
            btnSave?.text = getString(R.string.submit)
            coreActivityVM?.footerVisibility?.set(View.GONE)
        }

        btnSave?.setOnClickListener {
            if (isResetPassword) {
                if (resetPassValidation()) {
                    getViewModel().resetPassword().observe(this, Observer { apiResponse ->
                        context?.simpleAlert(getString(R.string.alert_profile_reset)) {
                            val intent = Intent(activity, NewLoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                    })
                }
            } else {
                if (changePassValidation()) {
                    getViewModel().changePassword().observe(this, Observer { apiResponse ->
                        context?.simpleAlert(getString(R.string.alert_profile_change)) {
                            activity?.onBackPressed()
                        }
                    })
                }
            }
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(basket: Bundle? = null) = ChangePasswordFragment().apply { arguments = basket }
    }

    private fun resetPassValidation(): Boolean {
        when {
            getViewModel().newPassword.get()?.length == 0 -> {
                context?.simpleAlert(getString(R.string.req_reset_password)) {
                    edtNPassword?.requestFocus()
                }
                return false
            }
            !getViewModel().newPassword.isValidPassword() -> {
                context?.simpleAlert(getString(R.string.valid_password)) {
                    edtNPassword?.requestFocus()
                    edtNPassword?.setSelection(edtNPassword.text.length)
                }
                return false
            }
            getViewModel().confirmPassword.get()?.length == 0 -> {
                context?.simpleAlert(getString(R.string.req_reset_password)) {
                    edtCNPassword?.requestFocus()
                }
                return false
            }
            !getViewModel().newPassword.get().equals(getViewModel().confirmPassword.get()) -> {
                context?.simpleAlert(getString(R.string.req_match_confirm_password)) {
                    edtCNPassword?.requestFocus()
                }
                return false
            }
            else -> {
                return true
            }
        }
    }

    private fun changePassValidation(): Boolean {
        when {
            getViewModel().currentPassword.get()?.length == 0 -> {
                context?.simpleAlert(getString(R.string.req_change_password)) {
                    edtNPassword?.requestFocus()
                }
                return false
            }
            getViewModel().newPassword.get()?.length == 0 -> {
                context?.simpleAlert(getString(R.string.req_change_password)) {
                    edtNPassword?.requestFocus()
                }
                return false
            }
            !getViewModel().newPassword.isValidPassword() -> {
                context?.simpleAlert(getString(R.string.valid_password)) {
                    edtNPassword?.requestFocus()
                    edtNPassword?.setSelection(edtNPassword.text.length)
                }
                return false
            }
            getViewModel().confirmPassword.get()?.length == 0 -> {
                context?.simpleAlert(getString(R.string.req_change_password)) {
                    edtCNPassword?.requestFocus()
                }
                return false
            }
            !getViewModel().newPassword.get().equals(getViewModel().confirmPassword.get()) -> {
                context?.simpleAlert(getString(R.string.req_match_confirm_password)) {
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
