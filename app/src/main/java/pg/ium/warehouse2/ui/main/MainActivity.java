package pg.ium.warehouse2.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pg.ium.warehouse2.R;
import pg.ium.warehouse2.data.Jointer;
import pg.ium.warehouse2.data.OutPutter;
import pg.ium.warehouse2.global.IumApplication;
import pg.ium.warehouse2.network.Connection;
import pg.ium.warehouse2.ui.login.Role;
import pg.ium.warehouse2.ui.product.Change;
import pg.ium.warehouse2.ui.product.ProductActivity;
import pg.ium.warehouse2.ui.product.ProductInfo;

public class MainActivity extends AppCompatActivity {

    public ProductTable productTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IumApplication app = (IumApplication)getApplicationContext();
        app.main_context = this;
        final SwipeRefreshLayout srl = findViewById(R.id.layout_swiperefresh);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                synchronize();
                srl.setRefreshing(false);
            }
        });

        Role role = app.getUser().role;
        if(role == Role.WORKER) {
            Button btn;
            btn = (Button) findViewById(R.id.button_remove);
            btn.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        update();
    }

    private List<ProductInfo> check_id(List<ProductInfo> products, Map<String, String> new_ids) {
        for(ProductInfo product : products) {
            if (product.id.startsWith("c")) {
                for (Map.Entry<String, String> entry : new_ids.entrySet()) {
                    if (entry.getKey().equals(product.id)) {
                        product.id = entry.getValue();
                    }
                }
            }
        }
        return  products;
    }

    public void synchronize() {
        final OutPutter op = new OutPutter(this);
        final Connection connection = new Connection(this);

        Response.Listener<JSONObject> response_listener =
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.w("Response:", response.getString("response"));
                            Map<String, String> new_ids = new HashMap<>();
                            JSONArray j_array = (JSONArray)response.get("result");
                            for(int i = 0; i < j_array.length(); i++) {
                                JSONObject obj = (JSONObject)j_array.get(i);
                                String client_id = obj.getString("c_id");
                                String server_id = obj.getString("s_id");
                                new_ids.put(client_id, server_id);
                            }
                            List<ProductInfo> removed = check_id(op.read_removal(), new_ids);
                            List<ProductInfo> updated = check_id(op.read_update(), new_ids);
                            List<ProductInfo> increased = check_id(op.read_increase(), new_ids);
                            List<ProductInfo> decreased = check_id(op.read_decrease(), new_ids);
                            connection.update(updated);
                            connection.increase(increased);
                            connection.decrease(decreased);
                            connection.remove(removed);

                            op.flush_all_but_base();

                            List<ProductInfo> product_infos = new ArrayList<>();
                            connection.getInfo(product_infos);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        update();
                    }
                };
        connection.create(op.read_creation(), response_listener);
    }

    public void update() {
        productTable = new ProductTable(this);
        OutPutter op = new OutPutter(this);
        List<ProductInfo> base = op.read_base();
        List<ProductInfo> created = op.read_creation();
        List<ProductInfo> removed = op.read_removal();
        List<ProductInfo> changed = op.read_update();
        List<ProductInfo> increased = op.read_increase();
        List<ProductInfo> decreased = op.read_decrease();

        Jointer jointer = new Jointer();
        jointer.join(base, created);
        jointer.join(base, changed);
        jointer.join(base, increased);
        jointer.join(base, decreased);
        jointer.join(base, removed);

        productTable.update(base);
    }

    public void addProduct(View view) {
        Intent intent = new Intent(getApplicationContext(), ProductActivity.class);
        intent.putExtra("change", Change.ADD);
        startActivity(intent);
    }

    public void removeProduct(View view) {
        ProductInfo product = productTable.getProductBySelectedRow();
        if(product == null) {
            Toast.makeText(getApplicationContext(), "Choose item to remove by holding tap.", Toast.LENGTH_SHORT).show();
        }
        else {
            product.markRemoved();
//            Connection connection = new Connection(this);
//            connection.remove(id);
            OutPutter op = new OutPutter(this);
            op.write(product);
            update();
//            Toast.makeText(getApplicationContext(), created, Toast.LENGTH_SHORT).show();
        }
    }

}