package com.brook.common.base

import android.os.Handler
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.brook.common.base.databinding.FragmentLoadMoreBinding
import com.brook.common.base.databinding.ItemCountBinding
import com.brook.common.base.fragment.BaseFragment
import com.brook.common.base.view.BrookRecyclerAdapter
import com.brook.common.base.view.BrookViewHolder

class LoadMoreFragment : BaseFragment<FragmentLoadMoreBinding>() {
    override fun inflateBinding(container: ViewGroup?): FragmentLoadMoreBinding {
        return FragmentLoadMoreBinding.inflate(layoutInflater, container, false)
    }

    override fun initView(binding: FragmentLoadMoreBinding) {
        mBinding.list.layoutManager = LinearLayoutManager(activity)
        mBinding.list.adapter = adapter

        Handler().postDelayed(
            Runnable {
                val dataList = mutableListOf<Int>()
                var i = 0
                while (i++ < 20) {
                    dataList.add(i)
                }
                adapter.setTotalCount(100)
                adapter.setDataList(dataList)
            }, 3000
        )
        adapter.needLoading(true)
    }

    private val adapter =
        object : BrookRecyclerAdapter<Int, ItemCountBinding>(ItemCountBinding::class.java) {
            override fun onBindViewHolder(
                holder: BrookViewHolder,
                binding: ItemCountBinding,
                position: Int,
                data: Int
            ) {
                binding.text.text = data.toString()
            }

            override fun onLoadMore() {
                Handler().postDelayed(
                    Runnable {
                        val dataList = mutableListOf<Int>()
                        var i = itemCount - 1
                        while (i++ < 20 + itemCount - 1) {
                            dataList.add(i)
                        }
                        addDataList(dataList)
                    }, 1500
                )
            }
        }

}