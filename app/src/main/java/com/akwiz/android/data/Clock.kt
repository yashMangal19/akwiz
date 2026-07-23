package com.akwiz.android.data

/** Time as a dependency, so the session TTL can be tested without waiting a day. */
fun interface Clock {
    fun now(): Long

    companion object {
        val System = Clock { java.lang.System.currentTimeMillis() }
    }
}
