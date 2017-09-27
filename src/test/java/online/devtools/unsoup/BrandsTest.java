package online.devtools.unsoup;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;


public class BrandsTest {

    static class Brand {

        @AttributeMapping(selector = ".brands a", name = "href")
        public String href;

        @AttributeMapping(selector = ".brands a", name = "title")
        public String title;

        @ElementMapping(selector = ".brands a")
        public String detailedTitle;

        public String methodInjectedTitle;

        public String methodInjectedDetailedTitle;

        public String methodInjectedHref;

        @AttributeMapping(selector = ".brands a", name = "href")
        public void setHref(String href) {
            this.methodInjectedHref = href;
        }

        @AttributeMapping(selector = ".brands a", name = "title")
        public void setTitle(String title) {
            this.methodInjectedTitle = title;
        }

        @ElementMapping(selector = ".brands a")
        public void setDetailedTitle(String detailedTitle) {
            this.methodInjectedDetailedTitle = detailedTitle;
        }
    }

    static class Brands {

        @ElementMapping(selector = ".brands a", type = Brand.class)
        public ArrayList<Brand> brands;
    }

    @Test
    public void testBrands() throws IllegalAccessException, IOException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        final Brands brands = new UnSoup().unsoup(TestTools.getTestFile("brands.html"), Brands.class);
        Assert.assertNotNull(brands);
        Assert.assertNotNull(brands.brands);
        Assert.assertEquals(5, brands.brands.size());

        final String[] expectedTitles = {"Abarth", "Acura", "Alfa Romeo", "Aston Martin", "Audi"};
        for (int i = 0; i < brands.brands.size(); i++) {
            final Brand brand = brands.brands.get(i);
            final String expectedTitle = expectedTitles[i];
            Assert.assertEquals(expectedTitle, brand.detailedTitle);
            Assert.assertEquals(expectedTitle, brand.title);
            Assert.assertEquals(expectedTitle, brand.methodInjectedDetailedTitle);
            Assert.assertEquals(expectedTitle, brand.methodInjectedTitle);

            final String expectedHref = "model/" + (i + 1);
            Assert.assertEquals(expectedHref, brand.href);
            Assert.assertEquals(expectedHref, brand.methodInjectedHref);
        }
    }
}