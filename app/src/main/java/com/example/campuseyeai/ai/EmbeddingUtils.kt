package com.example.campuseyeai.ai

import org.json.JSONArray

object EmbeddingUtils {

    fun toJson(
        embedding: FloatArray
    ): String {

        val json = JSONArray()

        embedding.forEach {

            json.put(it)

        }

        return json.toString()

    }

    fun fromJson(
        json: String
    ): FloatArray {

        val array = JSONArray(json)

        val result =
            FloatArray(array.length())

        for (i in 0 until array.length()) {

            result[i] =
                array.getDouble(i).toFloat()

        }

        return result

    }

}