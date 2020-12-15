package pg.ium.warehouse2.network;

import org.json.JSONException;
import org.json.JSONObject;

public class Credentials {
    public String username;
    public String password;

    public Credentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public JSONObject json() {
        JSONObject credentials = new JSONObject();
        try {
            credentials.put("username", username);
            credentials.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return credentials;
    }
}
