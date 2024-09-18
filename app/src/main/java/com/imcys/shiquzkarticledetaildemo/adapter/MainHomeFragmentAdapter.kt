package com.imcys.shiquzkarticledetaildemo.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * 主要负责首页的ViewPager的适配器
 */
class MainHomeFragmentAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    var fragments: List<Fragment>
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = fragments.size


    override fun createFragment(position: Int): Fragment = fragments[position]
}