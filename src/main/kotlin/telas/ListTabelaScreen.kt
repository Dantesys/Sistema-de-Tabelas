package telas

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

class ListTabelaScreen:Screen {
    override val key: ScreenKey = uniqueScreenKey
    override fun Content() {
        val limit = 12
        val pages = remember { mutableIntStateOf(1) }
        val navigator = LocalNavigator.currentOrThrow

    }
}