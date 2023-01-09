package com.brook.common.base.view

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.brook.common.base.databinding.ItemEmptyBinding
import com.brook.common.base.databinding.ItemFooterBinding
import com.brook.common.base.databinding.ItemHeaderBinding
import com.brook.common.base.databinding.ItemLoadingBinding

private const val ITEM_VIEW_TYPE_HEADER = 1
private const val ITEM_VIEW_TYPE_CONTENT = 2
private const val ITEM_VIEW_TYPE_FOOTER = 3
private const val ITEM_VIEW_TYPE_LOADING = 4
private const val ITEM_VIEW_TYPE_EMPTY = 5

private const val TAG = "BrookRecyclerAdapter"

abstract class BrookRecyclerAdapter<T, VB : ViewBinding>(private val vbClazz: Class<VB>) :
    RecyclerView.Adapter<BrookViewHolder>(), OnItemClickListener, OnLoadMoreListener {

    private val dataList = mutableListOf<T>()
    private var isNeedHeader = false
    private var isNeedFooter = false
    private var isNeedEmpty = false
    private var isNeedLoading = false
    private var isLoading = true
    private var hasMoreData = false
    private var mOnItemClickListener: OnItemClickListener? = null
    private var mOnLoadMoreListener: OnLoadMoreListener? = null
    private var mTotal = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrookViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> BrookViewHolder(getHeaderBinding(parent))
            ITEM_VIEW_TYPE_FOOTER -> BrookViewHolder(getFooterBinding(parent))
            ITEM_VIEW_TYPE_LOADING -> BrookViewHolder(getLoadingBinding(parent))
            ITEM_VIEW_TYPE_EMPTY -> BrookViewHolder(getEmptyBinding(parent))
            else -> BrookViewHolder(getBinding(parent))
        }
    }


    open fun getFooterBinding(parent: ViewGroup): ViewBinding {
        return ItemFooterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    open fun getHeaderBinding(parent: ViewGroup): ViewBinding {
        return ItemHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    open fun getLoadingBinding(parent: ViewGroup): ViewBinding {
        return ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    open fun getEmptyBinding(parent: ViewGroup): ViewBinding {
        return ItemEmptyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun onBindViewHolder(
        holder: BrookViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)
    }

    override fun onBindViewHolder(holder: BrookViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder.position:$position/$itemCount")
        checkLoadMore(holder, position)
        when (getItemViewType(position)) {
            ITEM_VIEW_TYPE_HEADER -> onBindHeaderViewHolder(holder, holder.binding, position)
            ITEM_VIEW_TYPE_FOOTER -> onBindFooterViewHolder(holder, holder.binding, position)
            ITEM_VIEW_TYPE_LOADING -> onBindLoadingViewHolder(holder, holder.binding, position)
            ITEM_VIEW_TYPE_EMPTY -> onBindEmptyViewHolder(holder, holder.binding, position)
            else -> {
                holder.setOnItemClickListener(this)
                onBindViewHolder(holder, holder.binding as VB, position, getItemData(position))
            }
        }
    }


    open fun onBindFooterViewHolder(
        holder: BrookViewHolder,
        binding: ViewBinding,
        position: Int

    ) {
        if (holder.binding is ItemFooterBinding) {
            if (hasMoreData) {
                holder.binding.text.text = "加载更多数据"
            } else {
                holder.binding.text.text = "没有更多数据"
            }
        }
    }

    open fun onBindHeaderViewHolder(holder: BrookViewHolder, binding: ViewBinding, position: Int) {
        if (holder.binding is ItemHeaderBinding) {
            if (hasMoreData) {
                holder.binding.text.text = "加载更多数据"
            } else {
                holder.binding.text.text = "没有更多数据"
            }
        }
    }


    open fun onBindEmptyViewHolder(
        holder: BrookViewHolder,
        binding: ViewBinding,
        position: Int
    ) {
    }

    open fun onBindLoadingViewHolder(
        holder: BrookViewHolder,
        binding: ViewBinding,
        position: Int
    ) {
    }

    override fun getItemCount(): Int {
        val dataCount = getDataCount()
        val itemCount =
            if (isNeedLoading || isNeedEmpty || isNeedHeader || isNeedFooter) {
                dataCount + 1
            } else {
                dataCount
            }

        return itemCount
    }

    fun getDataCount(): Int = dataList.size

    override fun getItemViewType(position: Int): Int {
        val dataCount = getDataCount()
        if (dataCount == 0) {
            if (isNeedLoading && isLoading) {
                return ITEM_VIEW_TYPE_LOADING
            } else {
                return ITEM_VIEW_TYPE_FOOTER
            }
        } else {
            if (isNeedHeader && position == 0) {
                return ITEM_VIEW_TYPE_HEADER
            }
            if (isNeedFooter && position == dataCount) {
                return ITEM_VIEW_TYPE_FOOTER
            }
        }

        return ITEM_VIEW_TYPE_CONTENT
    }

    private fun checkLoadMore(holder: BrookViewHolder, position: Int) {
        hasMoreData = mTotal > dataList.size
        if (isNeedFooter && hasMoreData && position == itemCount - 1) {
            holder.binding.root.post {
                onLoadMore()
            }
        }

    }

    private fun getItemData(position: Int): T {
        return if (isNeedHeader) {
            dataList[position - 1]
        } else {
            dataList[position]
        }
    }

    fun setDataList(dataList: List<T>) {
        this.isLoading = false
        this.dataList.clear()
        this.dataList.addAll(dataList)
        notifyDataSetChanged()
    }

    fun addDataList(dataList: List<T>) {
        this.isLoading = false
        this.dataList.addAll(dataList)
        notifyDataSetChanged()
    }


    fun setTotalCount(total: Int) {
        mTotal = total
        isNeedFooter = true
        hasMoreData = mTotal > dataList.size
    }


    private fun getBinding(parent: ViewGroup): VB {
        return vbClazz.getMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        ).invoke(null, LayoutInflater.from(parent.context), parent, false) as VB
    }


    fun setOnLoadMoreListener(listener: OnLoadMoreListener): BrookRecyclerAdapter<T, VB> {
        this.mOnLoadMoreListener = listener
        return this
    }

    fun setOnItemClickListener(listener: OnItemClickListener): BrookRecyclerAdapter<T, VB> {
        this.mOnItemClickListener = listener
        return this
    }

    override fun onItemClick(position: Int, itemView: View) {
        mOnItemClickListener?.onItemClick(position, itemView)
    }

    override fun onLoadMore() {
        mOnLoadMoreListener?.onLoadMore()
    }

    fun needLoading(needLoading: Boolean): BrookRecyclerAdapter<T, VB> {
        this.isNeedLoading = needLoading
        this.isLoading = true
        return this
    }

    fun showLoading(showLoading: Boolean): BrookRecyclerAdapter<T, VB> {
        this.isLoading = showLoading
        return this
    }

    fun needEmpty(needEmpty: Boolean): BrookRecyclerAdapter<T, VB> {
        this.isNeedEmpty = needEmpty
        return this
    }

    abstract fun onBindViewHolder(holder: BrookViewHolder, binding: VB, position: Int, data: T)
}

interface OnLoadMoreListener {
    fun onLoadMore()
}

interface OnItemClickListener {
    fun onItemClick(position: Int, itemView: View)
}

class BrookViewHolder(val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root),
    View.OnClickListener {
    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
        itemView.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        onItemClickListener?.onItemClick(adapterPosition, v)
    }


}

