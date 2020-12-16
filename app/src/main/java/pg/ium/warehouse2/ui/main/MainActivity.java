package pg.ium.warehouse2.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

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

    public void synchronize() {
        OutPutter op = new OutPutter(this);
        Connection connection = new Connection(this);
        connection.create(op.read_creation());
        connection.update(op.read_update());
        connection.increase(op.read_increase());
        connection.decrease(op.read_decrease());
        connection.remove(op.read_removal());

        op.flush_all_but_base();

        List<ProductInfo> product_infos = new ArrayList<>();
        connection.getInfo(product_infos);
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
//            Toast.makeText(getApplicationContext(), created, Toast.LENGTH_SHORT).show();
        }
    }

}