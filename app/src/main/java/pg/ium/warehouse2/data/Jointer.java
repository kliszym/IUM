package pg.ium.warehouse2.data;

import android.util.Log;

import java.util.List;

import pg.ium.warehouse2.ui.product.ProductInfo;

public class Jointer {
    public Jointer() {

    }

    public void join(List<ProductInfo> base, List<ProductInfo> editor) {
        for(ProductInfo pi : editor) {
            Log.w("editor", pi.id + " " + pi.getProduct_state().toString());
            switch (pi.getProduct_state()) {
                case NEW:
                    pi.quantity = 0;
                    pi.markUnchanged();
                    base.add(pi);
                    break;
                case REMOVED:
                    for(int i = 0; i < base.size(); i++) {
                        if(base.get(i).id.equals(pi.id)) {
                            base.remove(i);
                        }
                    }
                    break;
                case CHANGED:
                    for(int i = 0; i < base.size(); i++) {
                        if (base.get(i).id.equals(pi.id)) {
                            pi.quantity = base.get(i).quantity;
                            pi.markUnchanged();
                            base.set(i, pi);
                        }
                    }
                    break;
                case ADDED:
                    for(int i = 0; i < base.size(); i++) {
                        if (base.get(i).id.equals(pi.id)) {
                            base.get(i).quantity += pi.quantity;
                        }
                    }
                    break;
                case SUBTRACTED:
                    for(int i = 0; i < base.size(); i++) {
                        if (base.get(i).id.equals(pi.id)) {
                            base.get(i).quantity -= pi.quantity;
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
