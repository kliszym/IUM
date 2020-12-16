package pg.ium.warehouse2.ui.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pg.ium.warehouse2.global.IumApplication;
import pg.ium.warehouse2.ui.product.ProductInfo;
import pg.ium.warehouse2.R;
import pg.ium.warehouse2.ui.login.Role;
import pg.ium.warehouse2.ui.product.Change;
import pg.ium.warehouse2.ui.product.ProductActivity;

public class ProductTable {

    public TableLayout table;
    private Context context;
    public List<ProductInfo> products = new ArrayList<>();
    private TableRow rowSelected = null;

    public ProductTable(Context context) {
        this.context = context;
        table = ((Activity)context).findViewById(R.id.table_product);
    }

    public void update(List<ProductInfo> products) {
        table.removeAllViews();
        this.products = products;
        create(products);
        table.invalidate();
    }

    private void create(List<ProductInfo> products) {
        this.products = products;

        int row_number = 0;
        for(ProductInfo product : products) {
            TableRow row = new TableRow(context);
            allowRowClick(row, context);

            TextView tv_parent;
            tv_parent = ((Activity)context).findViewById(R.id.tv_manufacturer);
            addCell(row, product.manufacturer, context, tv_parent, false);
            tv_parent = ((Activity)context).findViewById(R.id.tv_model);
            addCell(row, product.model, context, tv_parent, false);
            tv_parent = ((Activity)context).findViewById(R.id.tv_price);
            addCell(row, product.price.toString(), context, tv_parent, true);
            tv_parent = ((Activity)context).findViewById(R.id.tv_quantity);
            addCell(row, product.quantity.toString(), context, tv_parent, true);

            row.setTag(row_number);
            row_number++;
            table.addView(row);
        }
    }

    public void addCell(TableRow row, String content, Context context, TextView parentCell, boolean alignmentRight) {
        final TextView text = new TextView(context);

        text.setText(content);
        text.setTextSize(15);

        text.setLayoutParams(createCellLayoutParams(parentCell));
        text.setSingleLine(true);
        text.setEllipsize(TextUtils.TruncateAt.END);

        if(alignmentRight)
            text.setGravity(Gravity.END);

        row.addView(text);
    }

    LinearLayout.LayoutParams createCellLayoutParams(TextView parentCell) {
        LinearLayout.LayoutParams parent_params =
                (LinearLayout.LayoutParams) parentCell.getLayoutParams();

        TableRow.LayoutParams layout_params = new TableRow.LayoutParams(
                0,
                ViewGroup.LayoutParams.MATCH_PARENT,
                parent_params.weight);

        layout_params.setMargins(
                parent_params.leftMargin,
                0,
                parent_params.rightMargin,
                0
        );
        return layout_params;
    }

    public ProductInfo getProductBySelectedRow() {
        if(rowSelected == null)
            return null;
        int row_number = (int) rowSelected.getTag();
        return products.get(row_number);
    }

    public void allowRowClick(final TableRow row, final Context context) {
        row.setClickable(true);

        row.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(rowSelected == row) {
                    rowSelected = null;
                    row.setBackgroundColor(Color.WHITE);
                    row.invalidate();
                }
                else if (rowSelected != null) {
                    rowSelected.setBackgroundColor(Color.WHITE);
                    rowSelected = row;
                    row.setBackgroundColor(Color.GRAY);
                    row.invalidate();
                }
                else {
                    int row_number = (int) v.getTag();
                    Intent intent = new Intent(context.getApplicationContext(), ProductActivity.class);
                    intent.putExtra("change", Change.UPDATE);
                    intent.putExtra("product", products.get(row_number).json().toString());
                    context.startActivity(intent);
                }
            }
        });

        row.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                IumApplication app = (IumApplication)context.getApplicationContext();
                if(app.getUser().role == Role.MASTER) {
                    if (rowSelected == null) {
                        rowSelected = row;
                        row.setBackgroundColor(Color.GRAY);
                        row.invalidate();
                    }
                }
                return true;
            }
        });
    }

}
