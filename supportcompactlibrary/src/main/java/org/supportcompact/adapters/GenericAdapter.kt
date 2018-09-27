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
abstract class GenericAdapter<in T, U : ViewDataBinding>(arrList: ArrayList<T>) : RecyclerView.Adapter<GenericAdapter.ViewHolder<U>>() {

    private var listItem = arrList

    override fun getItemCount(): Int {
        return listItem.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<U> {
        return ViewHolder(DataBindingUtil.bind(parent.inflate(getLayout()))!!)
    }

    override fun onBindViewHolder(holder: ViewHolder<U>, position: Int) {
        val item = listItem[position]
        holder.binding.setVariable(getBindingVariable(), item)
        holder.binding.executePendingBindings()
        onBound(item, holder.binding, position)
    }

    class ViewHolder<out V : ViewDataBinding>(internal val binding: V) : RecyclerView.ViewHolder(binding.root)

    @LayoutRes
    abstract fun getLayout(): Int

    abstract fun getBindingVariable(): Int

    /**
     * extra method to perform extra operations on list items
     */
    protected abstract fun onBound(item: T, binder: ViewDataBinding?, position: Int)
}