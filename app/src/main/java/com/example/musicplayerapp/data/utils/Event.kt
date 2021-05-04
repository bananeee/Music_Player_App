package com.example.musicplayerapp.data.utils

/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 *
 * For more information, see:
 * https://medium.com/google-developers/livedata-with-events-ac2622673150
 */
open class Event<out T>(private val content: T) {
    var hasBeenHandle = false
        private set

    fun getContentIfNotHandle(): T? {
        return if (hasBeenHandle)
            null
        else {
            hasBeenHandle = true
            content
        }
    }

    fun peekContent() = content
}