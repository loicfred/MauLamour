package my.dating.app.object.enums;

public enum HairColour implements Displayable {
    BLACK("Black"),
    BROWN("Brown"),
    BLONDE("Blonde"),
    RED("Red"),
    GREY_OR_WHITE("Grey or white"),
    DYED("Dyed"),
    NO_HAIR("No hair");

    private final String displayName;

    HairColour(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
