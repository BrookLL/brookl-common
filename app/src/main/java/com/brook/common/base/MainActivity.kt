package com.brook.common.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
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
        val dataList = listOf("加载更多")
        adapter.setDataList(dataList)
        supportFragmentManager.addOnBackStackChangedListener {
            mBinding.list.isVisible = supportFragmentManager.backStackEntryCount == 0
        }
    }

    private val adapter =
        object : BrookRecyclerAdapter<String, ItemCountBinding>(ItemCountBinding::class.java) {
            override fun onBindViewHolder(
                holder: BrookViewHolder,
                binding: ItemCountBinding,
                position: Int,
                data: String
            ) {
                binding.text.text = data
            }

            override fun onItemClick(position: Int, itemView: View) {
                when (position) {
                    0 -> {
                        showFragment(LoadMoreFragment())
                    }
                    else -> {}
                }
            }

        }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().add(R.id.container, fragment, "加载更多")
            .addToBackStack(null).commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}