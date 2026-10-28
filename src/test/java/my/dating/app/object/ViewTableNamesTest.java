package my.dating.app.object;

import my.dating.app.object.chatting.Chat;
import my.dating.app.object.msg.Message;
import my.dating.app.object.msg.Message_Reaction;
import org.junit.jupiter.api.Test;
import org.solarframework.db.spring.DatabaseObject;

import static org.junit.jupiter.api.Assertions.assertEquals;

// A view class extends its table's entity, and DatabaseObject.getTableName walks up to the parent's @Table,
// so without its own @Table a view class reads the plain table.
class ViewTableNamesTest {

    @Test
    void eachViewClassReadsItsView() {
        assertEquals("profile_view", DatabaseObject.getTableName(Profile.Profile_View.class));
        assertEquals("chatlist_item", DatabaseObject.getTableName(Chat.Chatlist_Item.class));
        assertEquals("message_view", DatabaseObject.getTableName(Message.Message_View.class));
        assertEquals("message_reaction_view", DatabaseObject.getTableName(Message_Reaction.Message_Reaction_View.class));
    }
}
