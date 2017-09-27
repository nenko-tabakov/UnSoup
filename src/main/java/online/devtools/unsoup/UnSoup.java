package online.devtools.unsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class UnSoup {

    public static final String TIMEOUT = "timeout";

    public static final String ENCODING = "encoding";

    private final Map<String, String> configuration;

    public UnSoup() {
        this(new HashMap<>());
    }

    public UnSoup(Map<String, String> configuration) {
        if (configuration == null) {
            this.configuration = new HashMap<>();
        } else {
            this.configuration = configuration;
        }
    }

    public <T> T unsoup(File file, Class<T> model) throws IOException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        final String encoding = configuration.getOrDefault(ENCODING, "UTF-8");
        return unsoup(Jsoup.parse(file, encoding), model);
    }

    public <T> T unsoup(URL url, Class<T> model) throws IOException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        final String timeout = configuration.getOrDefault(TIMEOUT, "120000");
        return unsoup(Jsoup.parse(url, Integer.parseInt(timeout)), model);
    }

    public <T> T unsoup(String url, Class<T> model) throws IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        return unsoup(Jsoup.connect(url).get(), model);
    }

    private <T> T unsoup(Document document, Class<T> model) throws IOException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        return new TypeMapper(document).map(model);
    }
}