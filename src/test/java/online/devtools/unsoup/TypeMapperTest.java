package online.devtools.unsoup;

import org.jsoup.Jsoup;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public class TypeMapperTest {

    @Test
    public void testMap() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException {
        AnnotatedFieldsBook book = new TypeMapper(Jsoup.parse(TestTools.getTestFile("book.html"), "UTF-8")).map(AnnotatedFieldsBook.class);

        Assert.assertEquals("Book Title", book.getName());
        Assert.assertEquals("Book Description", book.getDescription());
        Assert.assertEquals("ISBN0-7645-2641-3", book.getIsbn());

        Collection<String> contents = book.getContents();
        Assert.assertNotNull(contents);

        final int expectedSize = 6;
        Assert.assertEquals(expectedSize, contents.size());
        for (int i = 1; i < expectedSize; i++) {
            Assert.assertTrue(contents.contains("Chapter " + i));
        }

        Assert.assertEquals(expectedSize, book.getChapters().intValue());
        Assert.assertEquals(364, book.getPages());
    }
}