package com.github.zhuaidadaya.MCH.language;

public enum Language {
    /**
     * supported languages in MCH
     *
     */
    CHINESE(0, "Chinese"), ENGLISH(1, "English"), CHINESE_TW(2, "Chinese_tw"),AUTO(3,"Auto");

    private final int value;
    private final String name;

    /**
     * init, set language
     * @param value value(ID) of language
     * @param name name of Language
     */
    Language(int value, String name) {
        this.value = value;
        this.name = name;
    }

    /**
     * get language value(ID)
     */
    public int getValue() {
        return value;
    }


    /**
     * get language name
     */
    public String getName() {
        return name;
    }

    public Language getLanguageForName(String name) {
        Language language = null;
        switch(name) {
            case "Chinese": {
                language = CHINESE;
                break;
            }
            case "English": {
                language = ENGLISH;
                break;
            }
            case "Chinese_tw": {
                language = CHINESE_TW;
                break;
            }
            case "Auto": {
                language = AUTO;
                break;
            }
        }
        return language;
    }
}
