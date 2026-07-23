package com.akwiz.android.data.local

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

/** One generic serializer for both stores — the only difference is the type. */
internal class JsonSerializer<T>(
    private val json: Json,
    private val kSerializer: KSerializer<T>,
    override val defaultValue: T,
) : Serializer<T> {

    override suspend fun readFrom(input: InputStream): T =
        try {
            json.decodeFromString(kSerializer, input.readBytes().decodeToString())
        } catch (e: SerializationException) {
            // Surfaced to the store's corruption handler, which replaces the file.
            throw CorruptionException("Unreadable data on disk", e)
        }

    override suspend fun writeTo(t: T, output: OutputStream) {
        output.write(json.encodeToString(kSerializer, t).encodeToByteArray())
    }
}
