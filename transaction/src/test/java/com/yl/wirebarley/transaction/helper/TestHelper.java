package com.yl.wirebarley.transaction.helper;

import static org.springframework.test.util.ReflectionTestUtils.setField;

public class TestHelper {
    public static <T> void setId(T entity, Long id) {
        setField(entity, "id", id);
    }
}
