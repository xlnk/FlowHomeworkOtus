package otus.homework.flowcats

import android.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

private const val TAG = "CatsRepository"

class CatsRepository(
    private val catsService: CatsService,
    private val refreshIntervalMs: Long = 5000
) {

    fun listenForCatFacts(): Flow<Result<Fact>> = flow<Result<Fact>> {
        while (true) {
            try {
                emit(Result.Loading)
                val latestNews: Fact = catsService.getCatFact()
                Log.d(TAG, "Network response received: ${System.currentTimeMillis()}")
                emit(Result.Success(latestNews))
            } catch (clean: CancellationException) {
                throw clean
            } catch (ex: Exception) {
                Log.d(TAG, "An exception occurred while processing a network request: $ex", ex)
                emit(Result.Error(ex))
            }
            delay(refreshIntervalMs)
        }
    }
}