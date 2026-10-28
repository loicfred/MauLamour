package my.dating.app.object.enums;

public enum EducationLevel implements Displayable {
    NONE("None"),
    SECONDARY("Secondary school"),
    VOCATIONAL("Vocational diploma"),
    BACHELOR("Bachelor's degree"),
    MASTER("Master's degree"),
    DOCTORATE("Doctorate");

    private final String displayName;

    EducationLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
