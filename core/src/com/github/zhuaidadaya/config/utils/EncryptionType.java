package com.github.zhuaidadaya.config.utils;

public enum EncryptionType {
    RANDOM_SEQUENCE(0, "Random Sequence"), COMPOSITE_SEQUENCE(1, "Composite Sequence"), MIXED_SEQUENCE(2,"Mixed Sequence");

    final int id;
    final String name;

    EncryptionType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
