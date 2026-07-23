package com.akwiz.android.data

import com.akwiz.android.domain.Question
import java.security.MessageDigest

private const val FIELD = "␟"

/**
 * Hashes the questions themselves rather than the bytes they arrived in.
 *
 * Reformatting the source JSON leaves the hash alone, and the same questions from
 * the network and the bundled copy hash identically — so falling back doesn't throw
 * away a session that's still valid. Only a real change to a question moves it.
 */
internal fun contentHashOf(questions: List<Question>): String {
    val canonical = questions.joinToString("\n") { q ->
        "${q.id}$FIELD${q.text}$FIELD${q.options.joinToString(FIELD)}$FIELD${q.correctIndex}"
    }
    return MessageDigest.getInstance("SHA-256")
        .digest(canonical.toByteArray(Charsets.UTF_8))
        .joinToString("") { "%02x".format(it) }
}
