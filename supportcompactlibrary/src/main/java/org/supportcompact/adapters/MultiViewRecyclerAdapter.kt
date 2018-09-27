package org.supportcompact.adapters

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import org.supportcompact.ktx.inflate

/***
 * This is extension to setup adapter by giving fallowing inputs
 * @param itemList Data to to be bound with layout.
 * @param onBind Unit function to override onBindViewHolder of RecyclerView.Adapter.
 * */
fun <T : WidgetsViewModel> RecyclerView.setUpMultiViewRecyclerAdapter(itemList: ArrayList<T>, onBind: ((item: T, binder: ViewDataBinding, position: Int) -> Unit)) = MultiViewRecyclerAdapter(this, itemList, onBind)


class MultiViewRecyclerAdapter<T : WidgetsViewModel>(recyclerView: RecyclerView, private val list: ArrayList<T>, private val onBind: (item: T, binder: ViewDataBinding, position: Int) -> Unit) : RecyclerView.Adapter<MultiViewRecyclerAdapter.ViewHolder<ViewDataBinding>>() {

    init {
        recyclerView.adapter = this
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].layoutId()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<ViewDataBinding> {
        return ViewHolder(DataBindingUtil.bind(parent.inflate(viewType))!!)
    }

    override fun onBindViewHolder(holder: ViewHolder<ViewDataBinding>, position: Int) {
        val item = list[position]
        onBind.invoke(item, holder.binding, position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder<out V : ViewDataBinding>(val binding: V) : RecyclerView.ViewHolder(binding.root)
}