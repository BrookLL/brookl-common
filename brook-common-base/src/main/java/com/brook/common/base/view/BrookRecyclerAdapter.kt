package com.brook.common.base.view

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.brook.common.base.databinding.ItemFooterBinding
import com.brook.common.base.databinding.ItemHeaderBinding

private const val ITEM_VIEW_TYPE_HEADER = 1
private const val ITEM_VIEW_TYPE_CONTENT = 2
private const val ITEM_VIEW_TYPE_FOOTER = 3

private const val TAG = "BrookRecyclerAdapter"
abstract class BrookRecyclerAdapter<T, VB : ViewBinding>(private val vbClazz: Class<VB>) :
    RecyclerView.Adapter<BrookViewHolder>() {

    private val dataList = mutableListOf<T>()
    private var isNeedHeader = false
    private var isNeedFooter = false
    private var hasMoreData = false
    private var mOnItemClickListener: OnItemClickListener? = null
    private var mOnLoadMoreListener: OnLoadMoreListener? = null
    private var mTotal = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrookViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> BrookViewHolder(getHeaderBinding(parent))
            ITEM_VIEW_TYPE_FOOTER -> BrookViewHolder(getFooterBinding(parent))
            else -> BrookViewHolder(getBinding(parent))
        }
    }


    open fun getFooterBinding(parent: ViewGroup): ViewBinding {
        return ItemFooterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    open fun getHeaderBinding(parent: ViewGroup): ViewBinding {
        return ItemHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun onBindViewHolder(
        holder: BrookViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)
    }

    override fun onBindViewHolder(holder: BrookViewHolder, position: Int) {
        Log.d(TAG,"onBindViewHolder.position:$position/$itemCount")
        checkLoadMore(holder,position)
        when (getItemViewType(position)) {
            ITEM_VIEW_TYPE_HEADER -> onBindHeaderViewHolder(holder, holder.binding, position)
            ITEM_VIEW_TYPE_FOOTER -> onBindFooterViewHolder(holder, holder.binding, position)
            else -> {
                mOnItemClickListener?.let { holder.setOnItemClickListener(it) }
                onBindViewHolder(holder, holder.binding as VB, position, getItemData(position))
            }
        }
    }

    private fun checkLoadMore(holder: BrookViewHolder, position: Int) {
        hasMoreData = mTotal > dataList.size
        if (isNeedFooter&&hasMoreData&&position == itemCount-1&&mOnLoadMoreListener!=null){
            holder.binding.root.post {
                mOnLoadMoreListener!!.onLoadMore()
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
        this.dataList.clear()
        this.dataList.addAll(dataList)
        notifyDataSetChanged()
    }

    fun addDataList(dataList: List<T>) {
        this.dataList.addAll(dataList)
        notifyDataSetChanged()
    }


    fun setTotalCount(total: Int) {
        mTotal = total
        isNeedFooter = true
        hasMoreData = mTotal > dataList.size
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

    override fun getItemCount(): Int {
        var dataCount = getDataCount()
        if (isNeedHeader) {
            dataCount += 1
        }
        if (isNeedFooter) {
            dataCount += 1
        }
        return dataCount
    }

    fun getDataCount(): Int = dataList.size

    override fun getItemViewType(position: Int): Int {
        if (isNeedHeader && position == 0) {
            return ITEM_VIEW_TYPE_HEADER
        }
        if (isNeedFooter && position == dataList.size) {
            return ITEM_VIEW_TYPE_FOOTER
        }
        return ITEM_VIEW_TYPE_CONTENT
    }

    private fun getBinding(parent: ViewGroup): VB {
        return vbClazz.getMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        ).invoke(null, LayoutInflater.from(parent.context), parent, false) as VB
    }

    abstract fun onBindViewHolder(holder: BrookViewHolder, binding: VB, position: Int, data: T)

    fun setOnLoadMoreListener(listener:OnLoadMoreListener):BrookRecyclerAdapter<T,VB>{
        this.mOnLoadMoreListener = listener
        return this
    }
    fun setOnItemClickListener(listener:OnItemClickListener):BrookRecyclerAdapter<T,VB>{
        this.mOnItemClickListener = listener
        return this
    }

    interface OnLoadMoreListener{
        fun onLoadMore()
    }
    interface OnItemClickListener {
        fun onItemClick(position: Int, itemView: View)
    }

}
class BrookViewHolder(val binding: ViewBinding) :
    RecyclerView.ViewHolder(binding.root) {
    private var onItemClickListener: BrookRecyclerAdapter.OnItemClickListener? = null
    private val onClickListener = View.OnClickListener {
        onItemClickListener?.onItemClick(adapterPosition, itemView)
    }

    fun setOnItemClickListener(onItemClickListener: BrookRecyclerAdapter.OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
        itemView.setOnClickListener(onClickListener)
    }


}

