package my.dating.app.object.enums;

public enum Language implements Displayable {
    ENGLISH("English"),
    SPANISH("Spanish"),
    FRENCH("French"),
    GERMAN("German"),
    ITALIAN("Italian"),
    PORTUGUESE("Portuguese"),
    RUSSIAN("Russian"),
    CHINESE("Chinese (Mandarin)"),
    JAPANESE("Japanese"),
    KOREAN("Korean"),
    HINDI("Hindi"),
    ARABIC("Arabic"),
    TURKISH("Turkish"),
    DUTCH("Dutch"),
    SWEDISH("Swedish"),
    NORWEGIAN("Norwegian"),
    DANISH("Danish"),
    POLISH("Polish"),
    GREEK("Greek"),
    HEBREW("Hebrew");

    private final String displayName;

    Language(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
