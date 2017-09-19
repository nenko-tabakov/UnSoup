package online.devtools.unsoup;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ElementExtractor extends Extractor<ElementMapping> {

    private static final Function<Elements, String> STRING_VALUE = (elements) -> elements.first().ownText();

    private static final Function<Elements, Integer> INTEGER_VALUE = STRING_VALUE.andThen(STRING_TO_INTEGER);

    private static final Function<Elements, Boolean> BOOLEAN_VALUE = STRING_VALUE.andThen(STRING_TO_BOOLEAN);

    private static final Function<Elements, Float> FLOAT_VALUE = STRING_VALUE.andThen(STRING_TO_FLOAT);

    private static final Function<Elements, Double> DOUBLE_VALUE = STRING_VALUE.andThen(STRING_TO_DOUBLE);

    private static final Function<Elements, Long> LONG_VALUE = STRING_VALUE.andThen(STRING_TO_LONG);

    private static final Function<Elements, Short> SHORT_VALUE = STRING_VALUE.andThen(STRING_TO_SHORT);

    private static final Function<Elements, Byte> BYTE_VALUE = STRING_VALUE.andThen(STRING_TO_BYTE);

    private static final Function<Elements, Collection<String>> COLLECTION_VALUE = (elements) -> elements.stream().map(element -> element.ownText()).collect(Collectors.toList());

    private static final Map<String, Function<Elements, ? extends Object>> ELEMENT_EXTRACTORS = new HashMap<>();

    static {
        ELEMENT_EXTRACTORS.put(String.class.getName(), STRING_VALUE);
        ELEMENT_EXTRACTORS.put(Integer.class.getName(), INTEGER_VALUE);
        ELEMENT_EXTRACTORS.put("int", INTEGER_VALUE);
        ELEMENT_EXTRACTORS.put(Boolean.class.getName(), BOOLEAN_VALUE);
        ELEMENT_EXTRACTORS.put("boolean", BOOLEAN_VALUE);
        ELEMENT_EXTRACTORS.put(Float.class.getName(), FLOAT_VALUE);
        ELEMENT_EXTRACTORS.put("float", FLOAT_VALUE);
        ELEMENT_EXTRACTORS.put(Double.class.getName(), DOUBLE_VALUE);
        ELEMENT_EXTRACTORS.put("double", DOUBLE_VALUE);
        ELEMENT_EXTRACTORS.put(Long.class.getName(), LONG_VALUE);
        ELEMENT_EXTRACTORS.put("long", LONG_VALUE);
        ELEMENT_EXTRACTORS.put(Short.class.getName(), SHORT_VALUE);
        ELEMENT_EXTRACTORS.put("short", SHORT_VALUE);
        ELEMENT_EXTRACTORS.put(Byte.class.getName(), BYTE_VALUE);
        ELEMENT_EXTRACTORS.put("byte", BYTE_VALUE);
        ELEMENT_EXTRACTORS.put(Collection.class.getName(), COLLECTION_VALUE);
        ELEMENT_EXTRACTORS.put(ArrayList.class.getName(), COLLECTION_VALUE);
        ELEMENT_EXTRACTORS.put(LinkedList.class.getName(), COLLECTION_VALUE);
    }

    public ElementExtractor(final ElementMapping annotation) {
        super(annotation);
    }

    public Object extract(final Document document, final String type) {
        final Elements elements = getElements(document, annotation.selector());
        if (elements != null && !elements.isEmpty()) {
            if (ELEMENT_EXTRACTORS.containsKey(type)) {
                return ELEMENT_EXTRACTORS.get(type).apply(elements);
            } else {
                throw new IllegalArgumentException("This type is not supported: " + type);
            }
        }

        return null;
    }
}