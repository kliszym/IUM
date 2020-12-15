package pg.ium.warehouse2.ui.product;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import pg.ium.warehouse2.data.OutPutter;

public class ProductInfo {
    public enum ProductState {
        UNCHANGED,
        CHANGED,
        NEW,
        REMOVED,
        SUBTRACTED,
        ADDED
    }

    public String id;
    public String manufacturer;
    public String model;
    public Double price;
    public Integer quantity;

    private ProductState product_state;

    public ProductInfo(String id, String manufacturer, String model, Double price, int quantity) {
        this.id = id;
        this.manufacturer = manufacturer;
        this.model = model;
        this.price = price;
        this.quantity = quantity;

        this.product_state = ProductState.UNCHANGED;
    }

    public ProductInfo(String id, String manufacturer, String model, Double price) {
        this.id = id;
        this.manufacturer = manufacturer;
        this.model = model;
        this.price = price;

        this.product_state = ProductState.CHANGED;
    }

    public ProductInfo(Context context, String manufacturer, String model, Double price) {
        OutPutter op = new OutPutter(context);
        this.id = "c" + op.read_counter().toString();
        this.manufacturer = manufacturer;
        this.model = model;
        this.price = price;

        this.product_state = ProductState.NEW;
    }

    public ProductInfo(String id) {
        this.id = id;

        this.product_state = ProductState.REMOVED;
    }

    public ProductInfo(String id, Operation operation, Integer value) {
        this.id = id;
        this.quantity = value;

        if(operation == Operation.ADD)
            this.product_state = ProductState.ADDED;
        else
            this.product_state = ProductState.SUBTRACTED;
    }

    public ProductInfo(Context context, JSONObject obj, ProductState product_state) {
        try {
            switch(product_state) {
                case UNCHANGED:
                    id = obj.getString("obj_id");
                    manufacturer = obj.getString("manufacturer");
                    model = obj.getString("model");
                    price = obj.getDouble("price");
                    quantity = obj.getInt("quantity");
                    break;
                case CHANGED:
                case NEW:
                    id = obj.getString("obj_id");
                    manufacturer = obj.getString("manufacturer");
                    model = obj.getString("model");
                    price = obj.getDouble("price");
                    break;
                case REMOVED:
                    id = obj.getString("obj_id");
                    break;
                case ADDED:
                case SUBTRACTED:
                    id = obj.getString("obj_id");
                    quantity = obj.getInt("quantity");
                    if(obj.getString("operation").equals("+"))
                        product_state = ProductState.ADDED;
                    else
                        product_state = ProductState.SUBTRACTED;
                    break;
            }
            this.product_state = product_state;
        } catch(JSONException e) {
            Toast.makeText(context, "Json to object failed.", Toast.LENGTH_SHORT).show();
        }
    }

    public ProductState getProduct_state() {
        return product_state;
    }
    public void markRemoved() { this.product_state = ProductState.REMOVED; }

    public JSONObject json() {
        JSONObject obj = new JSONObject();
        try {
            switch(this.product_state) {
                case UNCHANGED:
                    obj.put("obj_id", id);
                    obj.put("manufacturer", manufacturer);
                    obj.put("model", model);
                    obj.put("price", price);
                    obj.put("quantity", quantity);
                    break;
                case CHANGED:
                case NEW:
                    obj.put("obj_id", id);
                    obj.put("manufacturer", manufacturer);
                    obj.put("model", model);
                    obj.put("price", price);
                    break;
                case REMOVED:
                    obj.put("obj_id", id);
                    break;
                case ADDED:
                    obj.put("obj_id", id);
                    obj.put("operation", "+");
                    obj.put("quantity", quantity);
                    break;
                case SUBTRACTED:
                    obj.put("obj_id", id);
                    obj.put("operation", "-");
                    obj.put("quantity", quantity);
                    break;
                default:
                    //
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
