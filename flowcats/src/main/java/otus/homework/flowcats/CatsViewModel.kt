package otus.homework.flowcats

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import otus.homework.flowcats.Result

class CatsViewModel(
    private val catsRepository: CatsRepository
) : ViewModel() {

    private val _catsLiveData = MutableLiveData<Result<Fact>>()
    val catsLiveData: LiveData<Result<Fact>> = _catsLiveData

    init {
        viewModelScope.launch {
//            withContext(Dispatchers.IO) {
                catsRepository.listenForCatFacts().collect {
                    _catsLiveData.value = it
                }
//            }
        }
    }
}

class CatsViewModelFactory(private val catsRepository: CatsRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        CatsViewModel(catsRepository) as T
}