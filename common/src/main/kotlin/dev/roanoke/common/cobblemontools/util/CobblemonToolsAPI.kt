package dev.roanoke.common.cobblemontools.util

import com.google.gson.JsonParser
import dev.roanoke.common.cobblemontools.CobblemonTools
import dev.roanoke.common.cobblemontools.formats.CTTeam
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.concurrent.CompletableFuture


object CobblemonToolsAPI {

    suspend fun getTeamById(teamID: String): CTTeam? = withContext(Dispatchers.IO) {
        val urlString = CobblemonTools.config.CobblemonToolsURL + "/api/v1/teams/$teamID"
        CobblemonTools.LOGGER.info("Trying to get Cobblemon Tools Team from URL: $urlString")
        try {
            val url = URL(urlString)
            val con = url.openConnection() as HttpURLConnection
            con.requestMethod = "GET"
            if (con.responseCode == HttpURLConnection.HTTP_OK) {
                con.inputStream.bufferedReader(StandardCharsets.UTF_8).use { reader ->
                    val response = reader.readText()
                    CobblemonTools.LOGGER.info("Response: $response")
                    val responseJson = JsonParser.parseString(response).asJsonObject
                    if (responseJson.has("name")) {
                        CTTeam.fromJson(responseJson)
                    } else {
                        CobblemonTools.LOGGER.info("Incorrect Response type from Cobblemon Tools: No Name Key. Check the Team ID is valid!")
                        null
                    }
                }
            } else {
                throw IOException("GET request failed, response code: ${con.responseCode}")
            }
        } catch (e: IOException) {
            CobblemonTools.LOGGER.error("IO Exception when fetching Cobblemon Tools Team, is Cobblemon Tools up?", e)
            null
        }
    }

    suspend fun uploadTeam(team: CTTeam): String? = withContext(Dispatchers.IO) {
        val urlString = CobblemonTools.config.CobblemonToolsURL + "/api/v1/teams"
        CobblemonTools.LOGGER.info("Trying to create Cobblemon Tools Team with URL: $urlString")
        try {
            val url = URL(urlString)
            val con = url.openConnection() as HttpURLConnection
            con.requestMethod = "POST"
            con.setRequestProperty("Content-Type", "application/json; utf-8")
            con.setRequestProperty("Accept", "application/json")
            con.doOutput = true

            OutputStreamWriter(con.outputStream, StandardCharsets.UTF_8).use { writer ->
                writer.write(team.toJsonString())
                writer.flush()
            }

            if (con.responseCode == HttpURLConnection.HTTP_OK) {
                con.inputStream.bufferedReader(StandardCharsets.UTF_8).use { reader ->
                    val response = reader.readText()
                    val responseJson = JsonParser.parseString(response).asJsonObject
                    if (responseJson.has("team_url")) {
                        CobblemonTools.config.CobblemonToolsURL + responseJson.get("team_url").asString
                    } else {
                        CobblemonTools.LOGGER.info("Failed to upload Team to Cobblemon Tools...")
                        null
                    }
                }
            } else {
                throw IOException("POST request failed, response code: ${con.responseCode}")
            }
        } catch (e: IOException) {
            CobblemonTools.LOGGER.error(
                "IO Exception when trying to create Cobblemon Tools Team, is Cobblemon Tools up?",
                e
            )
            null
        }
    }


}
