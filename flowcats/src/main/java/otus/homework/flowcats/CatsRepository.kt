package otus.homework.flowcats

import android.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.net.SocketTimeoutException
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
                Log.d(TAG, "Network response received: ${timeFormat.format(System.currentTimeMillis())}")
                emit(Result.Success(fact))
            } catch (cancel: CancellationException) {
                Log.d(TAG, "listenForCatFacts flow was cancelled")
                throw cancel
//            } catch (ex: Exception) { // насколько я поняла документацию try/catch внутри Flow нарушает Exception transparency, хотя и работает. Но очень не уверена что правильно её поняла
//                Log.d(TAG, "An exception occurred while processing a network request: $ex", ex)
//                emit(Result.Error(ex))
            }
            delay(refreshIntervalMs)
        }
    }.catch { emit(Result.Error(it)) }

    /** вынести в отдельную функцию чтобы не "нарушать" exception transparency */
    private suspend fun loadCatFact(): Fact {
        return try {
            catsService.getCatFact()
        } catch (ex: SocketTimeoutException) {
            Log.d(TAG, "Main server isn't response, try other server. Error: $ex")
            catsService.getCatFactReserve().toFact()
        }
    }
}