package my.dating.app.object.enums;

public enum Religion implements Displayable {
    NONE("None"),
    CHRISTIANITY("Christianity"),
    ISLAM("Islam"),
    JUDAISM("Judaism"),
    HINDUISM("Hinduism"),
    BUDDHISM("Buddhism"),
    OTHER("Other");

    private final String displayName;

    Religion(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
