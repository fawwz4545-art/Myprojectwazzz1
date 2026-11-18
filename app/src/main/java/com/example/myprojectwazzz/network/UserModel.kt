package com.example.myprojectwazzz.network

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("data")
    val users: List<UserItem>
)

data class UserItem(
    val id: Int = 0,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    val email: String,
    val avatar: String = ""
)
