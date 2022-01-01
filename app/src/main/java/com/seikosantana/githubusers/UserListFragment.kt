package com.seikosantana.githubusers

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class UserListFragment : Fragment() {

    private lateinit var users: ArrayList<UserMain>
    private lateinit var usersRecyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        users = arguments?.getParcelableArrayList("users")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_follows, container, false)
        usersRecyclerView = view.findViewById(R.id.rvFollows)
        usersRecyclerView.layoutManager = LinearLayoutManager(view.context)
        usersRecyclerView.adapter = UserListAdapter(users)
        return view
    }

    companion object {

        @JvmStatic
        fun newInstance(users: ArrayList<UserMain>) =
            UserListFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList("users", users)
                }
            }
    }
}