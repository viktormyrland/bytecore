package net.bbytes.bukkit.user;

import java.util.Locale;

public enum Language {

    ENGLISH("en", Locale.ENGLISH),
    FRENCH("fr", Locale.FRENCH),
    SPANISH("es", new Locale("es", "ES")),
    RUSSIAN("ru", new Locale("ru", "RU"));

    private String id;
    private Locale locale;

    Language(String id, Locale locale) {
        this.id = id;
        this.locale = locale;
    }

    public String getID() {
        return id;
    }

    public static Language getLanguage(String id){
        for(Language lang : values())
            if(lang.getID().equals(id))
                return lang;
            return null;
    }

    public Locale getLocale() {
        return locale;
    }
}
