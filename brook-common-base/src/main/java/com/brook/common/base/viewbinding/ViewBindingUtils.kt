package com.brook.common.base.viewbinding

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

inline fun <reified VB : ViewBinding> LayoutInflater.inflate(
    parent: ViewGroup? = null,
    attachToParent: Boolean = false
): VB {
    val method = VB::class.java.getMethod(
        "inflate",
        LayoutInflater::class.java,
        ViewGroup::class.java,
        Boolean::class.java
    )
    return method.invoke(null, this, parent, attachToParent) as VB
}

inline fun <reified VB : ViewBinding> Activity.inflate(
    parent: ViewGroup? = null,
    attachToParent: Boolean = false
): VB {
    return layoutInflater.inflate(parent, attachToParent)
}
inline fun <reified VB : ViewBinding> Fragment.inflate(
    parent: ViewGroup? = null,
    attachToParent: Boolean = false
): VB {
    return layoutInflater.inflate(parent, attachToParent)
}