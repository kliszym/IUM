package pg.ium.warehouse2.network;

import pg.ium.warehouse2.ui.login.Role;

public class User  {
    public String username;
    public Role role;
    public String id;

    public User(String username, Role role, String id) {
        this.username = username;
        this.role = role;
        this.id = id;
    }
}
