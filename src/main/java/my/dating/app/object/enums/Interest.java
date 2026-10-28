package my.dating.app.object.enums;

public enum Interest implements Displayable {
    MUSIC("Music"),
    GAMING("Gaming"),
    SPORTS("Sports"),
    TRAVELING("Traveling"),
    COOKING("Cooking"),
    BAKING("Baking"),
    READING("Reading"),
    MOVIES("Movies"),
    FITNESS("Fitness"),
    ART("Art"),
    DANCING("Dancing"),
    PHOTOGRAPHY("Photography"),
    TECHNOLOGY("Technology"),
    CODING("Coding"),
    WRITING("Writing"),
    HIKING("Hiking"),
    FASHION("Fashion"),
    GARDENING("Gardening"),
    VOLUNTEERING("Volunteering"),
    MEDITATION("Meditation"),
    PETS("Pets"),
    FOOD_TASTING("Food Tasting"),
    YOGA("Yoga"),
    SWIMMING("Swimming"),
    RUNNING("Running"),
    CYCLING("Cycling"),
    BOARD_GAMES("Board Games"),
    PUZZLES("Puzzles"),
    KNITTING("Knitting"),
    DIY_PROJECTS("DIY Projects"),
    CAMPING("Camping"),
    FISHING("Fishing"),
    SKATEBOARDING("Skateboarding"),
    SINGING("Singing"),
    THEATER("Theater"),
    LANGUAGE_LEARNING("Language Learning"),
    ASTRONOMY("Astronomy"),
    MARTIAL_ARTS("Martial Arts"),
    COLLECTING("Collecting");

    private final String displayName;

    Interest(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
