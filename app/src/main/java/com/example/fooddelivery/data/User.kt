package com.example.fooddelivery.data

import java.io.Serializable

class User : Serializable {
    var id = ""
    var name = ""
    var email = ""
    var phone = ""
    var favorites = listOf<String>()
    var address = ""
    var paymentMethod = ""
}