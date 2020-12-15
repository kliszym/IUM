package pg.ium.warehouse2.ui.product;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import pg.ium.warehouse2.R;
import pg.ium.warehouse2.data.OutPutter;
import pg.ium.warehouse2.network.Connection;

public class ProductActivity extends AppCompatActivity {

    private ProductInfo product;
    private Change change;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        Intent intent = getIntent();
        change = (Change)intent.getSerializableExtra("change");
        if(change == Change.UPDATE) {
            product = new ProductInfo(
                    intent.getStringExtra("id"),
                    intent.getStringExtra("manufacturer"),
                    intent.getStringExtra("model"),
                    intent.getDoubleExtra("price", 0.0),
                    intent.getIntExtra("quantity", 0)
            );

            id = product.id;

            TextView tv;
            tv = (TextView) findViewById(R.id.editText_manufacturer);
            tv.setText(product.manufacturer);
            tv = (TextView) findViewById(R.id.editText_model);
            tv.setText(product.model);
            tv = (TextView) findViewById(R.id.editText_price);
            tv.setText(product.price.toString());
            tv = (TextView) findViewById(R.id.tv_quantity_value);
            tv.setText(product.quantity.toString());
        }
        else {
            OutPutter op = new OutPutter(getApplicationContext());
            id = "client" + op.read_counter().toString();
            Button btn;
            btn = (Button) findViewById(R.id.button_quantity_plus);
            btn.setVisibility(View.GONE);
            btn = (Button) findViewById(R.id.button_quantity_minus);
            btn.setVisibility(View.GONE);

            TextView tv;
            tv = (TextView) findViewById(R.id.tv_quantity);
            tv.setVisibility(View.GONE);
            tv = (TextView) findViewById(R.id.tv_quantity_value);
            tv.setVisibility(View.GONE);
        }

        EditText et;
        et = (EditText) findViewById(R.id.editText_price);
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //nothing to do here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //nothing to do here
            }

            @Override
            public void afterTextChanged(Editable s) {
                //care about empty string
                if (s.length() > 4) {
                    if (s.charAt(s.length() - 4) == '.') {
                        s.delete(s.length() - 1, s.length());
                    }
                }
            }
        });
    }

    public void showQuantityProductChangeActivityAdd(View view) {
        activateProductChangeActivity(Operation.ADD);
    }

    public void showQuantityProductChangeActivitySubstract(View view) {
        activateProductChangeActivity(Operation.SUBTRACT);
    }

    public void activateProductChangeActivity(Operation operation) {
        Intent intent = new Intent(getApplicationContext(), ProductChangeActivity.class);
        intent.putExtra("id", product.id);
        intent.putExtra("operation", operation);
        intent.putExtra("value", product.quantity.toString());
        this.startActivity(intent);
    }

    public void saveChanges(View view) {
        String manufacturer = ((TextView) findViewById(R.id.editText_manufacturer)).getText().toString().trim();
        String model = ((TextView) findViewById(R.id.editText_model)).getText().toString().trim();
        String price_string = ((TextView) findViewById(R.id.editText_price)).getText().toString().trim();

        if(manufacturer.isEmpty() || model.isEmpty() || price_string.isEmpty()) {
            Toast.makeText(this, "Any field can not be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.valueOf(price_string);
        product = new ProductInfo(id, manufacturer, model, price);

        Connection connection = new Connection(this);
        OutPutter op = new OutPutter(getApplicationContext());
        op.write(product);

        if(change == Change.UPDATE) {
//            connection.update(product);
        }
        else {
//            connection.create(product);
            op.increase_counter();
        }

        onBackPressed();
    }
}
