package otus.homework.flowcats

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels

class MainActivity : AppCompatActivity() {

    private val diContainer = DiContainer()
    private val catsViewModel by viewModels<CatsViewModel> { CatsViewModelFactory(diContainer.repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = layoutInflater.inflate(R.layout.activity_main, null) as CatsView
        setContentView(view)

        catsViewModel.catsLiveData.observe(this){
            when(it) {
                is Result.Success -> view.populate(it.data)
                is Result.Loading -> Unit
                is Result.Error -> Toast.makeText(this, it.getMessage(this), Toast.LENGTH_LONG).show()
            }

        }
    }
}