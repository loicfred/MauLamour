package my.dating.app.object;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.solarframework.auth.obj.Account_User;
import org.solarframework.db.spring.DatabaseObject;

import java.time.Instant;
import java.util.List;

import static org.solarframework.db.spring.DatabaseRegistry.SolarDBManager;

@Entity
@Table(name = "profile_photo")
public class Profile_Photo extends DatabaseObject.ID_OBJ<Long, Profile_Photo> {

    public long UserID;
    public byte[] Image;

    public Profile_Photo() {}
    public Profile_Photo(Account_User user, byte[] img) {
        this.ID = Instant.now().toEpochMilli();
        this.UserID = user.getID();
        this.Image = img;
        Write();
    }

    public static Profile_Photo getById(long id) {
        return SolarDBManager.getById(Profile_Photo.class, id).orElse(null);
    }
    public static List<Profile_Photo> getByUser(long userid) {
        return SolarDBManager.getAllWhere(Profile_Photo.class, "UserID = ?", userid);
    }
}
