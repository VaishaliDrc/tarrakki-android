package org.supportcompact.adapters

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import org.supportcompact.ktx.inflate


/**
 * Created by jayeshparkariya on 26/2/18.
 */


/***
 * This is extension to setup adapter by giving fallowing inputs
 * @param layoutRes Layout to be bound.
 * @param itemList Data to to be bound with layout.
 * @param onBind Unit function to override onBindViewHolder of RecyclerView.Adapter.
 * */
fun <T, U : ViewDataBinding> RecyclerView.setUpRecyclerView(@LayoutRes layoutRes: Int, itemList: ArrayList<T>, onBind: ((item: T, binder: U, position: Int) -> Unit)) = BaseAdapter(this, layoutRes, itemList, onBind)

/***
 * This generic class to implement recycler-view's adapter.
 * */
class BaseAdapter<in T, U : ViewDataBinding>(recyclerView: RecyclerView,
                                             @LayoutRes private val layoutRes: Int, arrList: ArrayList<T>, private val onBind: (item: T, binder: U, position: Int) -> Unit) : RecyclerView.Adapter<BaseAdapter.ViewHolder<U>>() {

    private var listItem = arrList

    init {
        recyclerView.adapter = this
    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    fun getItems(): ArrayList<in T> {
        return listItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<U> {
        return ViewHolder(DataBindingUtil.bind(parent.inflate(getLayout()))!!)
    }

    override fun onBindViewHolder(holder: ViewHolder<U>, position: Int) {
        val item = listItem[position]
        onBind.invoke(item, holder.binding, position)
    }

    class ViewHolder<out V : ViewDataBinding>(internal val binding: V) : RecyclerView.ViewHolder(binding.root)

    @LayoutRes
    private fun getLayout(): Int = layoutRes
}