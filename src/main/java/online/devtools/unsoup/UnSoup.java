package online.devtools.unsoup;

import org.jsoup.Jsoup;

import org.jsoup.nodes.Document;

import org.jsoup.select.Elements;


import java.io.File;

import java.io.IOException;

import java.lang.reflect.Field;

import java.net.URL;

import java.util.Collection;

import java.util.HashMap;

import java.util.Map;

import java.util.function.Function;

import java.util.stream.Collectors;


public class UnSoup {


    public static final String TIMEOUT = "timeout";

    public static final String ENCODING = "UTF-8";

    private static final Function<Elements, String> SINGLE_VALUE = (elements) -> elements.first().ownText();

    private static final Function<Elements, Collection<String>> COLLECTION_VALUE = (elements) -> elements.stream().map(element -> element.ownText()).collect(Collectors.toList());

    private static final Map<String, Function<Elements, ? extends Object>> OBJECT_VALUE_EXTRACTORS = new HashMap<>();

    static {
        OBJECT_VALUE_EXTRACTORS.put("java.lang.String", SINGLE_VALUE);
        OBJECT_VALUE_EXTRACTORS.put("java.util.Collection", COLLECTION_VALUE);
    }

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

    public <T> T unsoup(File file, Class<T> model) throws IOException, IllegalAccessException, InstantiationException {
        final String encoding = configuration.getOrDefault(ENCODING, "UTF-8");
        return unsoup(Jsoup.parse(file, encoding), model);
    }

    public <T> T unsoup(URL url, Class<T> model) throws IOException, IllegalAccessException, InstantiationException {
        final String timeout = configuration.getOrDefault(TIMEOUT, "120000");
        return unsoup(Jsoup.parse(url, Integer.parseInt(timeout)), model);
    }

    public <T> T unsoup(String url, Class<T> model) throws IOException, InstantiationException, IllegalAccessException {
        return unsoup(Jsoup.connect(url).get(), model);
    }

    private <T> T unsoup(Document document, Class<T> model) throws IOException, IllegalAccessException, InstantiationException {
        T modelInstance = model.newInstance();

        for (Field field : model.getDeclaredFields()) {
            if (field.isAnnotationPresent(ElementMapping.class)) {
                Class<?> fieldType = field.getType();
                String fieldTypeName = fieldType.getName();
                if (fieldTypeName != null) {
                    field.setAccessible(true);
                    Elements elements = getElements(document, field.getAnnotation(ElementMapping.class));
                    if (elements != null && !elements.isEmpty()) {
                        switch (fieldTypeName) {
                            case "int":
                                setInt(modelInstance, field, elements);
                                break;
                            case "boolean":
                                setBoolean(modelInstance, field, elements);
                                break;
                            case "float":
                                setFloat(modelInstance, field, elements);
                                break;
                            case "double":
                                setDouble(modelInstance, field, elements);
                                break;
                            case "long":
                                setLong(modelInstance, field, elements);
                                break;
                            case "short":
                                setShort(modelInstance, field, elements);
                                break;
                            case "byte":
                                setByte(modelInstance, field, elements);
                                break;
                            default:
                                if (OBJECT_VALUE_EXTRACTORS.containsKey(fieldTypeName)) {
                                    field.set(modelInstance, OBJECT_VALUE_EXTRACTORS.get(fieldTypeName).apply(elements));
                                } else {
                                    throw new IllegalArgumentException("The annotated type is not supported: " + fieldTypeName);
                                }
                        }
                    }
                }
            }
        }

        return modelInstance;
    }


    private Elements getElements(Document document, ElementMapping annotation) {
        String selector = annotation.selector();
        if (selector != null && !selector.isEmpty()) {
            return document.select(selector);
        }

        return null;
    }

    private <T> void setInt(T object, Field field, Elements elements) throws IllegalAccessException {
        try {
            field.setInt(object, Integer.parseInt(SINGLE_VALUE.apply(elements)));
        } catch (NumberFormatException e) {
            //TODO: What is appropriate action here?
        }
    }

    private <T> void setFloat(T object, Field field, Elements elements) throws IllegalAccessException {
        try {
            field.setFloat(object, Float.parseFloat(SINGLE_VALUE.apply(elements)));
        } catch (NumberFormatException e) {
            //TODO: What is appropriate action here?
        }
    }

    private <T> void setDouble(T object, Field field, Elements elements) throws IllegalAccessException {
        try {
            field.setDouble(object, Double.parseDouble(SINGLE_VALUE.apply(elements)));
        } catch (NumberFormatException e) {
            //TODO: What is appropriate action here?
        }
    }

    private <T> void setLong(T object, Field field, Elements elements) throws IllegalAccessException {
        try {
            field.setLong(object, Long.parseLong(SINGLE_VALUE.apply(elements)));
        } catch (NumberFormatException e) {
            //TODO: What is appropriate action here?
        }
    }


    private <T> void setShort(T object, Field field, Elements elements) throws IllegalAccessException {
        try {
            field.setShort(object, Short.parseShort(SINGLE_VALUE.apply(elements)));
        } catch (NumberFormatException e) {
            //TODO: What is appropriate action here?
        }
    }


    private <T> void setByte(T object, Field field, Elements elements) throws IllegalAccessException {
        field.setByte(object, Byte.parseByte(SINGLE_VALUE.apply(elements)));
    }

    private <T> void setBoolean(T object, Field field, Elements elements) throws IllegalAccessException {
        field.setBoolean(object, Boolean.parseBoolean(SINGLE_VALUE.apply(elements)));
    }
}