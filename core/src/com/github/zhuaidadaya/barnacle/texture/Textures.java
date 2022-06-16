package com.github.zhuaidadaya.barnacle.texture;

import com.badlogic.gdx.graphics.*;

import java.util.*;

public class Textures {
    public static LinkedHashMap<String, Texture> atlas = new LinkedHashMap<>();

    public Texture get(String name) {
        return atlas.get(name);
    }

    public void set(String name, Texture texture) {
        atlas.put(name, texture);
    }
}
