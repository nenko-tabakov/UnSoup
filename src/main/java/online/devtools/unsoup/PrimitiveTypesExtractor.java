package online.devtools.unsoup;

import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

final class PrimitiveTypesExtractor {

    private PrimitiveTypesExtractor() {
    }

    private static final Function<Element, String> ELEMENT_STRING_VALUE = Element::ownText;

    private static final BiFunction<Element, String, String> ATTRIBUTE_STRING_VALUE = Element::attr;

    private static final Map<String, Function<String, ?>> STRING_EXTRACTORS = new HashMap<>();

    static {
        STRING_EXTRACTORS.put(String.class.getName(), s -> s);
        STRING_EXTRACTORS.put(Integer.class.getName(), Integer::valueOf);
        STRING_EXTRACTORS.put("int", Integer::valueOf);
        STRING_EXTRACTORS.put(Boolean.class.getName(), Boolean::valueOf);
        STRING_EXTRACTORS.put("boolean", Boolean::valueOf);
        STRING_EXTRACTORS.put(Float.class.getName(), Float::valueOf);
        STRING_EXTRACTORS.put("float", Float::valueOf);
        STRING_EXTRACTORS.put(Double.class.getName(), Double::valueOf);
        STRING_EXTRACTORS.put("double", Double::valueOf);
        STRING_EXTRACTORS.put(Long.class.getName(), Long::valueOf);
        STRING_EXTRACTORS.put("long", Long::valueOf);
        STRING_EXTRACTORS.put(Short.class.getName(), Short::valueOf);
        STRING_EXTRACTORS.put("short", Short::valueOf);
        STRING_EXTRACTORS.put(Byte.class.getName(), Byte::valueOf);
        STRING_EXTRACTORS.put("byte", Byte::valueOf);
    }

    static <T> T fromElement(Element element, Class<T> type) {
        if (isValidInput(element, type)) {
            return (T) ELEMENT_STRING_VALUE.andThen(STRING_EXTRACTORS.get(type.getName())).apply(element);
        }

        return null;
    }

    static <T> T fromAttribute(Element element, Class<T> type, String name) {
        if (isValidInput(element, type) && name != null && !name.isEmpty()) {
            return (T) ATTRIBUTE_STRING_VALUE.andThen(STRING_EXTRACTORS.get(type.getName())).apply(element, name);
        }

        return null;
    }

    static boolean isSupportedType(Class<?> type) {
        return STRING_EXTRACTORS.containsKey(type.getName());
    }

    private static boolean isValidInput(Element element, Class<?> type) {
        return element != null && type != null && STRING_EXTRACTORS.containsKey(type.getName());
    }
}