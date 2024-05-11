package dev.roanoke.common.cobblemontools.util

import com.cobblemon.mod.common.api.abilities.Ability
import com.cobblemon.mod.common.api.moves.Moves
import com.cobblemon.mod.common.api.pokemon.Natures
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.pokemon.Gender
import com.cobblemon.mod.common.pokemon.Pokemon
import dev.roanoke.common.cobblemontools.CobblemonTools
import dev.roanoke.common.cobblemontools.formats.CTPokemon
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

object PokemonConversions {

        fun getPokemonFromTeam(team: List<CTPokemon>): List<Pokemon> {
            return team.map {
                this.getPokemonFromData(it)
            }
        }

        fun getDataFromPokemon(pokemon: Pokemon): CTPokemon {
            var ivs: MutableList<Int> = mutableListOf(0, 0, 0, 0, 0, 0)

            ivs[0] = pokemon.ivs.getOrDefault(Stats.HP)
            ivs[1] = pokemon.ivs.getOrDefault(Stats.ATTACK)
            ivs[2] = pokemon.ivs.getOrDefault(Stats.DEFENCE)
            ivs[3] = pokemon.ivs.getOrDefault(Stats.SPECIAL_ATTACK)
            ivs[4] = pokemon.ivs.getOrDefault(Stats.SPECIAL_DEFENCE)
            ivs[5] = pokemon.ivs.getOrDefault(Stats.SPEED)

            var evs: MutableList<Int> = mutableListOf(0, 0, 0, 0, 0, 0)

            evs[0] = pokemon.evs.getOrDefault(Stats.HP)
            evs[1] = pokemon.evs.getOrDefault(Stats.ATTACK)
            evs[2] = pokemon.evs.getOrDefault(Stats.DEFENCE)
            evs[3] = pokemon.evs.getOrDefault(Stats.SPECIAL_ATTACK)
            evs[4] = pokemon.evs.getOrDefault(Stats.SPECIAL_DEFENCE)
            evs[5] = pokemon.evs.getOrDefault(Stats.SPEED)

            var sdGender = pokemon.gender.showdownName
            var gender: String
            if (sdGender == "M") {
                gender = "male"
            } else if (sdGender == "F") {
                gender = "female"
            } else {
                gender = "genderless"
            }

            var formName = "normal"
            if (pokemon.form.name.lowercase() != "normal") {
                try {
                    formName = pokemon.form.aspects[0]
                } catch (e: Exception) {
                    CobblemonTools.LOGGER.error("Failed to get Form Aspect from Pokemon: " + pokemon.species.name)
                }
            }

            val teamMember = CTPokemon(
                species = pokemon.species.resourceIdentifier.toString(),
                form = formName,
                gender = gender,
                shiny = if (pokemon.shiny) "yes" else "no",
                level = pokemon.level,
                ability = pokemon.ability.template.name,
                nature = pokemon.nature.name.toString(),
                item = Registries.ITEM.getId(pokemon.heldItem().item).toString(),
                ball = pokemon.caughtBall.name.toString(),
                ivs = ivs,
                evs = evs,
                happiness = pokemon.friendship,
                moves = pokemon.moveSet.map { it.name }
            )
            return teamMember
        }

        fun getPokemonFromData(data: CTPokemon): Pokemon {

            val result = Pokemon()
            result.initialize()

            val speciesTest = PokemonSpecies.getByIdentifier(Identifier(data.species))
            if (speciesTest != null) {
                result.species = speciesTest
            } else {
                CobblemonTools.LOGGER.error("Failed to find Species (" + data.species + "), using random.")
            }

            //PokemonProperties.parse((result.species.forms.find { it.name.lowercase() == data.form.lowercase() }
            //    ?: result.species.standardForm).name).apply(result)
            PokemonProperties.parse(data.form).apply(result)
            result.updateAspects()

            when (data.gender.lowercase()) {
                "male" -> result.gender = Gender.MALE
                "female" -> result.gender = Gender.FEMALE
                "genderless" -> result.gender = Gender.GENDERLESS
            }

            if (result.isPossibleFriendship(data.happiness)) {
                result.setFriendship(data.happiness)
            } else {
                result.setFriendship(255)
                CobblemonTools.LOGGER.info("Invalid friendship, set to default (255)")
            }

            val isShiny = data.shiny == "true"
            result.shiny = isShiny

            result.level = data.level

            val ability: Ability = result.form.abilities.find {
                it.template.name == data.ability
            }?.template?.create(forced=true) ?: result.form.abilities.first().template.create(forced=true)

            result.updateAbility(ability)

            result.nature =
                Natures.getNature(Identifier(data.nature.lowercase())) ?: Natures.getRandomNature()

            val held_item: ItemStack = Registries.ITEM.get(Identifier(data.item)).defaultStack
            result.swapHeldItem(held_item, false)

            result.moveSet.clear()
            for (move in data.moves) {
                val template = Moves.getByName(move) // get by ID vs display name
                if (template != null) {
                    result.moveSet.add(template.create(template.pp, 3)) // raise PP stages
                } else {
                    CobblemonTools.LOGGER.info("Failed to add move $move to Pokemon")
                }
            }

            result.ivs[Stats.HP] = data.ivs[0]
            result.ivs[Stats.ATTACK] = data.ivs[1]
            result.ivs[Stats.DEFENCE] = data.ivs[2]
            result.ivs[Stats.SPECIAL_ATTACK] = data.ivs[3]
            result.ivs[Stats.SPECIAL_DEFENCE] = data.ivs[4]
            result.ivs[Stats.SPEED] = data.ivs[5]

            result.evs[Stats.HP] = data.evs[0]
            result.evs[Stats.ATTACK] = data.evs[1]
            result.evs[Stats.DEFENCE] = data.evs[2]
            result.evs[Stats.SPECIAL_ATTACK] = data.evs[3]
            result.evs[Stats.SPECIAL_DEFENCE] = data.evs[4]
            result.evs[Stats.SPEED] = data.evs[5]

            return result

        }
}