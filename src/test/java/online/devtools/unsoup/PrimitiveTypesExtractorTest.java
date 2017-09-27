package online.devtools.unsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class PrimitiveTypesExtractorTest {

    @Test
    public void testExtractElement() throws IOException {
        Document document = Jsoup.parse(TestTools.getTestFile("book.html"), "UTF-8");

        Assert.assertEquals("Book Title", PrimitiveTypesExtractor.fromElement(getElement(document, ".book-header h1"), String.class));
        Assert.assertEquals(Integer.valueOf(6), PrimitiveTypesExtractor.fromElement(getElement(document, ".book-detail.chapters"), Integer.class));
    }

    @Test
    public void testExtractAttribute() throws IOException {
        Document document = Jsoup.parse(TestTools.getTestFile("brands.html"), "UTF-8");
        Assert.assertEquals("model/1", PrimitiveTypesExtractor.fromAttribute(getElement(document, ".brands a"), String.class, "href"));
    }

    @Test
    public void testExtractElementNullArguments() {
        Assert.assertNull(PrimitiveTypesExtractor.fromElement(null, null));
    }

    @Test
    public void testExtractAttributesNullArguments() {
        Assert.assertNull(PrimitiveTypesExtractor.fromAttribute(null, null, null));
    }

    @Test
    public void testNotSupportedClass() {
        Assert.assertNull(PrimitiveTypesExtractor.fromAttribute(null, NotExisting.class, null));
        Assert.assertNull(PrimitiveTypesExtractor.fromElement(null, NotExisting.class));
    }

    @Test
    public void testIsSupportedType() {
        Assert.assertTrue(PrimitiveTypesExtractor.isSupportedType(String.class));
        Assert.assertFalse(PrimitiveTypesExtractor.isSupportedType(NotExisting.class));
    }

    private Element getElement(Document document, String selector) {
        return document.select(selector).first();
    }

    private static class NotExisting {
    }
}