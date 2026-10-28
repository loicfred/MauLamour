package my.dating.app.object.enums;

public enum Rapidity implements Displayable {
    INSTANTLY("Almost instantly"),
    FAST("Within an hour"),
    MEDIUM("Within a few hours"),
    SLOW("Within a day or more");

    private final String displayName;

    Rapidity(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
