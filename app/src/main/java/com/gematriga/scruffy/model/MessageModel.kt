package com.gematriga.scruffy.model


data class MessageModel(

    var message : String? = "",
    var senderId : String? = "",
    var timeStamp: Long = 0,
    var imageUrl: String?= "",
    var messageType: String?= "",
    var messageDate: String?= ""
)
