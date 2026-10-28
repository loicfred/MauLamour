package my.dating.app.object;

import my.dating.app.object.chatting.Chat;
import my.dating.app.object.msg.Message;
import my.dating.app.object.msg.Message_Reaction;

import static org.solarframework.db.spring.DatabaseRegistry.SolarDBManager;

// A write clears SolarFramework's cache for the registered entities only (DatabaseManager.resetCacheForClass), and a
// view class has no @Entity, so its cached rows would outlive the change by up to an hour. Each write the views read
// from calls one of these.
public final class ViewCache {
    private ViewCache() {}

    public static void forgetProfiles() {
        forget(Profile.Profile_View.class);
    }
    public static void forgetChats() {
        forget(Chat.Chatlist_Item.class);
        forget(Message.Message_View.class);
        forget(Message_Reaction.Message_Reaction_View.class);
    }

    private static void forget(Class<?> view) {
        if (SolarDBManager != null) SolarDBManager.resetCacheForClass(view, true, true);
    }
}
