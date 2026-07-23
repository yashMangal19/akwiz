package com.akwiz.android.data.local

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.first

/** The irreplaceable half: in-progress session and the all-time best streak. */
internal interface PlayerStore {
    suspend fun readSession(): SavedSession?
    suspend fun writeSession(session: SavedSession)
    suspend fun clearSession()
    suspend fun bestStreak(): Int
    suspend fun recordBestStreak(value: Int)
}

internal class DataStorePlayerStore(
    private val store: DataStore<PlayerState>,
) : PlayerStore {

    override suspend fun readSession(): SavedSession? = store.data.first().session

    override suspend fun writeSession(session: SavedSession) {
        store.updateData { it.copy(session = session) }
    }

    override suspend fun clearSession() {
        store.updateData { it.copy(session = null) }
    }

    override suspend fun bestStreak(): Int = store.data.first().bestStreak

    // max inside updateData so the read-modify-write is atomic — a plain
    // read-then-write in the repository could lose a concurrent improvement.
    override suspend fun recordBestStreak(value: Int) {
        store.updateData { it.copy(bestStreak = maxOf(it.bestStreak, value)) }
    }
}
