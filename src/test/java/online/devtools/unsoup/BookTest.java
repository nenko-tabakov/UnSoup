package online.devtools.unsoup;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

public class BookTest {

    @Test
    public void testBookAnnotatedFields() throws IllegalAccessException, IOException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        testBook(AnnotatedFieldsBook.class);
    }

    @Test
    public void testBookAnnotatedMethods() throws IllegalAccessException, IOException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        testBook(AnnotatedMethodsBook.class);
    }

    public void testBook(Class<? extends Book> bookClass) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        Book book = new UnSoup().unsoup(TestTools.getTestFile("book.html"), bookClass);
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

    static class AnnotatedMethodsBook implements Book {

        private String name;

        private String description;

        private String isbn;

        private Collection<String> contents;

        private Integer chapters;

        private int pages;

        @ElementMapping(selector = ".book-header h1")
        public void setName(String name) {
            this.name = name;
        }

        @ElementMapping(selector = ".book-header .subheading")
        public void setDescription(String description) {
            this.description = description;
        }

        @ElementMapping(selector = ".book-header .isbn")
        public void setIsbn(String isbn) {
            this.isbn = isbn;
        }

        @ElementMapping(selector = ".table-of-contents-wrapper li")
        public void setContents(ArrayList<String> contents) {
            this.contents = contents;
        }

        @ElementMapping(selector = ".book-detail.chapters")
        public void setChapters(Integer chapters) {
            this.chapters = chapters;
        }

        @ElementMapping(selector = ".book-detail.pages")
        public void setPages(int pages) {
            this.pages = pages;
        }

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

        public Integer getChapters() {
            return chapters;
        }

        public int getPages() {
            return pages;
        }
    }
}