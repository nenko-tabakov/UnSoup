package online.devtools.unsoup;


import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;


public class BrandsTest {

    static class Brand {
        @AttributeMapping(selector = ".brands a", name = "href")
        public String name;

        @ElementMapping(selector = ".brands a")
        public String detailsLocation;
    }

    static class Brands {

        @ElementMapping(selector = ".brands a")
        public Collection<Brand> brands;
    }

    @Test
    public void testBrands() throws IllegalAccessException, IOException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        final Brands brands = new UnSoup().unsoup(TestTools.getTestFile("brands.html"), Brands.class);

        Assert.assertNotNull(brands);
    }

} 