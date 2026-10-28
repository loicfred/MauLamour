package my.dating.app.object.enums;

public enum Diet implements Displayable {
    OMNIVORE("Omnivore"),
    VEGETARIAN("Vegetarian"),
    VEGAN("Vegan");

    private final String displayName;

    Diet(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
