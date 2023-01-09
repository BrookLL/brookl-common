package com.brook.common.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.brook.common.base.viewbinding.inflate

abstract class BaseFragment<VB:ViewBinding>():Fragment() {
    protected lateinit var mBinding:VB
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = inflateBinding(container)
        return mBinding.root
    }

    abstract fun inflateBinding(container: ViewGroup?): VB

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(mBinding)
    }

    protected abstract fun initView(binding:VB)
}