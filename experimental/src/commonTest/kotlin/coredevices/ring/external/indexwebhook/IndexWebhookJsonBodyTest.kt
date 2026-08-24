package coredevices.ring.external.indexwebhook

import kotlin.test.Test
import kotlin.test.assertEquals

class IndexWebhookJsonBodyTest {

    private fun render(
        template: String,
        transcription: String? = "hello",
        recordedAt: Long = 1700000000000,
        trigger: String = "single-click-hold",
        isTest: Boolean = false,
    ) = renderWebhookJsonBody(template, transcription, recordedAt, trigger, isTest)

    @Test
    fun substitutesTheTranscription() {
        assertEquals(
            """{"content":"hello"}""",
            render("""{"content":"{{transcription}}"}"""),
        )
    }

    @Test
    fun escapesQuotesAndNewlinesInTheTranscription() {
        assertEquals(
            """{"content":"she said \"hi\"\nthen left"}""",
            render("""{"content":"{{transcription}}"}""", transcription = "she said \"hi\"\nthen left"),
        )
    }

    @Test
    fun escapesBackslashes() {
        assertEquals(
            """{"content":"C:\\temp"}""",
            render("""{"content":"{{transcription}}"}""", transcription = """C:\temp"""),
        )
    }

    @Test
    fun aMissingTranscriptionBecomesEmpty() {
        assertEquals(
            """{"content":""}""",
            render("""{"content":"{{transcription}}"}""", transcription = null),
        )
    }

    @Test
    fun substitutesMetadataPlaceholders() {
        assertEquals(
            """{"at":1700000000000,"by":"single-click-hold","from":"ring","test":false}""",
            render("""{"at":{{recordedAt}},"by":"{{trigger}}","from":"{{client}}","test":{{test}}}"""),
        )
    }

    @Test
    fun marksTestEvents() {
        assertEquals("""{"test":true}""", render("""{"test":{{test}}}""", isTest = true))
    }

    @Test
    fun repeatedPlaceholdersAreAllReplaced() {
        assertEquals(
            """{"a":"hello","b":"hello"}""",
            render("""{"a":"{{transcription}}","b":"{{transcription}}"}"""),
        )
    }

    @Test
    fun unknownPlaceholdersAreLeftAlone() {
        assertEquals("""{"x":"{{nope}}"}""", render("""{"x":"{{nope}}"}"""))
    }

    @Test
    fun aTemplateWithoutPlaceholdersPassesThrough() {
        assertEquals("""{"fixed":true}""", render("""{"fixed":true}"""))
    }

    @Test
    fun aTranscriptionContainingAPlaceholderIsNotRewritten() {
        assertEquals(
            """{"content":"say {{client}} out loud"}""",
            render("""{"content":"{{transcription}}"}""", transcription = "say {{client}} out loud"),
        )
    }
}
