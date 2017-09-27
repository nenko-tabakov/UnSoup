package online.devtools.unsoup;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

class TypeMapper {

    private final Document document;

    TypeMapper(final Document document) {
        this.document = document;
    }

    private <T> T fromElementToModel(Element element, Class<T> model) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        final T modelInstance = model.newInstance();

        for (final Field field : model.getDeclaredFields()) {
            final Class<?> fieldType = field.getType();
            field.setAccessible(true);
            final Object value = getValue(element, fieldType, field);
            if (value != null) {
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

    private <T extends Collection> T fromElementsToCollection(Elements elements, Class<?> container, Class<?> type) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        final T t = (T) container.newInstance();

        for (Element element : elements) {
            if (PrimitiveTypesExtractor.isSupportedType(type)) {
                t.add(PrimitiveTypesExtractor.fromElement(element, type));
            } else {
                t.add(fromElementToModel(element, type));
            }
        }

        return t;
    }

    <T> T map(Class<T> model) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        final T modelInstance = model.newInstance();

        for (final Field field : model.getDeclaredFields()) {
            final Class<?> fieldType = field.getType();
            final Elements elements = getElements(document, field);
            if (elements != null && !elements.isEmpty()) {
                field.setAccessible(true);
                field.set(modelInstance, getValue(elements, fieldType, field));
            }
        }

        for (final Method method : model.getDeclaredMethods()) {
            if (method.getParameterCount() == 1) {
                final Class<?> methodType = method.getParameters()[0].getType();
                final Elements elements = getElements(document, method);
                if (elements != null && !elements.isEmpty()) {
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
                return fromElementsToCollection(elements, model, elementMapping.type());
            }
        } else if (PrimitiveTypesExtractor.isSupportedType(model)) {
            return getValue(elements.first(), model, accessibleObject);
        }
        return map(model);
    }

    private <T> T getValue(Element element, Class<T> type, AccessibleObject accessibleObject) {
        final ElementMapping elementMapping = accessibleObject.getAnnotation(ElementMapping.class);
        if (elementMapping != null) {
            return PrimitiveTypesExtractor.fromElement(element, type);
        }

        final AttributeMapping attributeMapping = accessibleObject.getAnnotation(AttributeMapping.class);
        if (attributeMapping != null) {
            return PrimitiveTypesExtractor.fromAttribute(element, type, attributeMapping.name());
        }

        return null;
    }

    private Elements getElements(Document document, AccessibleObject accessibleObject) {
        final ElementMapping elementMapping = accessibleObject.getAnnotation(ElementMapping.class);
        if (elementMapping != null) {
            return document.select(elementMapping.selector());
        }

        final AttributeMapping attributeMapping = accessibleObject.getAnnotation(AttributeMapping.class);
        if (attributeMapping != null) {
            return document.select(attributeMapping.selector());
        }

        return null;
    }
}