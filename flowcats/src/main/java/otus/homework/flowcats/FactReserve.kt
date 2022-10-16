package otus.homework.flowcats

/** for https://catfact.ninja/fact */
data class FactReserve(val fact: String, val length: Int) {
    fun toFact(): Fact = Fact.EMPTY.copy(text = this.fact)
}
