package online.devtools.unsoup;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.function.Function;

class TypeMapper {

    private final Document document;

    TypeMapper(final Document document) {
        this.document = document;
    }


    private <T> T fromElementToModel(Element element, Class<T> model) throws IllegalAccessException, InstantiationException, InvocationTargetException {

        final T modelInstance = model.newInstance();

        for (final Field field : model.getDeclaredFields()) {
            final Object value = getValue(element, field.getType(), field);
            if (value != null) {
                field.setAccessible(true);
                field.set(modelInstance, value);
            }
        }

        for (final Method method : model.getDeclaredMethods()) {
            if (method.getParameterCount() == 1) {
                final Class<?> methodType = method.getParameters()[0].getType();
                final Object value = getValue(element, methodType, method);
                if (value != null) {
                    method.invoke(modelInstance, value);
                }
            }
        }

        return modelInstance;
    }

    private <T extends Collection> T fromElementsToCollection(Elements elements, Class<?> container, Class<?> type, AccessibleObject accessibleObject) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        final T t = (T) container.newInstance();

        for (Element element : elements) {
            if (PrimitiveTypesExtractor.isSupportedType(type)) {
                t.add(getValue(element, type, accessibleObject));
            } else {
                t.add(fromElementToModel(element, type));
            }
        }

        return t;
    }

    <T> T map(Class<T> model) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        final T modelInstance = model.newInstance();

        for (final Field field : model.getDeclaredFields()) {
            final Elements elements = getElements(document, field);
            if (elements != null && !elements.isEmpty()) {
                field.setAccessible(true);
                field.set(modelInstance, getValue(elements, field.getType(), field));
            }
        }

        for (final Method method : model.getDeclaredMethods()) {
            if (method.getParameterCount() == 1) {
                final Elements elements = getElements(document, method);
                if (elements != null && !elements.isEmpty()) {
                    final Class<?> methodType = method.getParameters()[0].getType();
                    method.invoke(modelInstance, getValue(elements, methodType, method));
                }
            }
        }

        return modelInstance;
    }

    private Object getValue(Elements elements, Class<?> model, AccessibleObject accessibleObject) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        if (Collection.class.isAssignableFrom(model)) {
            final ElementMapping elementMapping = accessibleObject.getAnnotation(ElementMapping.class);
            if (elementMapping != null) {
                return fromElementsToCollection(elements, model, elementMapping.type(), accessibleObject);
            }

            return null;
        } else if (PrimitiveTypesExtractor.isSupportedType(model)) {
            return getValue(elements.first(), model, accessibleObject);
        }

        return map(model);
    }

    private <T> T getValue(Element element, Class<T> type, AccessibleObject accessibleObject) {
        return getValueIfAnnotationPresent(accessibleObject, elementMapping -> {
            final String attributeName = elementMapping.attributeName();
            if (!attributeName.isEmpty()) {
                return PrimitiveTypesExtractor.fromAttribute(element, type, attributeName);
            }
            return PrimitiveTypesExtractor.fromElement(element, type);
        });
    }

    private Elements getElements(Document document, AccessibleObject accessibleObject) {
        return getValueIfAnnotationPresent(accessibleObject, elementMapping -> document.select(elementMapping.selector()));
    }

    private <T> T getValueIfAnnotationPresent(AccessibleObject accessibleObject, Function<ElementMapping, T> valueExtractor) {
        final ElementMapping elementMapping = accessibleObject.getAnnotation(ElementMapping.class);
        if (elementMapping != null) {
            return valueExtractor.apply(elementMapping);
        }

        return null;
    }
}