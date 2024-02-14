package dev.roanoke.common.cobblemontools.formats

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore
import com.cobblemon.mod.common.net.messages.client.storage.party.SetPartyPokemonPacket
import com.cobblemon.mod.common.pokemon.Pokemon
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import dev.roanoke.common.cobblemontools.CobblemonTools
import dev.roanoke.common.cobblemontools.util.PokemonConversions
import net.minecraft.server.network.ServerPlayerEntity

class CTTeam(private val party: MutableList<CTPokemon?>,
             var name: String = "Default Team",
             val id: String = ""
) : Iterable<CTPokemon?> {

    private val MAX_SIZE = 6

    companion object {
        fun fromParty(playerParty: PlayerPartyStore): CTTeam {
            var party: MutableList<CTPokemon?> = mutableListOf()
            for (i in 0..5) {
                var pokemon: Pokemon? = playerParty.get(i)
                if (pokemon == null) {
                    party.add(null)
                } else {
                    party.add(PokemonConversions.getDataFromPokemon(pokemon))
                }
            }
            return CTTeam(party)
        }

        fun fromPlayerParty(player: ServerPlayerEntity): CTTeam {
            var playerParty: PlayerPartyStore? = null
            playerParty = Cobblemon.storage.getParty(player)
            return fromParty(playerParty)
        }

        fun fromJson(json: JsonObject): CTTeam? {
            try {
                val teamArray = json.get("team").asJsonArray
                val teamID = json.get("id").asString
                val teamName = json.get("name").asString
                val team = mutableListOf<CTPokemon?>()

                teamArray.forEach { jsonElement ->
                    try {
                        val teamMemberObject = jsonElement.asJsonObject
                        // fill other fields based on TeamMember class

                        var friendship: Int = 255
                        if (teamMemberObject.has("happiness")) {
                            friendship = teamMemberObject.get("happiness").asInt
                        }

                        val teamMember = CTPokemon(
                            ability = teamMemberObject.get("ability").asString,
                            item = teamMemberObject.get("item").asString,
                            ball = teamMemberObject.get("ball").asString,
                            // assuming evs, ivs are list of integers and moves is a list of strings
                            evs = teamMemberObject.getAsJsonArray("evs").map { it.asInt },
                            form = teamMemberObject.get("form").asString,
                            gender = teamMemberObject.get("gender").asString,
                            ivs = teamMemberObject.getAsJsonArray("ivs").map { it.asInt },
                            level = teamMemberObject.get("level").asInt,
                            moves = teamMemberObject.getAsJsonArray("moves").map { it.asString },
                            nature = teamMemberObject.get("nature").asString,
                            shiny = teamMemberObject.get("shiny").asString,
                            species = teamMemberObject.get("species").asString,
                            happiness = friendship
                        )
                        team.add(teamMember)
                    } catch (e: Exception) {
                        team.add(null)
                    }
                }

                return CTTeam(party = team.map { it }.toMutableList(), name = teamName, id = teamID)

            } catch (e: Exception) {
                CobblemonTools.LOGGER.error("Failed to load team: ", e)
                return null
            }
        }

    }

    fun give(player: ServerPlayerEntity) {
        val team = PokemonConversions.getPokemonFromTeam(this.getTeamList().filterNotNull())
        var party: PlayerPartyStore? = null
        party = Cobblemon.storage.getParty(player)
        var pc = Cobblemon.storage.getPC(player.uuid)

        for (pokemon in party) {
            party.remove(pokemon)
            pc.add(pokemon)
        }

        for (member in team) {
            val pos = party.getFirstAvailablePosition()
            party.add(member)
            party.sendPacketToObservers(
                SetPartyPokemonPacket(
                    party.uuid,
                    pos!!,
                    member
                )
            )
        }

        party.heal()
    }

    fun getTeamList(): MutableList<CTPokemon?> {
        return party
    }

    fun addTeamMember(pokemon: CTPokemon, slot: Int) {
        if (slot < 0 || slot >= MAX_SIZE) {
            throw IllegalArgumentException("Invalid slot")
        }
        party[slot] = pokemon
    }

    fun removeTeamMember(slot: Int) {
        if (slot < 0 || slot >= MAX_SIZE) {
            throw IllegalArgumentException("Invalid slot")
        }
        party[slot] = null
    }

    fun getTeamMember(slot: Int): CTPokemon? {
        if (slot < 0 || slot >= MAX_SIZE) {
            throw IllegalArgumentException("Invalid slot")
        }
        return party[slot]
    }

    override fun hashCode(): Int {
        return party.hashCode()
    }

    override fun iterator(): Iterator<CTPokemon?> {
        return party.iterator()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CTTeam

        if (MAX_SIZE != other.MAX_SIZE) return false
        return party == other.party
    }

    fun toJsonString(): String {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val teamMap = mapOf(
            "name" to name,
            "format" to "cobblemon",
            "team" to this.getTeamList()
        )
        return gson.toJson(teamMap).toString()
    }
}