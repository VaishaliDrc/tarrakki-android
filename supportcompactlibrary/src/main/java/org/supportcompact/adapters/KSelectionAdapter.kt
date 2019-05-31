package org.supportcompact.adapters

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.util.SparseBooleanArray
import android.view.ViewGroup
import org.supportcompact.ktx.inflate

/*fun setAdapter(list: MutableList<EditPurchaseEntryListResponse.Data.SalesGroup.SoldProduct>) {
    adapter = setUpAdapter(list,
        ChoiceMode.NONE,
        R.layout.row_edit_purchase_entry,
        { item, binder: RowEditPurchaseEntryBinding?, position, adapter ->
            binder?.vm = item
            binder?.executePendingBindings()
        },
        { item, position, adapter ->

        }
    )
    rv_purchase_entry_edit_products.adapter = adapter
}*/

fun <ITEM, U : ViewDataBinding> setUpAdapter(
        items: MutableList<ITEM>,
        choiceMode: ChoiceMode = ChoiceMode.NONE,
        layoutResId: Int,
        bindHolder: ((item: ITEM, binder: U?, position: Int, adapter: KSelectionAdapter<ITEM, U>) -> Unit)? = null,
        itemClick: ((item: ITEM, position: Int, adapter: KSelectionAdapter<ITEM, U>) -> Unit)? = null,
        isToggleEnable: Boolean = true
): KSelectionAdapter<ITEM, U> {
    return KSelectionAdapter(items, choiceMode, layoutResId, bindHolder, itemClick, isToggleEnable)
}

class KSelectionAdapter<ITEM, U : ViewDataBinding>(
        var items: MutableList<ITEM>,
        val choiceMode: ChoiceMode,
        val layoutResId: Int,
        val onBind: ((ITEM, U?, Int, KSelectionAdapter<ITEM, U>) -> Unit)?,
        val itemClick: ((ITEM, Int, KSelectionAdapter<ITEM, U>) -> Unit)? = null,
        var isToggleEnable: Boolean
) : RecyclerView.Adapter<KSelectionAdapter.ViewHolder<U>>() {

    private var filterItems: MutableList<ITEM>? = arrayListOf()

    init {
        filterItems?.addAll(items)
    }

    val selectedItemViewCount: Int
        get() = selectedItemViews.size()

    private val selectedItemViews = SparseBooleanArray()

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): ViewHolder<U> {
        return ViewHolder(DataBindingUtil.bind(parent.inflate(layoutResId)))
    }

    override fun onBindViewHolder(holder: ViewHolder<U>, position: Int) {
        val item = items[position]
        onBind?.invoke(item, holder.binding, position, this)

        holder.binding?.root?.setOnClickListener {
            itemClick?.invoke(items[position], position, this)
            if (isToggleEnable) {
                toggleItemView(position)
            }
        }
    }

    class ViewHolder<out V : ViewDataBinding>(internal val binding: V?) : RecyclerView.ViewHolder(binding?.root!!)

    fun addItems(items: MutableList<ITEM>) {
        val lastPostion = items.size - 1
        addItemsInternal(items)
        notifyItemRangeChanged(lastPostion, items.size)
    }

    fun addItem(item: ITEM) {
        addItemInternal(item)
        if (items.isEmpty()) {
            notifyItemInserted(0)
        } else {
            notifyItemInserted(items.size)
        }
    }

    fun removeItem(position: Int) {
        removeItemInternal(position)
        notifyItemRemoved(position)
    }

    fun clearItems() {
        clearItemsInternal()
        clearSelectedItemViews()
    }

    fun isItemViewToggled(position: Int): Boolean = selectedItemViews.get(position, false)

    fun getSelectedItemViews(): MutableList<Int> {
        val items: MutableList<Int> = arrayListOf()
        (0 until selectedItemViews.size()).mapTo(items) { selectedItemViews.keyAt(it) }
        return items
    }

    fun getSelectedItems(): MutableList<ITEM> {
        val items: MutableList<ITEM> = ArrayList()
        if (this.items?.isNotEmpty() == true && getSelectedItemViews().size <= this.items.size) {
            for (it in getSelectedItemViews()) {
                items.add(this.items[it])
            }
        }
        return items
    }

    fun getAllItems(): MutableList<ITEM>? {
        return this.filterItems
    }

    fun clearSelectedItemViews() {
        selectedItemViews.clear()
        notifyDataSetChanged()
    }

    open fun toggleItemView(position: Int) {
        when (choiceMode) {
            ChoiceMode.NONE -> return
            ChoiceMode.SINGLE -> {
                getSelectedItemViews().forEach {
                    selectedItemViews.delete(it)
                    notifyItemChanged(it)
                }
                selectedItemViews.put(position, true)
            }

            ChoiceMode.MULTIPLE -> if (isItemViewToggled(position)) {
                selectedItemViews.delete(position)
            } else {
                selectedItemViews.put(position, true)
            }
        }
        notifyItemChanged(position)
    }

    protected open fun addItemsInternal(items: MutableList<ITEM>) {
        this.items.addAll(items)
        this.filterItems?.addAll(items)
    }

    protected open fun addItemInternal(item: ITEM) {
        this.items.add(item)
        this.filterItems?.add(item)
    }

    protected open fun removeItemInternal(position: Int) {
        removeSelectedItemView(position)

        this.items.removeAt(position)
        this.filterItems?.removeAt(position)
    }

    protected open fun clearItemsInternal() {
        this.items.clear()
        this.filterItems?.clear()
    }

    private fun removeSelectedItemView(position: Int) {
        val selectedPositions = getSelectedItemViews()
        if (isItemViewToggled(position)) {
            selectedPositions.removeAt(selectedPositions.indexOf(position))
        }

        selectedItemViews.clear()
        for (selectedPosition in selectedPositions) {
            selectedItemViews.put(if (position > selectedPosition) selectedPosition else selectedPosition - 1, true)
        }
    }

    fun setFilter(list: MutableList<ITEM>) {
        clearSelectedItemViews()
        this.items.clear()
        this.items.addAll(list)
        notifyDataSetChanged()
    }

    fun initFilter() {
        this.items.clear()
        filterItems?.let { this.items.addAll(it) }
        notifyDataSetChanged()
    }
}

enum class ChoiceMode {
    NONE, SINGLE, MULTIPLE
}