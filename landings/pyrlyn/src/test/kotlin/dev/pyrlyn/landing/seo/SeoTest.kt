package dev.pyrlyn.landing.seo

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SeoTest {
    @Test
    fun plainDropsMarkdownNoise() {
        assertEquals("Use rtok & ketch <now>", Seo.plain("Use [`rtok`](x) &amp; **ketch** &lt;now&gt; <!-- c -->"))
    }

    @Test
    fun clipPrefersASentenceEndPastHalfway() {
        val text = "First sentence is here and fairly long. Second one goes on and on and on."
        assertEquals("First sentence is here and fairly long.", Seo.clip(text, 50))
        assertEquals("short", Seo.clip("  short  ", 50))
    }

    @Test
    fun clipAndWordClipStayWithinTheLimit() {
        val text = "word ".repeat(80)
        listOf(20, 60, 155, 160).forEach { max ->
            assertTrue(Seo.clip(text, max).length <= max)
            assertTrue(Seo.wordClip(text, max).length <= max)
            assertTrue(Seo.wordClip(text, max).endsWith("…"))
        }
    }

    @Test
    fun firstSentence() {
        assertEquals("One.", Seo.firstSentence("One. Two."))
        assertEquals("", Seo.firstSentence("no end"))
    }

    @Test
    fun documentationTitleFitsSixtyCharacters() {
        val long = "A very long documentation page title that keeps going and going"
        val t = Seo.documentationTitle(long, "Rtok", "ru/guide.md")
        assertTrue(t.length <= 60, t)
        assertTrue(t.endsWith(" · Rtok docs (ru)"), t)
        assertEquals("Intro · Rtok docs | Pyrlyn", Seo.documentationTitle("Intro", "Rtok", null))
    }

    private fun project(language: String?): dev.pyrlyn.landing.content.Project {
        val dir = java.io.File("build/tmp/seo-test").apply { mkdirs() }
        val file = java.io.File(dir, "demo.md")
        file.writeText(
            "---\ntitle: demo\ntagline: A demo.\nrepo: https://github.com/pyrlyn/demo\n" +
                "install: demo\nversion: \"1.0.0\"\naccent: \"#123456\"\n" +
                (language?.let { "language: $it\n" } ?: "") + "---\n\n## Overview\n",
        )
        return dev.pyrlyn.landing.content.Project.load(file)
    }

    private val seo = Seo(dev.pyrlyn.landing.config.SiteConfig.fromEnv { null })

    @Test
    fun sourceCodeNamesTheLanguageFromTheFrontMatter() {
        val node = seo.sourceCode(project("Kotlin"))
        assertEquals("\"Kotlin\"", node["programmingLanguage"].toString())
        assertEquals("\"https://github.com/pyrlyn/demo\"", node["codeRepository"].toString())
    }

    @Test
    fun sourceCodeLeavesTheLanguageOutWhenUnknown() {
        assertFalse("programmingLanguage" in seo.sourceCode(project(null)))
    }

    @Test
    fun graphEscapesScriptBreakouts() {
        val g = Seo(dev.pyrlyn.landing.config.SiteConfig.fromEnv { null })
            .graph(listOf(kotlinx.serialization.json.JsonPrimitive("</script><script>x")))
        assertFalse(g.contains("</script>"))
        assertTrue(g.contains("\\u003c/script>"))
    }
}
