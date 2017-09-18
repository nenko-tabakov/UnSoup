package online.devtools.unsoup;

import org.junit.Assert;

import org.junit.Test;


import java.io.File;

import java.io.FileWriter;
import java.io.IOException;

import java.util.Collection;

public class BookTest {

    @Test
    public void testBookUnSoup() throws IllegalAccessException, IOException, InstantiationException {
        Book book = new UnSoup().unsoup(TestTools.getTestFile("book.html"), Book.class);

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
    }

    static class Book {

        @ElementMapping(selector = ".book-header h1")
        private String name;

        @ElementMapping(selector = ".book-header .subheading")
        private String description;

        @ElementMapping(selector = ".book-header .isbn")
        private String isbn;

        @ElementMapping(selector = ".table-of-contents-wrapper li")
        private Collection<String> contents;

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getIsbn() {
            return isbn;
        }

        public Collection<String> getContents() {
            return contents;
        }
    }
}