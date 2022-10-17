package otus.homework.flowcats

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import otus.homework.flowcats.Result

class CatsViewModel(
    private val catsRepository: CatsRepository
) : ViewModel() {

    private val _catsLiveData = MutableLiveData<Result<Fact>>()
    val catsLiveData: LiveData<Result<Fact>> = _catsLiveData

    lateinit var catsFlow: StateFlow<Result<Fact>>

    init {
        viewModelScope.launch {
            catsFlow = catsRepository.listenForCatFacts().flowOn(Dispatchers.IO).onEach {
                _catsLiveData.value = it
            }.stateIn(viewModelScope, SharingStarted.Eagerly, Result.Loading)
        }
    }
}

class CatsViewModelFactory(private val catsRepository: CatsRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        CatsViewModel(catsRepository) as T
}