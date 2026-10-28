package my.dating.app.object.enums;

public enum EyeColour implements Displayable {
    BROWN("Brown"),
    BLACK("Black"),
    HAZEL("Hazel"),
    GREEN("Green"),
    BLUE("Blue"),
    GREY("Grey"),
    OTHER("Other");

    private final String displayName;

    EyeColour(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
