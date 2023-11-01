package viewmodel

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import model.Utente
import repository.RetrofitInstance
import retrofit2.create
import service.UtenteService

class UsersViewModel(
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob()),
    private val service: UtenteService = RetrofitInstance.retrofit.create(),
) {
    private val _users = MutableStateFlow(UserState())
    val users = _users.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(10_000),
        initialValue = UserState()
    )

    init {
        coroutineScope.launch {
            delay(800)
            findAll()
        }
    }

    private suspend fun findAll() {
        val data = withContext(Dispatchers.IO) {
            try {
                val response = service.findAll()
                if (response.isSuccessful) response.body()!! else emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
        _users.emit(UserState(false, data))
    }

    fun findByName(nome: String) {
        coroutineScope.launch {
            if (nome.isBlank()) {
                findAll()
            } else {
                val data = try {
                    val response = service.searchByName(nome)
                    if (response.isSuccessful) response.body()!! else emptyList()
                } catch (e: Exception) {
                    e.printStackTrace()
                    emptyList()
                }
                _users.emit(UserState(false, data))
            }
        }
    }

}

data class UserState(
    val isLoading: Boolean = true,
    val data: List<Utente> = emptyList(),
)