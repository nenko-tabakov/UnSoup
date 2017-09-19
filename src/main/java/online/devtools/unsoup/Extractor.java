package online.devtools.unsoup;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.lang.annotation.Annotation;
import java.util.function.Function;

abstract class Extractor<T extends Annotation> {

    static final Function<String, Integer> STRING_TO_INTEGER = s -> Integer.valueOf(s);

    static final Function<String, Boolean> STRING_TO_BOOLEAN = s -> Boolean.valueOf(s);

    static final Function<String, Float> STRING_TO_FLOAT = s -> Float.valueOf(s);

    static final Function<String, Double> STRING_TO_DOUBLE = s -> Double.valueOf(s);

    static final Function<String, Long> STRING_TO_LONG = s -> Long.valueOf(s);

    static final Function<String, Short> STRING_TO_SHORT = s -> Short.valueOf(s);

    static final Function<String, Byte> STRING_TO_BYTE = s -> Byte.valueOf(s);
    protected final T annotation;

    protected Extractor(final T annotation) {
        this.annotation = annotation;
    }

    protected Elements getElements(final Document document, final String selector) {
        if (selector != null && !selector.isEmpty()) {
            return document.select(selector);
        }

        return null;
    }

    abstract public Object extract(final Document document, final String type);
}