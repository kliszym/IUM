package pg.ium.warehouse2.data;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import pg.ium.warehouse2.ui.product.ProductInfo;

public class OutPutter {

    private Context context;

    private String CREATION = "creation";
    private String COUNTER = "counter";
    private String REMOVAL = "removal";
    private String UPDATE_PRODUCT = "update_product";
    private String UPDATE_QUANTITY = "update_quantity";

    public OutPutter(Context context) {
        this.context = context;
    }

//    private read(String filename) {
//
//    }

    private void write(String filename, String content) {
        try {
            OutputStream os = context.openFileOutput(CREATION, Context.MODE_APPEND);
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);
            bw.write(content);
            bw.newLine();
            bw.close();
        } catch (FileNotFoundException e) {
            Toast.makeText(context, "File Existence Error (" + filename + ").", Toast.LENGTH_SHORT).show();
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
            } catch (FileNotFoundException e1) {
                try {
                    OutputStream os = context.openFileOutput(COUNTER, Context.MODE_PRIVATE);
                    OutputStreamWriter osw = new OutputStreamWriter(os);
                    BufferedWriter bw = new BufferedWriter(osw);
                    created_products = 0;
                    bw.write(Integer.toString(created_products));
                    bw.close();
                } catch (FileNotFoundException e2) {
                    Toast.makeText(context, "File Existence Error (Counter).", Toast.LENGTH_SHORT).show();
                }
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
        } catch (FileNotFoundException e) {
            Toast.makeText(context, "File Existence Error (Counter).", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(context, "File Storage Error (Counter).", Toast.LENGTH_SHORT).show();
        }
    }

    public void increase_counter() {
        int counter = read_counter();
        counter++;
        write_counter(counter);
    }

// CREATION

    public void write(ProductInfo product) {
        String content = product.json().toString();

        switch(product.getProduct_state()) {
            case UNCHANGED:
                //TODO:
                break;
            case CHANGED:
                write(UPDATE_PRODUCT, content);
                break;
            case NEW:
                write(CREATION, content);
                break;
            case REMOVED:
                write(REMOVAL, content);
                break;
            case SUBTRACTED:
            case ADDED:
                write(UPDATE_QUANTITY, content);
                break;
        }
    }

    // FLUSH
    public void flush_all() {
        flush_counter();
        flush_creation();
        flush_removal();
        flush_update_product();
        flush_update_quantity();
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

    public void flush_update_product() {
        flush(UPDATE_PRODUCT);
    }

    public void flush_update_quantity() {
        flush(UPDATE_QUANTITY);
    }
}
