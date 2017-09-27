package online.devtools.unsoup;

import java.util.ArrayList;
import java.util.Collection;

class AnnotatedFieldsBook implements Book {

    @ElementMapping(selector = ".book-header h1")
    private String name;

    @ElementMapping(selector = ".book-header .subheading")
    private String description;

    @ElementMapping(selector = ".book-header .isbn")
    private String isbn;


    @ElementMapping(selector = ".table-of-contents-wrapper li")
    private ArrayList<String> contents;

    @ElementMapping(selector = ".book-detail.chapters")
    private Integer chapters;

    @ElementMapping(selector = ".book-detail.pages")
    private int pages;

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