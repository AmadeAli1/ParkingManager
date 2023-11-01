package nav

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import okhttp3.Route
import screen.Screen
import kotlin.coroutines.EmptyCoroutineContext

object Navigation {
    private val _screen = MutableStateFlow<Screen>(Screen.Panel)

    val screen = _screen.stateIn(
        scope = CoroutineScope(EmptyCoroutineContext),
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = Screen.Panel
    )

    fun navigate(screen: Screen) {
        _screen.tryEmit(screen)
    }


}