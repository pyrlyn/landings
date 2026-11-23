package dev.pyrlyn.landing.markdown

import kotlin.test.Test
import kotlin.test.assertEquals

class SafeHtmlTest {
    @Test
    fun keepsAllowListedTagsWithoutAttributes() {
        assertEquals("<kbd>Ctrl</kbd><br/><details open><summary>s</summary></details>",
            SafeHtml.clean("<KBD>Ctrl</KBD><br/><details open><summary>s</summary></details>"))
    }

    @Test
    fun escapesUnknownTagsAndAllowListedTagsWithAttributes() {
        assertEquals("&lt;script&gt;x&lt;/script&gt;", SafeHtml.clean("<script>x</script>"))
        assertEquals("&lt;b onclick=&quot;x()&quot;&gt;", SafeHtml.clean("<b onclick=\"x()\">"))
        assertEquals("&lt;img src=x onerror=alert(1)&gt;", SafeHtml.clean("<img src=x onerror=alert(1)>"))
    }

    @Test
    fun dropsCommentsIncludingAnUnterminatedOne() {
        assertEquals("ab", SafeHtml.clean("a<!-- hidden -->b"))
        assertEquals("a", SafeHtml.clean("a<!-- never closed <script>"))
    }

    @Test
    fun turnsNamedAnchorsIntoSpanTargets() {
        assertEquals("<span id=\"top\"></span>", SafeHtml.clean("<a name=\"top\"></a>"))
        assertEquals("", SafeHtml.clean("</a>"))
    }

    @Test
    fun escapesStrayAngleBrackets() {
        assertEquals("1 &lt; 2 &gt; 0", SafeHtml.clean("1 < 2 > 0"))
    }

    @Test
    fun hrefKeepsWebMailAndRelativeTargets() {
        listOf("https://pyrlyn.dev", "HTTP://x", "mailto:a@b.c", "./guide.md", "#install", "/docs/")
            .forEach { assertEquals(it, SafeHtml.href(it)) }
    }

    @Test
    fun hrefDropsScriptCapableSchemesEvenWhenObfuscated() {
        listOf("javascript:alert(1)", "JaVaScRiPt:alert(1)", " javascript:alert(1)", "java\tscript:alert(1)",
            "java\u0000script:x", "vbscript:x", "data:text/html,<script>x</script>", "file:///etc/passwd")
            .forEach { assertEquals("#", SafeHtml.href(it), it) }
    }
}
