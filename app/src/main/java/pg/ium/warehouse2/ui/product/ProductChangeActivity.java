package pg.ium.warehouse2.ui.product;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import pg.ium.warehouse2.R;
import pg.ium.warehouse2.data.OutPutter;
import pg.ium.warehouse2.network.Connection;
import pg.ium.warehouse2.ui.main.MainActivity;

public class ProductChangeActivity extends AppCompatActivity {

    private String value;
    private Operation operation;
    private int product_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        product_id = intent.getIntExtra("id", 1);
        operation = (Operation)intent.getSerializableExtra("operation");
        value = intent.getStringExtra("value");

        setContentView(R.layout.activity_product_change);

        TextView tv;
        tv = findViewById(R.id.tv_value_start);
        tv.setText(value);
        tv = findViewById(R.id.tv_operation);
        tv.setText(operation.toString());
        tv = findViewById(R.id.tv_value_final);
        tv.setText(value);

        EditText et;
        et = findViewById(R.id.editTextNumberDecimal);
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
                if(s.length() > 0) {
                    //delete dot if its quantity
                    if (s.charAt(s.length() - 1) == '.') {
                        s.delete(s.length() - 1, s.length());
                    }

                    TextView tv_base = findViewById(R.id.tv_value_start);

                    int base = Integer.parseInt(tv_base.getText().toString());
                    int value = Integer.parseInt(s.toString());
                    int result;

                    if(operation == Operation.ADD)
                        result = base + value;
                    else
                        result = base - value;

                    TextView tv_result = findViewById(R.id.tv_value_final);
                    tv_result.setText(Integer.toString(result));
                }
                else {
                    TextView tv_base = findViewById(R.id.tv_value_start);
                    TextView tv_result = findViewById(R.id.tv_value_final);
                    tv_result.setText(tv_base.getText());
                }
            }
        });
    }

    public void saveSingleChange(View view) {
        int value;
        String change_string = ((EditText)findViewById(R.id.editTextNumberDecimal)).getText().toString();

        if(!change_string.isEmpty()) {
            Log.w("value:", change_string);
            value = Integer.valueOf(change_string);

            if(value < 0) {
                Toast.makeText(this, "Result of operation can not be negative.", Toast.LENGTH_SHORT).show();
                return;
            }

            Connection connection = new Connection(this);
            Log.w("ID", Integer.toString(product_id));

            OutPutter op = new OutPutter(getApplicationContext());
            op.write(operation, value);
//            if(operation == Operation.ADD) {
//                connection.increase(product_id, value);
//            }
//            else {
//                connection.decrease(product_id, value);
//            }
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}