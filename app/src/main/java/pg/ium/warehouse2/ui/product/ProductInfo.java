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

    private String id_json = "obj_id";
    private String manufacturer_json = "manufacturer";
    private String model_json = "model";
    private String price_json = "price";
    private String quantity_json = "quantity";

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
                    id = obj.getString(id_json);
                    manufacturer = obj.getString(manufacturer_json);
                    model = obj.getString(model_json);
                    price = obj.getDouble(price_json);
                    quantity = obj.getInt(quantity_json);
                    break;
                case CHANGED:
                case NEW:
                    id = obj.getString(id_json);
                    manufacturer = obj.getString(manufacturer_json);
                    model = obj.getString(model_json);
                    price = obj.getDouble(price_json);
                    break;
                case REMOVED:
                    id = obj.getString(id_json);
                    break;
                case ADDED:
                case SUBTRACTED:
                    id = obj.getString(id_json);
                    quantity = obj.getInt(quantity_json);
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
    public void markQuantityAdded() { this.product_state = ProductState.ADDED; }
    public void markQuantitySubtracted() { this.product_state = ProductState.SUBTRACTED; }
    public void markUnchanged() { this.product_state = ProductState.UNCHANGED; }
    public void markChanged() { this.product_state = ProductState.CHANGED; }
    public void markNew() { this.product_state = ProductState.NEW; }

    public JSONObject json() {
        JSONObject obj = new JSONObject();
        try {
            switch(this.product_state) {
                case UNCHANGED:
                    obj.put(id_json, id);
                    obj.put(manufacturer_json, manufacturer);
                    obj.put(model_json, model);
                    obj.put(price_json, price);
                    obj.put(quantity_json, quantity);
                    break;
                case CHANGED:
                case NEW:
                    obj.put(id_json, id);
                    obj.put(manufacturer_json, manufacturer);
                    obj.put(model_json, model);
                    obj.put(price_json, price);
                    break;
                case REMOVED:
                    obj.put(id_json, id);
                    break;
                case ADDED:
                    obj.put(id_json, id);
                    obj.put("operation", "+");
                    obj.put(quantity_json, quantity);
                    break;
                case SUBTRACTED:
                    obj.put(id_json, id);
                    obj.put("operation", "-");
                    obj.put(quantity_json, quantity);
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
