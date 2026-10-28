package my.dating.app.object.enums;

public enum EthnicBackground implements Displayable {
    AFRICAN("African"),
    ARAB("Arab"),
    CHINESE("Chinese"),
    EAST_ASIAN("East Asian"),
    EUROPEAN("European"),
    INDIAN("Indian"),
    LATIN_AMERICAN("Latin American"),
    MIXED("Mixed"),
    OTHER("Other");

    private final String displayName;

    EthnicBackground(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
