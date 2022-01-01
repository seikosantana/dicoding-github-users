package com.seikosantana.githubusers

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class SectionsPagerAdapter(
    activity: AppCompatActivity,
    private val repositories: ArrayList<Repository>,
    private val followers: ArrayList<UserMain>,
    private val following: ArrayList<UserMain>
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> RepoListFragment.newInstance(repositories)
            1 -> UserListFragment.newInstance(followers)
            else -> UserListFragment.newInstance(following)
        }

    }
}