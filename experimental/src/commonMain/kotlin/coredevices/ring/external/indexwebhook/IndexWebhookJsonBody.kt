package coredevices.ring.external.indexwebhook

/**
 * Fills a user-supplied JSON body template. Text is escaped as JSON string content, so a
 * transcription containing quotes or newlines is safe inside a quoted placeholder. A placeholder
 * used anywhere else is the template author's business — the rendered body is validated before
 * it is sent.
 */
fun renderWebhookJsonBody(
    template: String,
    transcription: String?,
    recordedAt: Long,
    trigger: String,
    isTest: Boolean,
): String {
    val values = mapOf(
        "transcription" to jsonEscape(transcription.orEmpty()),
        "recordedAt" to recordedAt.toString(),
        "trigger" to jsonEscape(trigger),
        "client" to WEBHOOK_CLIENT,
        "test" to isTest.toString(),
    )
    // One pass, so a value that happens to contain a placeholder is never substituted again.
    return PLACEHOLDER.replace(template) { match -> values.getValue(match.groupValues[1]) }
}

private val PLACEHOLDER = Regex("""\{\{(transcription|recordedAt|trigger|client|test)}}""")

private fun jsonEscape(value: String): String = buildString(value.length) {
    for (c in value) {
        when (c) {
            '"' -> append("\\\"")
            '\\' -> append("\\\\")
            '\n' -> append("\\n")
            '\r' -> append("\\r")
            '\t' -> append("\\t")
            '\b' -> append("\\b")
            '\u000C' -> append("\\f")
            else -> if (c < ' ') append("\\u").append(c.code.toString(16).padStart(4, '0')) else append(c)
        }
    }
}
