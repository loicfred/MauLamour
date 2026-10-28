package my.dating.app.object.enums;

public enum BodyType implements Displayable {
    SLIM("Slim"),
    ATHLETIC("Athletic"),
    AVERAGE("Average"),
    CURVY("Curvy"),
    MUSCULAR("Muscular"),
    PLUS_SIZE("Plus size");

    private final String displayName;

    BodyType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
