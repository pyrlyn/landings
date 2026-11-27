package dev.pyrlyn.landing.markdown

import kotlin.test.Test
import kotlin.test.assertEquals

class EscTest {
    @Test
    fun textEscapesMarkupAndQuotes() {
        assertEquals("&lt;a href=&quot;x&quot;&gt;it&#39;s&lt;/a&gt;", Esc.text("<a href=\"x\">it's</a>"))
    }

    @Test
    fun textKeepsExistingEntitiesButEscapesBareAmpersands() {
        assertEquals("&amp; &copy; &#169; &#xA9; &amp;nope", Esc.text("& &copy; &#169; &#xA9; &nope"))
    }

    @Test
    fun attrEscapesEveryAmpersandAndTheFourCharacters() {
        assertEquals("a&amp;amp;b &lt;&gt;&quot;'", Esc.attr("a&amp;b <>\"'"))
    }
}
