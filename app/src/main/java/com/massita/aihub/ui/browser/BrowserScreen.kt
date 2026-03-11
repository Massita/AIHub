package com.massita.aihub.ui.browser

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun BrowserScreen(
    viewModel: BrowserViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val webViews = remember { mutableMapOf<String, WebView>() }

    val currentTabIds = uiState.tabs.map { it.id }.toSet()
    LaunchedEffect(currentTabIds) {
        val stale = webViews.keys - currentTabIds
        stale.forEach { id -> webViews.remove(id)?.destroy() }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            val (tabId, webView) = when (event) {
                is BrowserEvent.LoadUrl -> event.tabId to webViews[event.tabId]
                is BrowserEvent.GoBack -> event.tabId to webViews[event.tabId]
                is BrowserEvent.GoForward -> event.tabId to webViews[event.tabId]
                is BrowserEvent.Reload -> event.tabId to webViews[event.tabId]
            }
            webView ?: return@collect
            when (event) {
                is BrowserEvent.LoadUrl -> webView.loadUrl(event.url)
                is BrowserEvent.GoBack -> webView.goBack()
                is BrowserEvent.GoForward -> webView.goForward()
                is BrowserEvent.Reload -> webView.reload()
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            webViews.values.forEach { it.destroy() }
            webViews.clear()
        }
    }

    val activeTab = uiState.activeTab

    Column(modifier = modifier.fillMaxSize()) {
        BrowserTabStrip(
            tabs = uiState.tabs,
            activeTabIndex = uiState.activeTabIndex,
            onSelectTab = viewModel::onSelectTab,
            onCloseTab = viewModel::onCloseTab,
            onAddTab = viewModel::onAddTab
        )

        BrowserUrlBar(
            urlText = uiState.urlBarText,
            canGoBack = activeTab?.canGoBack == true,
            canGoForward = activeTab?.canGoForward == true,
            isLoading = activeTab?.isLoading == true,
            onUrlChanged = viewModel::onUrlBarTextChanged,
            onNavigate = viewModel::onNavigateToUrl,
            onGoBack = viewModel::onGoBack,
            onGoForward = viewModel::onGoForward,
            onReload = viewModel::onReload
        )

        AnimatedVisibility(
            visible = activeTab?.isLoading == true,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            LinearProgressIndicator(
                progress = { (activeTab?.loadingProgress ?: 0) / 100f },
                modifier = Modifier.fillMaxWidth().height(3.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }

        if (activeTab != null) {
            AndroidView(
                factory = { FrameLayout(it) },
                update = { container ->
                    val webView = webViews.getOrPut(activeTab.id) {
                        createWebView(context, activeTab.id, viewModel).also { wv ->
                            wv.loadUrl(activeTab.url)
                        }
                    }
                    if (webView.parent !== container) {
                        container.removeAllViews()
                        (webView.parent as? ViewGroup)?.removeView(webView)
                        container.addView(
                            webView,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                },
                modifier = Modifier.weight(1f).fillMaxWidth()
            )
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
private fun createWebView(
    context: android.content.Context,
    tabId: String,
    viewModel: BrowserViewModel
): WebView {
    return WebView(context).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.setSupportZoom(true)
        settings.builtInZoomControls = true
        settings.displayZoomControls = false
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true

        webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                viewModel.onPageStarted(tabId, url)
            }

            override fun onPageFinished(view: WebView, url: String) {
                viewModel.onPageFinished(tabId, url)
                viewModel.onNavigationStateChanged(tabId, view.canGoBack(), view.canGoForward())
            }

            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean = false
        }

        webChromeClient = object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView, title: String?) {
                title?.let { viewModel.onTitleReceived(tabId, it) }
            }

            override fun onProgressChanged(view: WebView, newProgress: Int) {
                viewModel.onProgressChanged(tabId, newProgress)
            }
        }
    }
}

// region Tab Strip

@Composable
private fun BrowserTabStrip(
    tabs: List<BrowserTab>,
    activeTabIndex: Int,
    onSelectTab: (Int) -> Unit,
    onCloseTab: (Int) -> Unit,
    onAddTab: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(start = 8.dp, end = 4.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEachIndexed { index, tab ->
                BrowserTabItem(
                    tab = tab,
                    isActive = index == activeTabIndex,
                    onClick = { onSelectTab(index) },
                    onClose = { onCloseTab(index) }
                )
                Spacer(modifier = Modifier.width(2.dp))
            }

            IconButton(
                onClick = onAddTab,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "New tab",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun BrowserTabItem(
    tab: BrowserTab,
    isActive: Boolean,
    onClick: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isActive) MaterialTheme.colorScheme.surface
        else MaterialTheme.colorScheme.surfaceContainerHigh,
        label = "tab_bg"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isActive) MaterialTheme.colorScheme.onSurface
        else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "tab_content"
    )

    Surface(
        onClick = onClick,
        modifier = modifier
            .height(36.dp)
            .widthIn(min = 100.dp, max = 200.dp),
        shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
        color = backgroundColor,
        tonalElevation = if (isActive) 1.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = tab.title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelSmall,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            IconButton(
                onClick = onClose,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Close tab",
                    modifier = Modifier.size(14.dp),
                    tint = contentColor.copy(alpha = 0.6f)
                )
            }
        }
    }
}

// endregion

// region URL Bar

@Composable
private fun BrowserUrlBar(
    urlText: String,
    canGoBack: Boolean,
    canGoForward: Boolean,
    isLoading: Boolean,
    onUrlChanged: (String) -> Unit,
    onNavigate: () -> Unit,
    onGoBack: () -> Unit,
    onGoForward: () -> Unit,
    onReload: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            IconButton(
                onClick = onGoBack,
                enabled = canGoBack,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back",
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = onGoForward,
                enabled = canGoForward,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Go forward",
                    modifier = Modifier.size(20.dp)
                )
            }

            OutlinedTextField(
                value = urlText,
                onValueChange = onUrlChanged,
                modifier = Modifier.weight(1f),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodySmall,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                keyboardActions = KeyboardActions(onGo = {
                    focusManager.clearFocus()
                    onNavigate()
                })
            )

            IconButton(
                onClick = {
                    if (isLoading) { /* stop not supported by basic WebView */ }
                    else onReload()
                },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    if (isLoading) Icons.Default.Close else Icons.Default.Refresh,
                    contentDescription = if (isLoading) "Stop" else "Reload",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// endregion
