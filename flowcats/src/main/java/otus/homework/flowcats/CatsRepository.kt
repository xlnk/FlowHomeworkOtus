package otus.homework.flowcats

import android.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retryWhen
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.DateFormat

private const val TAG = "CatsRepository"

class CatsRepository(
    private val catsService: CatsService,
    private val refreshIntervalMs: Long = 10000
) {
    private val timeFormat = DateFormat.getTimeInstance()

    fun listenForCatFacts(): Flow<Result<Fact>> = flow<Result<Fact>> {
        while (true) {
            emit(Result.Loading)

            try {
                val fact = loadCatFact()
                Log.d(
                    TAG,
                    "Network response received: ${timeFormat.format(System.currentTimeMillis())}"
                )
                emit(Result.Success(fact))
            } catch (cancel: CancellationException) {
                Log.d(TAG, "listenForCatFacts flow was cancelled")
                throw cancel
            }
            delay(refreshIntervalMs)
        }
    }.retryWhen { cause, attempt ->
        val needRetry: Boolean = when (cause) {
            is SocketTimeoutException,
            is UnknownHostException,
            is IOException -> attempt <= 10
            else -> false
        }
        Log.d(TAG, "RetryWhen operator was triggered. Need retry: $needRetry, exception: $cause")
        if (needRetry) {
            emit(Result.Error(cause))
            val retryTimeout = refreshIntervalMs * (attempt + 1)
            Log.d(TAG, "Network exception. Retry after: $retryTimeout ms")
            delay(retryTimeout)
        }
        needRetry
    }.catch {
        Log.d(TAG, "Flow catch operator was triggered. Exception: $it")
        emit(Result.Error(it))
    }

    private suspend fun loadCatFact(): Fact {
        return try {
            catsService.getCatFact()
        } catch (ex: SocketTimeoutException) {
            Log.d(TAG, "Main server isn't response, try other server. Error: $ex")
            catsService.getCatFactReserve().toFact()
        }
    }
}