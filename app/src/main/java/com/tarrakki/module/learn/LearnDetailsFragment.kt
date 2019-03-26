package com.tarrakki.module.learn


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.ShareCompat
import com.tarrakki.R
import com.tarrakki.api.model.Blog
import com.tarrakki.databinding.FragmentLearnDetailsBinding
import kotlinx.android.synthetic.main.fragment_learn_details.*
import org.greenrobot.eventbus.Subscribe
import org.supportcompact.CoreFragment
import org.supportcompact.ktx.toHTMl

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

    }

    override fun createReference() {
        getViewModel().blog.observe(this, Observer {
            it?.let { blog ->
                tvShare?.setOnClickListener {
                    val mimeType = "text/html"
                    ShareCompat.IntentBuilder.from(activity)
                            .setChooserTitle(R.string.send_to)
                            .setType(mimeType)
                            .setText(
                                    getBinding().article?.title
                                            .plus("\n\n")
                                            .plus(getBinding().article?.description?.toHTMl())
                            ).startChooser()
                }
                getBinding().article = blog
                getBinding().executePendingBindings()
            }
        })
    }

    @Subscribe(sticky = true)
    fun onReceive(blog: Blog) {
        getViewModel().blog.value = blog
        removeStickyEvent(blog)
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
