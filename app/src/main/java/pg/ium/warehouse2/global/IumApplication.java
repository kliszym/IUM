package pg.ium.warehouse2.global;

import android.app.Application;
import android.content.Context;

import pg.ium.warehouse2.network.User;
import pg.ium.warehouse2.ui.login.Role;

public class IumApplication extends Application {
    private User user;
    public Context main_context;

    public void setUser(String username, Role role, String id) {
        user = new User(username, role, id);
    }

    public User getUser() {
        return user;
    }
}
