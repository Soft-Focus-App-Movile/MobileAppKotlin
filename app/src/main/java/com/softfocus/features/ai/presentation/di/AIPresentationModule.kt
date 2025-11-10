package com.softfocus.features.ai.presentation.di

import android.content.Context
import com.softfocus.features.ai.data.di.AIDataModule
import com.softfocus.features.ai.presentation.chat.AIChatViewModel
import com.softfocus.features.ai.presentation.welcome.AIWelcomeViewModel

object AIPresentationModule {

    fun getAIChatViewModel(context: Context): AIChatViewModel {
        return AIChatViewModel(
            repository = AIDataModule.getAIChatRepository(context)
        )
    }

    fun getAIWelcomeViewModel(context: Context): AIWelcomeViewModel {
        return AIWelcomeViewModel(
            repository = AIDataModule.getAIChatRepository(context)
        )
    }
}
