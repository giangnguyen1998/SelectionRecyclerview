package edu.nuce.apps.newflowtypes

import java.net.HttpURLConnection

object NetworkErrorCodeConverter {

    fun convert(code: Int?): Int {
        return when (code) {
            HttpURLConnection.HTTP_UNAUTHORIZED -> {
                R.string.app_name
            }
            HttpURLConnection.HTTP_BAD_REQUEST -> {
                R.string.agenda
            }
            HttpURLConnection.HTTP_INTERNAL_ERROR -> {
                R.string.event_codelabs_title
            }
            HttpURLConnection.HTTP_UNAVAILABLE -> {
                R.string.app_name
            }
            HttpURLConnection.HTTP_FORBIDDEN -> {
                R.string.title_map
            }
            else -> {
                R.string.title_schedule
            }
        }
    }
}