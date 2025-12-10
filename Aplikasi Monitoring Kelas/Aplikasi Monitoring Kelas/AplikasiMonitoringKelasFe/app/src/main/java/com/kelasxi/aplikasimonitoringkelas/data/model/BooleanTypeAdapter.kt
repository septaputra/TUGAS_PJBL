package com.kelasxi.aplikasimonitoringkelas.data.model

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class BooleanTypeAdapter : JsonDeserializer<Boolean> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Boolean {
        return when {
            json == null -> false
            json.isJsonPrimitive -> {
                val primitive = json.asJsonPrimitive
                when {
                    primitive.isBoolean -> primitive.asBoolean
                    primitive.isNumber -> primitive.asInt != 0
                    primitive.isString -> {
                        val stringValue = primitive.asString
                        when (stringValue.lowercase()) {
                            "true", "1", "yes", "on" -> true
                            else -> false
                        }
                    }
                    else -> false
                }
            }
            else -> false
        }
    }
}