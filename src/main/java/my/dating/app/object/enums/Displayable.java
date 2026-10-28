package my.dating.app.object.enums;

// An answer's wording on screen: every profile enum has one, so pages and matching can show any of them the same way.
public interface Displayable {
    String name(); // the stored value, given by Enum
    String getDisplayName();
}
