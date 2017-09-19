package online.devtools.unsoup;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class AttributeExtractor extends Extractor<AttributeMapping> {

    private static final BiFunction<Elements, String, String> ATTRIBUTE_STRING_VALUE = (elements, name) -> elements.first().attr(name);

    private static final BiFunction<Elements, String, Integer> ATTRIBUTE_INTEGER_VALUE = ATTRIBUTE_STRING_VALUE.andThen(STRING_TO_INTEGER);

    private static final BiFunction<Elements, String, Boolean> ATTRIBUTE_BOOLEAN_VALUE = ATTRIBUTE_STRING_VALUE.andThen(STRING_TO_BOOLEAN);

    private static final BiFunction<Elements, String, Float> ATTRIBUTE_FLOAT_VALUE = ATTRIBUTE_STRING_VALUE.andThen(STRING_TO_FLOAT);

    private static final BiFunction<Elements, String, Double> ATTRIBUTE_DOUBLE_VALUE = ATTRIBUTE_STRING_VALUE.andThen(STRING_TO_DOUBLE);

    private static final BiFunction<Elements, String, Long> ATTRIBUTE_LONG_VALUE = ATTRIBUTE_STRING_VALUE.andThen(STRING_TO_LONG);

    private static final BiFunction<Elements, String, Short> ATTRIBUTE_SHORT_VALUE = ATTRIBUTE_STRING_VALUE.andThen(STRING_TO_SHORT);

    private static final BiFunction<Elements, String, Byte> ATTRIBUTE_BYTE_VALUE = ATTRIBUTE_STRING_VALUE.andThen(STRING_TO_BYTE);

    private static final Map<String, BiFunction<Elements, String, ? extends Object>> ATTRIBUTE_EXTRACTORS = new HashMap<>();

    static {
        ATTRIBUTE_EXTRACTORS.put(String.class.getName(), ATTRIBUTE_STRING_VALUE);
        ATTRIBUTE_EXTRACTORS.put(Integer.class.getName(), ATTRIBUTE_INTEGER_VALUE);
        ATTRIBUTE_EXTRACTORS.put("int", ATTRIBUTE_INTEGER_VALUE);
        ATTRIBUTE_EXTRACTORS.put(Boolean.class.getName(), ATTRIBUTE_BOOLEAN_VALUE);
        ATTRIBUTE_EXTRACTORS.put("boolean", ATTRIBUTE_BOOLEAN_VALUE);
        ATTRIBUTE_EXTRACTORS.put(Float.class.getName(), ATTRIBUTE_FLOAT_VALUE);
        ATTRIBUTE_EXTRACTORS.put("float", ATTRIBUTE_FLOAT_VALUE);
        ATTRIBUTE_EXTRACTORS.put(Double.class.getName(), ATTRIBUTE_DOUBLE_VALUE);
        ATTRIBUTE_EXTRACTORS.put("double", ATTRIBUTE_DOUBLE_VALUE);
        ATTRIBUTE_EXTRACTORS.put(Long.class.getName(), ATTRIBUTE_LONG_VALUE);
        ATTRIBUTE_EXTRACTORS.put("long", ATTRIBUTE_LONG_VALUE);
        ATTRIBUTE_EXTRACTORS.put(Short.class.getName(), ATTRIBUTE_SHORT_VALUE);
        ATTRIBUTE_EXTRACTORS.put("short", ATTRIBUTE_SHORT_VALUE);
        ATTRIBUTE_EXTRACTORS.put(Byte.class.getName(), ATTRIBUTE_BYTE_VALUE);
        ATTRIBUTE_EXTRACTORS.put("byte", ATTRIBUTE_BYTE_VALUE);
    }


    public AttributeExtractor(final AttributeMapping annotation) {
        super(annotation);
    }


    public Object extract(final Document document, final String type) {
        final Elements elements = getElements(document, annotation.selector());
        if (elements != null && !elements.isEmpty()) {
            if (ATTRIBUTE_EXTRACTORS.containsKey(type)) {
                return ATTRIBUTE_EXTRACTORS.get(type).apply(elements, annotation.name());
            } else {
                throw new IllegalArgumentException("This type is not supported: " + type);
            }
        }

        return null;
    }
}