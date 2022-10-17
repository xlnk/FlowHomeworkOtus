package otus.homework.flowcats

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import otus.homework.flowcats.Result

private const val TAG = "CatsViewModel"

class CatsViewModel(
    private val catsRepository: CatsRepository
) : ViewModel() {

    private val selfUpdatingCatsFactFlow by lazy { catsRepository.listenForCatFacts().flowOn(Dispatchers.IO) }

    private val _catsLiveData = MutableLiveData<Result<Fact>>()
    val catsLiveData: LiveData<Result<Fact>> = _catsLiveData

    private val _catsFlow = MutableStateFlow<Result<Fact>>(Result.Loading)
    val catsFlow: StateFlow<Result<Fact>> = _catsFlow.asStateFlow()

    fun subscribeCatsFlow(scope: CoroutineScope): StateFlow<Result<Fact>> = selfUpdatingCatsFactFlow
        .stateIn(scope, SharingStarted.WhileSubscribed(), Result.Loading)

    init {
//        // uncomment for MutableStateFlow variant
//        viewModelScope.launch {
//            selfUpdatingCatsFactFlow.collect {
//                _catsLiveData.value = it
//                _catsFlow.value = it
//            }
//        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared")
    }
}

class CatsViewModelFactory(private val catsRepository: CatsRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(CatsViewModel::class.java)) {
            throw IllegalArgumentException("Unsupported ViewModel class: " + modelClass.name)
        }
        @Suppress("UNCHECKED_CAST")
        return CatsViewModel(catsRepository) as T
    }

}