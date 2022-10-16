package otus.homework.flowcats

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
//import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TAG = "MainActivityTag"

class MainActivity : AppCompatActivity() {

    private val diContainer = DiContainer()
    private val catsViewModel by viewModels<CatsViewModel> { CatsViewModelFactory(diContainer.repository) }

    private var uiJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = layoutInflater.inflate(R.layout.activity_main, null) as CatsView
        setContentView(view)

//        catsViewModel.catsLiveData.observe(this){
//            when(it) {
//                is Result.Success -> view.populate(it.data)
//                is Result.Loading -> Unit
//                is Result.Error -> Toast.makeText(this, it.getMessage(this), Toast.LENGTH_LONG).show()
//            }
//        }

        uiJob = lifecycleScope.launchWhenStarted {
            catsViewModel.catsFlow.collect {
                when(it) {
                    is Result.Success -> view.populate(it.data)
                    is Result.Loading -> Unit
                    is Result.Error -> Toast.makeText(
                        this@MainActivity,
                        it.getMessage(this@MainActivity),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun stopUiJob() {
        Log.d(TAG, "attempt to stop ui")
        uiJob?.cancel()
        uiJob = null
    }

    override fun onStop() {
        super.onStop()
        stopUiJob()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopUiJob()
    }
}