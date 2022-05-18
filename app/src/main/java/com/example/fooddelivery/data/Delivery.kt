package com.example.fooddelivery.data

import java.io.Serializable

class Delivery: Serializable {
    var id = ""
    var description = ""
    var userId = ""
    var completed = false
    var progress = 0
}