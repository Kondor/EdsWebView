package com.example.appwebgps.network.utils.result

interface HttpResponse {

    val statusCode: Int

    val statusMessage: String?

    val url: String?
}
