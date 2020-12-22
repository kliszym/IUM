package pg.ium.warehouse2.network;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pg.ium.warehouse2.data.OutPutter;
import pg.ium.warehouse2.global.IumApplication;
import pg.ium.warehouse2.ui.product.ProductInfo;
import pg.ium.warehouse2.ui.login.Role;
import pg.ium.warehouse2.ui.main.MainActivity;

public class Connection {
    private String address = "http://10.0.2.2:8080";
    private String login_address = address + "/login";
    private String info_address = address + "/info";
    private String update_address = address + "/update";
    private String increase_address = address + "/increase";
    private String decrease_address = address + "/decrease";
    private String create_address = address + "/create";
    private String remove_address = address + "/remove";
    private String token_address = address + "/token";

    Context context;

    RequestQueue requestQueue;

    public Connection(Context context) {
        this.context = context;
        Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache, network, 1);
        requestQueue.start();
        }

    private JSONObject prepareData(List<ProductInfo> product_infos) {
        JSONArray j_array = new JSONArray();
        for(ProductInfo product_info : product_infos) {
            j_array.put(product_info.json());
        }

        JSONObject obj = new JSONObject();
        try {
            obj.put("id", getId());
            obj.put("products", j_array);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj;
    }

    private String getId() {
        IumApplication app = (IumApplication)context.getApplicationContext();
        return app.getUser().id;
    }

    public void getInfo(final List<ProductInfo> product_infos) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("id", getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.POST, info_address, obj, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray result = (JSONArray)response.get("result");
                            for(int i = 0; i < result.length(); i++) {
                                JSONObject product = (JSONObject) result.get(i);
                                product_infos.add(new ProductInfo(
                                        product.getString("obj_id"),
                                        product.getString("manufacturer"),
                                        product.getString("model"),
                                        product.getDouble("price"),
                                        product.getInt("quantity")
                                ));
                            }

                            OutPutter op = new OutPutter(context);
                            op.flush_base();
                            for(ProductInfo product : product_infos) {
                                op.write(product);
                            }
                            ((MainActivity)context).update();

                        } catch (JSONException e) {
                            Toast.makeText(context, "Unexpected error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(context, "Unexpected error", Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonRequest);
    }

    public void update(List<ProductInfo> product_infos) {
        JSONObject obj = prepareData(product_infos);
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.POST, update_address, obj, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        IumApplication app = (IumApplication)context.getApplicationContext();
                        ((MainActivity)app.main_context).update();
                    }
                   }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(context, "Unexpected error", Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonRequest);
    }

    public void create(List<ProductInfo> product_infos, Response.Listener<JSONObject> listener) {
        JSONObject obj = prepareData(product_infos);
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.POST, create_address, obj, listener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(context, "Unexpected error", Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonRequest);
    }

    public void remove(List<ProductInfo> product_infos) {
        JSONObject obj = prepareData(product_infos);
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.POST, remove_address, obj, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        IumApplication app = (IumApplication)context.getApplicationContext();
                        ((MainActivity)app.main_context).update();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(context, "Unexpected error", Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonRequest);
    }

    public void increase(List<ProductInfo> product_infos) {
        JSONObject obj = prepareData(product_infos);
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.POST, increase_address, obj, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        IumApplication app = (IumApplication)context.getApplicationContext();
                        ((MainActivity)app.main_context).update();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(context, "Unexpected error", Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonRequest);
    }

    public void decrease(List<ProductInfo> product_infos) {
        JSONObject obj = prepareData(product_infos);
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.POST, decrease_address, obj, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        IumApplication app = (IumApplication)context.getApplicationContext();
                        ((MainActivity)app.main_context).update();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(context, "Unexpected error", Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonRequest);
    }

    public void signIn(final Credentials credentials) {
        final JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.POST, login_address, credentials.json(), new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        String id;
                        boolean master_role;
                        try {
                            master_role = (boolean)response.get("role");
                            id = (String)response.get("id");
                        } catch (JSONException e) {
                            master_role = false;
                            id= "";
                        }
                        Role role = Role.set(master_role);
                        IumApplication app = (IumApplication)context.getApplicationContext();
                        app.setUser(credentials.username, role, id);
                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Wrong username or password", Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonRequest);
    }

    public void token(String token) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("token", token);
            Log.w("STR_TOKEN", token);
        } catch (JSONException e) {
            Log.w("TOKEN", e.toString());
        }
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.POST, token_address, obj, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String username;
                        boolean master_role;
                        String id;
                        try {
                            username = (String)response.get("username");
                            master_role = (boolean)response.get("role");
                            id = (String)response.get("id");
                        } catch (JSONException e) {
                            username = "";
                            master_role = false;
                            id= "";
                        }
                        Role role = Role.set(master_role);
                        IumApplication app = (IumApplication)context.getApplicationContext();
                        app.setUser(username, role, id);
                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(context, "Unexpected error", Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonRequest);
    }
}
