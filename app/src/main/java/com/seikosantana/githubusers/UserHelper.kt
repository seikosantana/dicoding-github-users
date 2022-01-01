package com.seikosantana.githubusers

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import database.SavedUser

class UserHelper {
    companion object {

        lateinit var requestQueue: RequestQueue

        private const val baseUrl = "https://api.github.com"
        private const val token = "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"

        fun getBasicUserInfo(
            username: String,
            resultCallBack: (reposCount: Int, followerCount: Int, followingCount: Int) -> Unit,
            failureCallback: () -> Unit
        ) {
            val url = "$baseUrl/users/$username"
            val basicInfoRequest = object : JsonObjectRequest(Request.Method.GET, url, null,
                {
                    val reposCount = it.getInt("public_repos")
                    val followerCount = it.getInt("followers")
                    val followingCount = it.getInt("following")
                    resultCallBack(reposCount, followerCount, followingCount)
                },
                {
                    failureCallback()
                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "Token $token"
                    return headers
                }
            }

            requestQueue.add(basicInfoRequest)
        }

        fun getFollows(
            followerOrFollowingUrl: String,
            resultCallback: (ArrayList<UserMain>) -> Unit,
            failureCallback: () -> Unit
        ) {
            val followerRequest =
                object : JsonArrayRequest(Request.Method.GET, followerOrFollowingUrl, null,
                    {
                        val users: ArrayList<UserMain> = arrayListOf()
                        if (it.length() == 0) {
                            resultCallback(users)
                        }
                        for (i in 0 until it.length()) {
                            val userJson = it.getJSONObject(i)
                            val avatarUrl = userJson.getString("avatar_url")
                            val username = userJson.getString("login")
                            getBasicUserInfo(
                                username,
                                { reposCount: Int, followersCount: Int, followingCount: Int ->
                                    run {
                                        val userMain =
                                            UserMain(
                                                username,
                                                avatarUrl,
                                                reposCount,
                                                followersCount
                                            )
                                        users.add(userMain)
                                        if (users.size == it.length()) {
                                            resultCallback(users)
                                        }
                                    }
                                },
                                {
                                    failureCallback()
                                })
                        }
                    },
                    {
                        failureCallback()
                    }
                ) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Authorization"] = "Token $token"
                        return headers
                    }
                }

            requestQueue.add(followerRequest)
        }

        fun getRepos(
            reposUrl: String,
            resultCallback: (ArrayList<Repository>) -> Unit,
            failureCallback: () -> Unit
        ) {
            val reposRequest = object : JsonArrayRequest(Request.Method.GET, reposUrl, null,
                {
                    val repos: ArrayList<Repository> = arrayListOf()
                    if (repos.size == 0) {
                        resultCallback(repos)
                    }
                    for (i in 0 until it.length()) {
                        val repoJson = it.getJSONObject(i)
                        val url = repoJson.getString("html_url")
                        val name = repoJson.getString("name")
                        val repository = Repository(name, url)
                        repos.add(repository)
                        if (repos.size == it.length()) {
                            resultCallback(repos)
                        }
                    }
                },
                {
                    failureCallback()
                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "Token $token"
                    return headers
                }
            }
            requestQueue.add(reposRequest)
        }

        fun getUserDetails(
            username: String,
            resultCallback: (UserDetail) -> Unit,
            failureCallback: () -> Unit
        ) {
            val url = "$baseUrl/users/$username"
            val request = object : JsonObjectRequest(Request.Method.GET, url, null,
                {
                    val avatarUrl = it.getString("avatar_url")
                    var name = it.getString("name")
                    var company = it.getString("company")
                    var location = it.getString("location")
                    if (company == "null") {
                        company = ""
                    }
                    if (name == "null") {
                        name = ""
                    }
                    if (location == "null") {
                        location = ""
                    }
                    val followersUrl = it.getString("followers_url")
                    val followingUrl = it.getString("following_url").replace("{/other_user}", "")
                    val url = it.getString("html_url")
                    val reposUrl = it.getString("repos_url")
                    getRepos(reposUrl, {
                        val repos = it
                        getFollows(followersUrl, {
                            val followers = it
                            getFollows(followingUrl, {
                                val following = it
                                val userDetail = UserDetail(
                                    username,
                                    avatarUrl,
                                    name,
                                    company,
                                    location,
                                    repos,
                                    followers,
                                    following,
                                    url
                                )
                                resultCallback(userDetail)
                            }, failureCallback)
                        }, failureCallback)
                    }, failureCallback)
                },
                {
                    failureCallback()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "Token $token"
                    return headers
                }
            }
            requestQueue.add(request)
        }

        fun getSavedUserList(
            savedUserList: List<SavedUser>,
            resultCallback: (ArrayList<UserMain>) -> Unit,
            failureCallback: () -> Unit
        ) {
            val usersResult: ArrayList<UserMain> = arrayListOf()
            for (savedUser in savedUserList) {
                getBasicUserInfo(savedUser.username, {reposCount: Int, followerCount: Int, _: Int -> run {
                    val userMain = UserMain(savedUser.username, savedUser.avatarUrl, reposCount, followerCount)
                    usersResult.add(userMain)
                    if (usersResult.size == savedUserList.size) {
                        resultCallback(usersResult)
                    }
                }}, {
                    failureCallback()
                })
            }
        }

        fun getUsers(
            phrase: String,
            resultCallback: (ArrayList<UserMain>) -> Unit,
            failureCallback: () -> Unit
        ) {
            val jsonRequest = object: JsonObjectRequest(
                Request.Method.GET, "$baseUrl/search/users?q=$phrase", null,
                {
                    val usersJson = it.getJSONArray("items")
                    val users: ArrayList<UserMain> = arrayListOf()
                    for (i in 0 until usersJson.length()!!) {
                        val userJson = usersJson.getJSONObject(i)
                        val avatarUrl = userJson.getString("avatar_url")
                        val username = userJson.getString("login")
                        getBasicUserInfo(
                            username,
                            { reposCount: Int, followersCount: Int, followingCount: Int ->
                                run {
                                    val userMain =
                                        UserMain(username, avatarUrl, reposCount, followersCount)
                                    users.add(userMain)
                                    val length = usersJson.length()
                                    if (users.size == length) {
                                        resultCallback(users)
                                    }
                                }
                            },
                            {
                                failureCallback()
                            })
                    }
                },
                {
                    failureCallback()
                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "Token $token"
                    return headers
                }
            }
            requestQueue.add(jsonRequest)
        }
    }
}