package com.alonso.eatelligence.utils;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

public class ValidationUtils {

    // Mapa con el orden de prioridad de anotaciones
    private static final Map<Class<? extends Annotation>, Integer> ANNOTATION_ORDER;

    static {
        Map<Class<? extends Annotation>, Integer> map = new HashMap<>();
        map.put(jakarta.validation.constraints.NotBlank.class, 1);
        map.put(jakarta.validation.constraints.Size.class, 2);
        map.put(jakarta.validation.constraints.Pattern.class, 3);
        ANNOTATION_ORDER = Collections.unmodifiableMap(map);
    }

    public static List<String> getFieldPathsInOrder(Class<?> clazz) {
        List<String> fieldPaths = new ArrayList<>();
        collectFieldPaths("", clazz, fieldPaths);
        return fieldPaths;
    }

    private static void collectFieldPaths(String prefix, Class<?> clazz, List<String> paths) {
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String fullPath = prefix.isEmpty() ? field.getName() : prefix + "." + field.getName();

            if (isSimpleField(field)) {
                paths.add(fullPath);
            } else {
                collectFieldPaths(fullPath, field.getType(), paths);
            }
        }
    }

    private static boolean isSimpleField(Field field) {
        Class<?> type = field.getType();
        return type.isPrimitive()
            || type.equals(String.class)
            || Number.class.isAssignableFrom(type)
            || Boolean.class.isAssignableFrom(type)
            || Enum.class.isAssignableFrom(type);
    }

    public static Optional<FieldError> getFirstOrderedError(List<FieldError> errors, List<String> orderedFieldPaths) {
        return errors.stream()
            .sorted(Comparator
                .comparingInt((FieldError fe) -> orderedFieldPaths.indexOf(fe.getField()))
                .thenComparingInt(fe -> getAnnotationOrder(fe.getCode()))
            ).findFirst();
    }

    public static Optional<FieldError> getFirstOrderedErrorFromBindingResult(BindingResult result, Class<?> dtoClass) {
        List<String> orderedFieldPaths = getFieldPathsInOrder(dtoClass);
        return getFirstOrderedError(result.getFieldErrors(), orderedFieldPaths);
    }

    private static int getAnnotationOrder(String code) {
        if (code == null) return Integer.MAX_VALUE;
        try {
            for (Map.Entry<Class<? extends Annotation>, Integer> entry : ANNOTATION_ORDER.entrySet()) {
                if (entry.getKey().getSimpleName().equalsIgnoreCase(code)) {
                    return entry.getValue();
                }
            }
        } catch (Exception ignored) {}
        return Integer.MAX_VALUE;
    }
}