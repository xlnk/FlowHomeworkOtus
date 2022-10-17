package otus.homework.flowcats

import retrofit2.http.GET

interface CatsService {

    @GET("random?animal_type=cat")
    suspend fun getCatFact(): Fact

    @GET("https://catfact.ninja/fact")
    suspend fun getCatFactReserve(): FactReserve
}