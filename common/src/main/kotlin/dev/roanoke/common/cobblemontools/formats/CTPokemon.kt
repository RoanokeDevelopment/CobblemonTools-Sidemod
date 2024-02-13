package dev.roanoke.common.cobblemontools.formats

data class CTPokemon(
    val species: String,
    val form: String,
    val gender: String,
    val shiny: String,
    val level: Int,
    val happiness: Int,
    val ability: String,
    val nature: String,
    val item: String,
    val ball: String,
    val ivs: List<Int>,
    val evs: List<Int>,
    val moves: List<String>
)