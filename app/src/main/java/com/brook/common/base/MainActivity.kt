package com.brook.common.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.brook.common.base.databinding.ActivityMainBinding
import com.brook.common.base.databinding.ItemCountBinding
import com.brook.common.base.view.BrookRecyclerAdapter
import com.brook.common.base.view.BrookViewHolder

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mBinding.list.layoutManager = LinearLayoutManager(this)
        mBinding.list.adapter = adapter
        val dataList = mutableListOf<Int>()
        var i = 0
        while (i++ < 20) {
            dataList.add(i)
        }

        adapter.setTotalCount(100)
        adapter.setDataList(dataList)
        adapter.setOnLoadMoreListener(object : BrookRecyclerAdapter.OnLoadMoreListener {
            override fun onLoadMore() {
                val dataList = mutableListOf<Int>()
                var i = adapter.itemCount - 1
                while (i++ < 20 + adapter.itemCount - 1) {
                    dataList.add(i)
                }
                adapter.addDataList(dataList)
            }

        })
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

        }
}