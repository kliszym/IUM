package pg.ium.warehouse2.data;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import pg.ium.warehouse2.ui.product.ProductInfo;

public class OutPutter {

    private Context context;

    private String BASE = "base";
    private String CREATION = "creation";
    private String COUNTER = "counter";
    private String REMOVAL = "removal";
    private String UPDATE = "update";
    private String INCREASE = "increase";
    private String DECREASE = "decrease";

    public OutPutter(Context context) {
        this.context = context;
    }

    private List<ProductInfo> read_objects(String filename, ProductInfo.ProductState product_state) {
        List<ProductInfo> product_infos = new ArrayList<>();
        File file = new File(context.getFilesDir(), filename);
        if(file.exists()) {
            try {
                InputStream is = context.openFileInput(filename);
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                while (true) {
                    String s = br.readLine();
                    if (s == null)
                        break;
                    else
                        s = s.trim();
                    product_infos.add(new ProductInfo(context, new JSONObject(s), product_state));
                }
                br.close();
            } catch (IOException | JSONException io_e) {
                Toast.makeText(context, "File Storage Error (" + filename + ").", Toast.LENGTH_SHORT).show();
            }
        }
        return product_infos;
    }

    private void write(String filename, String content) {
        try {
            OutputStream os = context.openFileOutput(filename, Context.MODE_APPEND);
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);
            bw.write(content);
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            Toast.makeText(context, "File Storage Error (" + filename +").", Toast.LENGTH_SHORT).show();
        }
    }

    private void flush(String filename) {
        File f = new File(context.getFilesDir(), filename);
        f.delete();
    }

// COUNTER
    public Integer read_counter() {
        int created_products = 0;
        try {
            try {
                InputStream is = context.openFileInput(COUNTER);
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String s = br.readLine().trim();
                created_products = Integer.parseInt(s);
                br.close();
            } catch (FileNotFoundException e) {
                OutputStream os = context.openFileOutput(COUNTER, Context.MODE_PRIVATE);
                OutputStreamWriter osw = new OutputStreamWriter(os);
                BufferedWriter bw = new BufferedWriter(osw);
                created_products = 0;
                bw.write(Integer.toString(created_products));
                bw.close();
            }
        }
        catch (IOException io_e) {
            Toast.makeText(context, "File Storage Error (Counter).", Toast.LENGTH_SHORT).show();
        }
        return created_products;
    }

    private void write_counter(Integer created_products) {
        try {
            OutputStream os = context.openFileOutput(COUNTER, Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);
            bw.write(created_products.toString());
            bw.close();
        } catch (IOException e) {
            Toast.makeText(context, "File Storage Error (Counter).", Toast.LENGTH_SHORT).show();
        }
    }

    public void increase_counter() {
        int counter = read_counter();
        counter++;
        write_counter(counter);
    }

    public List<ProductInfo> read_base() {
        return read_objects(BASE, ProductInfo.ProductState.UNCHANGED);
    }

    public List<ProductInfo> read_creation() {
        return read_objects(CREATION, ProductInfo.ProductState.NEW);
    }

    public List<ProductInfo> read_removal() {
        return read_objects(REMOVAL, ProductInfo.ProductState.REMOVED);
    }

    public List<ProductInfo> read_update() {
        return read_objects(UPDATE, ProductInfo.ProductState.CHANGED);
    }

    public List<ProductInfo> read_increase() {
        return read_objects(INCREASE, ProductInfo.ProductState.ADDED);
    }

    public List<ProductInfo> read_decrease() {
        return read_objects(DECREASE, ProductInfo.ProductState.SUBTRACTED);
    }

    public void write(ProductInfo product) {
        String content = product.json().toString();

        switch(product.getProduct_state()) {
            case UNCHANGED:
                write(BASE, content);
                break;
            case CHANGED:
                write(UPDATE, content);
                break;
            case NEW:
                write(CREATION, content);
                break;
            case REMOVED:
                write(REMOVAL, content);
                break;
            case SUBTRACTED:
                write(DECREASE, content);
                break;
            case ADDED:
                write(INCREASE, content);
                break;
        }
    }

    // FLUSH
    public void flush_all_but_base() {
        flush_counter();
        flush_creation();
        flush_removal();
        flush_UPDATE();
        flush_increase();
        flush_decrease();
    }

    public void flush_base() {
        flush(BASE);
    }

    public void flush_counter() {
        flush(COUNTER);
    }

    public void flush_creation() {
        flush(CREATION);
    }

    public void flush_removal() {
        flush(REMOVAL);
    }

    public void flush_UPDATE() {
        flush(UPDATE);
    }

    public void flush_increase() {
        flush(INCREASE);
    }

    public void flush_decrease() {
        flush(DECREASE);
    }

}
