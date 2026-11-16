package com.nmertcan.library.domain;

import java.util.Objects;

public abstract class Person {
    private final String id;
    private final String name;

    protected Person(String id, String name) {
        this.id = required(id, "Kimlik");
        this.name = required(name, "Ad");
    }

    public String id() { return id; }
    public String name() { return name; }

    protected static String required(String value, String field) {
        Objects.requireNonNull(value, field);
        if (value.isBlank()) throw new IllegalArgumentException(field + " boş olamaz.");
        return value.trim();
    }
}
