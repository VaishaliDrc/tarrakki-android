package com.tarrakki.module.learn


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.View
import com.tarrakki.App
import com.tarrakki.BR
import com.tarrakki.R
import com.tarrakki.api.model.BlogResponse
import com.tarrakki.databinding.FragmentLearnBinding
import com.tarrakki.module.transactions.LoadMore
import kotlinx.android.synthetic.main.fragment_learn.*
import org.supportcompact.CoreFragment
import org.supportcompact.adapters.WidgetsViewModel
import org.supportcompact.adapters.setUpMultiViewRecyclerAdapter
import org.supportcompact.ktx.startFragment
import org.supportcompact.ktx.string

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
        val loadMoreObservable = MutableLiveData<Int>()
        val blogs = ArrayList<WidgetsViewModel>()
        val blogObservable = Observer<BlogResponse> {
            it?.data?.let { blogResponse ->
                blogResponse.blogs?.let { blogsData ->
                    blogs.clear()
                    blogs.addAll(blogsData)
                    if (blogs.size >= 5 && blogResponse.total > blogs.size) {
                        blogs.add(getViewModel().loadMore)
                    }
                    if (rvArticles?.adapter == null) {
                        rvArticles?.setUpMultiViewRecyclerAdapter(blogs) { item: WidgetsViewModel, binder: ViewDataBinding, position: Int ->
                            binder.setVariable(BR.article, item)
                            binder.setVariable(BR.onReadMore, View.OnClickListener {
                                startFragment(LearnDetailsFragment.newInstance(), R.id.frmContainer)
                                postSticky(item)
                            })
                            binder.executePendingBindings()
                            if (item is LoadMore && !item.isLoading && mRefresh?.isRefreshing == false) {
                               // item.isLoading = true
                                loadMoreObservable.value = blogResponse.offset
                            }
                        }
                    } else {
                        rvArticles?.adapter?.notifyDataSetChanged()
                    }
                }
            }
            context?.string(R.string.no_data_found)?.let { coreActivityVM?.emptyView(blogs.isEmpty(), it) }
            mRefresh?.isRefreshing = false
        }
        getViewModel().getBlogs().observe(this, blogObservable)
        loadMoreObservable.observe(this, Observer {
            it?.let { offset ->
                Handler().postDelayed({
                    getViewModel().getBlogs(offset = offset).observe(this, blogObservable)
                }, 1500)
            }
        })
        App.INSTANCE.isRefreshing.observe(this, Observer {
            it?.let { isRefreshing ->
                context?.string(R.string.no_data_found)?.let { coreActivityVM?.emptyView(blogs.isEmpty(), it) }
                mRefresh?.isRefreshing = false
                App.INSTANCE.isRefreshing.value = null
            }
        })
        mRefresh?.setOnRefreshListener {
            getViewModel().getBlogs(isRefresh = true).observe(this, blogObservable)
        }
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
