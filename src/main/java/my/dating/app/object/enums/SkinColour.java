package my.dating.app.object.enums;

public enum SkinColour implements Displayable {
    VERY_FAIR("Very fair"),
    FAIR("Fair"),
    MEDIUM("Medium"),
    OLIVE("Olive"),
    BROWN("Brown"),
    DARK_BROWN("Dark brown"),
    VERY_DARK("Very dark");

    private final String displayName;

    SkinColour(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
