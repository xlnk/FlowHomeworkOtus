package otus.homework.flowcats

import android.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.net.SocketTimeoutException

private const val TAG = "CatsRepository"

class CatsRepository(
    private val catsService: CatsService,
    private val refreshIntervalMs: Long = 30000
) {

    fun listenForCatFacts(): Flow<Result<Fact>> = flow<Result<Fact>> {
        while (true) {
            emit(Result.Loading)
            try {
                if (true) throw RuntimeException("Test")
                val fact = loadCatFact()
                Log.d(TAG, "Network response received: ${System.currentTimeMillis()}")
                emit(Result.Success(fact))
            } catch (clean: CancellationException) {
                throw clean
            } catch (ex: Exception) {
                Log.d(TAG, "An exception occurred while processing a network request: $ex", ex)
                emit(Result.Error(ex))
            }
            delay(refreshIntervalMs)
        }
    }

    private suspend fun loadCatFact(): Fact {
        return try {
            catsService.getCatFact()
        } catch (ex: Exception) {
            if (ex !is SocketTimeoutException) {
                throw ex
            }
            Log.d(TAG, "Main server isn't response, try other server. Message: " + ex.message, ex)
            catsService.getCatFactReserve().toFact()
        }
    }
}