package com.massita.aihub.ui.browser

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

data class BrowserTab(
    val id: String = UUID.randomUUID().toString(),
    val url: String = DEFAULT_URL,
    val title: String = "New Tab",
    val isLoading: Boolean = false,
    val loadingProgress: Int = 0,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false
)

data class BrowserUiState(
    val tabs: List<BrowserTab> = listOf(BrowserTab()),
    val activeTabIndex: Int = 0,
    val urlBarText: String = DEFAULT_URL
) {
    val activeTab: BrowserTab? get() = tabs.getOrNull(activeTabIndex)
}

sealed class BrowserEvent {
    data class LoadUrl(val tabId: String, val url: String) : BrowserEvent()
    data class GoBack(val tabId: String) : BrowserEvent()
    data class GoForward(val tabId: String) : BrowserEvent()
    data class Reload(val tabId: String) : BrowserEvent()
}

private const val DEFAULT_URL = "https://www.google.com"

class BrowserViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BrowserUiState())
    val uiState: StateFlow<BrowserUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<BrowserEvent>(extraBufferCapacity = 8)
    val events = _events.asSharedFlow()

    fun onUrlBarTextChanged(text: String) {
        _uiState.update { it.copy(urlBarText = text) }
    }

    fun onNavigateToUrl() {
        val state = _uiState.value
        val tab = state.activeTab ?: return
        val input = state.urlBarText.trim()
        if (input.isBlank()) return

        val url = when {
            input.contains("://") || input.startsWith("about:") -> input
            input.contains(".") && !input.contains(" ") -> "https://$input"
            else -> "https://www.google.com/search?q=${input.replace(" ", "+")}"
        }

        _uiState.update { it.copy(urlBarText = url) }
        _events.tryEmit(BrowserEvent.LoadUrl(tab.id, url))
    }

    fun onGoBack() {
        val tab = _uiState.value.activeTab ?: return
        _events.tryEmit(BrowserEvent.GoBack(tab.id))
    }

    fun onGoForward() {
        val tab = _uiState.value.activeTab ?: return
        _events.tryEmit(BrowserEvent.GoForward(tab.id))
    }

    fun onReload() {
        val tab = _uiState.value.activeTab ?: return
        _events.tryEmit(BrowserEvent.Reload(tab.id))
    }

    fun onAddTab() {
        val newTab = BrowserTab()
        _uiState.update { state ->
            state.copy(
                tabs = state.tabs + newTab,
                activeTabIndex = state.tabs.size,
                urlBarText = newTab.url
            )
        }
    }

    fun onCloseTab(index: Int) {
        _uiState.update { state ->
            if (state.tabs.size <= 1) {
                val freshTab = BrowserTab()
                state.copy(
                    tabs = listOf(freshTab),
                    activeTabIndex = 0,
                    urlBarText = freshTab.url
                )
            } else {
                val newTabs = state.tabs.toMutableList().apply { removeAt(index) }
                val newIndex = when {
                    state.activeTabIndex > index -> state.activeTabIndex - 1
                    state.activeTabIndex >= newTabs.size -> newTabs.lastIndex
                    else -> state.activeTabIndex
                }
                state.copy(
                    tabs = newTabs,
                    activeTabIndex = newIndex,
                    urlBarText = newTabs[newIndex].url
                )
            }
        }
    }

    fun onSelectTab(index: Int) {
        _uiState.update { state ->
            val tab = state.tabs.getOrNull(index) ?: return
            state.copy(
                activeTabIndex = index,
                urlBarText = tab.url
            )
        }
    }

    // WebView callbacks

    fun onPageStarted(tabId: String, url: String) {
        updateTab(tabId) { it.copy(url = url, isLoading = true, loadingProgress = 0) }
        if (_uiState.value.activeTab?.id == tabId) {
            _uiState.update { it.copy(urlBarText = url) }
        }
    }

    fun onPageFinished(tabId: String, url: String) {
        updateTab(tabId) { it.copy(url = url, isLoading = false, loadingProgress = 100) }
    }

    fun onTitleReceived(tabId: String, title: String) {
        if (title.isNotBlank()) {
            updateTab(tabId) { it.copy(title = title) }
        }
    }

    fun onProgressChanged(tabId: String, progress: Int) {
        updateTab(tabId) { it.copy(loadingProgress = progress) }
    }

    fun onNavigationStateChanged(tabId: String, canGoBack: Boolean, canGoForward: Boolean) {
        updateTab(tabId) { it.copy(canGoBack = canGoBack, canGoForward = canGoForward) }
    }

    private fun updateTab(tabId: String, transform: (BrowserTab) -> BrowserTab) {
        _uiState.update { state ->
            state.copy(tabs = state.tabs.map { if (it.id == tabId) transform(it) else it })
        }
    }
}
