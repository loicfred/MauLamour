package my.dating.app.object.enums;

public enum LifestylePreference implements Displayable {
    YES("Yes"),
    YES_IF_PARTNER_COOKS("Yes, if partner cooks too"),
    WANT_TO_LEARN("No, but want to learn"),
    NEVER("Never");

    private final String displayName;

    LifestylePreference(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
