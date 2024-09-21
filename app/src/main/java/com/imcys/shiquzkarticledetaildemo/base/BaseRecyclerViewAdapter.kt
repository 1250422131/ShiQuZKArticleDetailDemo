package com.imcys.shiquzkarticledetaildemo.base

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding


abstract class BaseRecyclerViewAdapter<VB:ViewBinding,D> : RecyclerView.Adapter<CommonViewHolder<VB>>() {
    var onItemClick: ((D) -> Unit)? = null
}