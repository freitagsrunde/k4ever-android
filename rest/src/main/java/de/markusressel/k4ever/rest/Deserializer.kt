/*
 * Copyright (C) 2018 Markus Ressel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.markusressel.k4ever.rest

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.salomonbrys.kotson.gsonTypeToken
import com.google.gson.Gson
import java.lang.reflect.Type

/**
 * Extension function to create a single element deserializer for the given type
 */
inline fun <reified T : Any> singleDeserializer(): ResponseDeserializable<T> {
    return SingleDeserializer(gsonTypeToken<T>())
}

/**
 * Extension function to create a list deserializer for the given type
 */
inline fun <reified T : Any> listDeserializer(): ResponseDeserializable<List<T>> {
    return ListDeserializer(gsonTypeToken<List<T>>())
}

/**
 * Generic single element deserializer
 */
class SingleDeserializer<T : Any>(val type: Type) : ResponseDeserializable<T> {
    override fun deserialize(content: String): T? {
        return Gson().fromJson(content, type)
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

        return Gson().fromJson(content, type)
    }
}