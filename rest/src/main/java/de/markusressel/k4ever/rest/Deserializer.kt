package de.markusressel.k4ever.rest

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.salomonbrys.kotson.typeToken
import com.google.gson.Gson
import java.lang.reflect.Type

/**
 * Extension function to create a single element deserializer for the given type
 */
inline fun <reified T : Any> singleDeserializer(): ResponseDeserializable<T> {
    return SingleDeserializer(typeToken<T>())
}

/**
 * Extension function to create a list deserializer for the given type
 */
inline fun <reified T : Any> listDeserializer(): ResponseDeserializable<List<T>> {
    return ListDeserializer(typeToken<List<T>>())
}

/**
 * Generic single element deserializer
 */
class SingleDeserializer<T : Any>(val type: Type) : ResponseDeserializable<T> {
    override fun deserialize(content: String): T? {
        return Gson()
                .fromJson(content, type)
    }
}

/**
 * Generic list deserializer
 */
class ListDeserializer<T : Any>(val type: Type) : ResponseDeserializable<List<T>> {
    override fun deserialize(content: String): List<T>? {
        if (content.isEmpty()) {
            return emptyList()
        }

        return Gson()
                .fromJson(content, type)
    }
}