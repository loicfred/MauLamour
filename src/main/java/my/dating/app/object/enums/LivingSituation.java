package my.dating.app.object.enums;

public enum LivingSituation implements Displayable {
    ALONE("Alone"),
    WITH_FAMILY("With family"),
    WITH_FLATMATES("With flatmates"),
    WITH_MY_CHILDREN("With my children");

    private final String displayName;

    LivingSituation(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
