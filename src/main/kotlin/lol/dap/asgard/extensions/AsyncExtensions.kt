package extensions

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

suspend fun async(context: CoroutineContext = Dispatchers.IO, block: suspend () -> Unit) =
    withContext(context) {
        block()
    }