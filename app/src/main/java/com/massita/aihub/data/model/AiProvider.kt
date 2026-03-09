package com.massita.aihub.data.model

enum class AiProvider(
    val displayName: String,
    val keyHint: String,
    val prefKey: String
) {
    OPENAI("OpenAI", "sk-proj-...", "api_key_openai"),
    ALIBABA("Alibaba (Qwen)", "sk-...", "api_key_alibaba"),
    GEMINI("Google Gemini", "AIza...", "api_key_gemini");
}
