package online.devtools.unsoup;

import java.util.Collection;

interface Book {
    String getName();

    String getDescription();

    String getIsbn();

    Collection<String> getContents();

    Integer getChapters();

    int getPages();
}