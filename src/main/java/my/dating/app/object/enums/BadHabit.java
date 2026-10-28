package my.dating.app.object.enums;

public enum BadHabit implements Displayable {
    DAILY("Every day"),
    WEEKLY("Once a week"),
    OCCASIONAL("On special occasions"),
    NONE("No");

    private final String displayName;

    BadHabit(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
