package my.dating.app.object.enums;

public enum RelationshipStatus implements Displayable {
    SINGLE("Single"),
    SEPARATED("Separated"),
    DIVORCED("Divorced"),
    WIDOWED("Widowed");

    private final String displayName;

    RelationshipStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
