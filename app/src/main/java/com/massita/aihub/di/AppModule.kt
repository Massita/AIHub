package com.massita.aihub.di

import com.massita.aihub.data.repository.ApiKeyRepository
import com.massita.aihub.data.repository.ApiKeyRepositoryImpl
import com.massita.aihub.data.security.EncryptedApiKeyStorage
import com.massita.aihub.data.security.SecureApiKeyStorage
import com.massita.aihub.ui.browser.BrowserViewModel
import com.massita.aihub.ui.chat.ChatViewModel
import com.massita.aihub.ui.settings.ApiKeySettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single<SecureApiKeyStorage> { EncryptedApiKeyStorage(get()) }
    single<ApiKeyRepository> { ApiKeyRepositoryImpl(get()) }
    viewModelOf(::BrowserViewModel)
    viewModelOf(::ChatViewModel)
    viewModelOf(::ApiKeySettingsViewModel)
}
