package online.devtools.unsoup;

import java.io.File;

final class TestTools {

    static final String TEST_RESOURCES_LOCATION = "build/resources/test";

    static File getTestFile(final String filename) {
        return new File(TEST_RESOURCES_LOCATION, filename);
    }
}
