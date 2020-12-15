package pg.ium.warehouse2.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import pg.ium.warehouse2.R;
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
        update();
    }

    public void synchronize() {
        OutPutter op = new OutPutter(this);
        op.flush_all();
    }

    public void update() {
        Connection connection = new Connection(this);
        productTable = new ProductTable(this);
        connection.getInfo(productTable);
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
//            Toast.makeText(getApplicationContext(), created, Toast.LENGTH_SHORT).show();
        }
    }

}