package online.devtools.unsoup;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class UnSoup {

    public static final String TIMEOUT = "timeout";
    public static final String ENCODING = "UTF-8";
    public static final Class<AttributeMapping> ATTRIBUTE_MAPPING_ANNOTATION = AttributeMapping.class;
    private static final Class<ElementMapping> ELEMENT_MAPPING_ANNOTATION = ElementMapping.class;
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
        T modelInstance = model.newInstance();
        handleAnnotatedFields(modelInstance, document);
        handleAnnotatedMethods(modelInstance, document);

        return modelInstance;
    }

    private <T> void handleAnnotatedFields(T modelInstance, Document document) throws IllegalAccessException {
        handleAnnotatedFields(modelInstance, document, ELEMENT_MAPPING_ANNOTATION, (field) -> new ElementExtractor(field.getAnnotation(ELEMENT_MAPPING_ANNOTATION)));
        handleAnnotatedFields(modelInstance, document, ATTRIBUTE_MAPPING_ANNOTATION, (field) -> new AttributeExtractor(field.getAnnotation(ATTRIBUTE_MAPPING_ANNOTATION)));
    }

    private <T> void handleAnnotatedFields(T modelInstance, Document document, Class<? extends Annotation> annotation, Function<Field, Extractor> extractorFactory) throws IllegalAccessException {
        for (Field field : FieldUtils.getFieldsWithAnnotation(modelInstance.getClass(), annotation)) {
            final String fieldTypeName = field.getType().getName();
            final Extractor extractor = extractorFactory.apply(field);
            FieldUtils.writeField(field, modelInstance, extractor.extract(document, fieldTypeName), true);
        }
    }

    private <T> void handleAnnotatedMethods(T modelInstance, Document document) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        handleAnnotatedMethods(modelInstance, document, ELEMENT_MAPPING_ANNOTATION, method -> new ElementExtractor(method.getAnnotation(ELEMENT_MAPPING_ANNOTATION)));
        handleAnnotatedMethods(modelInstance, document, ATTRIBUTE_MAPPING_ANNOTATION, method -> new AttributeExtractor(method.getAnnotation(ATTRIBUTE_MAPPING_ANNOTATION)));
    }

    private <T> void handleAnnotatedMethods(T modelInstance, Document document, Class<? extends Annotation> annotation, Function<Method, Extractor> extractorFactory) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        for (Method method : MethodUtils.getMethodsWithAnnotation(modelInstance.getClass(), annotation)) {
            if (method.getParameterCount() != 1) {
                throw new IllegalArgumentException("The annotated method must have only one argument");
            }

            final Class<?> type = method.getParameters()[0].getType();
            final String parameterTypeName = type.getName();
            MethodUtils.invokeMethod(modelInstance, method.getName(),
                    new Object[]{extractorFactory.apply(method).extract(document, parameterTypeName)},
                    new Class[]{type});
        }
    }
}