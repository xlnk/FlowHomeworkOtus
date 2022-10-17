package otus.homework.flowcats

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
//import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TAG = "MainActivityTag"

class MainActivity : AppCompatActivity() {

    private val diContainer = DiContainer()
    private val catsViewModel by viewModels<CatsViewModel> { CatsViewModelFactory(diContainer.repository) }

    private lateinit var loadingIndicator: ProgressBar
    private lateinit var catsView: CatsView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        catsView = layoutInflater.inflate(R.layout.activity_main, null) as CatsView
        setContentView(catsView)
        loadingIndicator = findViewById(R.id.loading_indicator)

//        catsViewModel.catsLiveData.observe(this) { updateUi(it) } // LiveData variant

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                try {
                    // cancellable stateIn() variant
                    catsViewModel.subscribeCatsFlow(this).collect { updateUi(it) }
                } catch (cancel: CancellationException) {
                    Log.d(TAG, "subscribeCatsFlow was cancelled")
                    throw cancel
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                try {
                    // simple MutableStateFlow.collect() variant
                    catsViewModel.catsFlow.collect { updateUi(it) }
                } catch (cancel: CancellationException) {
                    Log.d(TAG, "Simple MutableStateFlow.collect() was cancelled")
                    throw cancel
                }
            }
        }
    }

    private fun updateUi(fact: Result<Fact>) {
        loadingIndicator.visibility = if (fact is Result.Loading) View.VISIBLE else View.GONE
        when(fact) {
            is Result.Success -> catsView.populate(fact.data)
            is Result.Loading -> Unit
            is Result.Error -> Toast.makeText(
                this,
                fact.getMessage(this),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }
}