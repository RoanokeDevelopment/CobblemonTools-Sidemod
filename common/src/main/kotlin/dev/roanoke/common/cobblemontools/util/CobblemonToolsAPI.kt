package dev.roanoke.common.cobblemontools.util

import com.google.gson.JsonParser
import dev.roanoke.common.cobblemontools.CobblemonTools
import dev.roanoke.common.cobblemontools.formats.CTTeam
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.CompletableFuture


object CobblemonToolsAPI {

    fun getTeamById(teamID: String): CTTeam? {
        val url: String = CobblemonTools.config.CobblemonToolsURL + "api/v1/teams/" + teamID
        CobblemonTools.LOGGER.info("Trying to get Cobblemon Tools Team from URL: $url")
        val future = CompletableFuture.supplyAsync {
            try {
                val obj = URL(url)
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                val responseCode = con.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val `in` = BufferedReader(
                        InputStreamReader(
                            con.inputStream
                        )
                    )
                    val response = StringBuilder()
                    var line: String?
                    while (`in`.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    `in`.close()
                    return@supplyAsync response.toString()
                } else {
                    throw IOException("GET request failed, response code: $responseCode")
                }
            } catch (e: IOException) {
                e.printStackTrace()
                return@supplyAsync null
            }
        }

        val responseString = future.join()
        println("Response: $responseString")

        val responseJson = JsonParser.parseString(responseString).asJsonObject
        return if (responseJson["name"].asString != null) {
            CTTeam.fromJson(responseJson)
        } else {
            CobblemonTools.LOGGER.info("Response Json didn't have name key")
            null
        }
    }
}
