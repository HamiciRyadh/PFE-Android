package usthb.lfbservices.com.pfe.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.network.PfeRx;
import usthb.lfbservices.com.pfe.roomDatabase.AppRoomDatabase;
import usthb.lfbservices.com.pfe.activities.DescSalesPointActivity;
import usthb.lfbservices.com.pfe.models.ProductSalesPoint;
import usthb.lfbservices.com.pfe.utils.Utils;

import static android.content.Context.MODE_PRIVATE;


public class ProductSalesPointListAdapter extends RecyclerView.Adapter<ProductSalesPointListAdapter.ViewHolder> implements ITouchHelperAdapter {

    private static final String TAG = "PSPListAdapter";
    private Context context;
    private AppRoomDatabase db;
    private View rootView;

    private List<ProductSalesPoint> productSalesPoints;

    public ProductSalesPointListAdapter (List<ProductSalesPoint> productSalesPoints) {
        this.productSalesPoints = productSalesPoints;
    }

    @Override
    public  ProductSalesPointListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_sales_point_product, parent, false);
        return new ViewHolder(rootView);
    }


    @Override
    public void onBindViewHolder(@NonNull ProductSalesPointListAdapter.ViewHolder holder, int position) {
        db = AppRoomDatabase.getInstance(ProductSalesPointListAdapter.this.context);
        String salespointId = productSalesPoints.get(position).getSalesPointId();
        String salespointName = db.salesPointDao().getSalesPointNameById(salespointId);
        holder.salesPointid.setText(salespointName);
        holder.price.setText(String.format("%.2f DA", productSalesPoints.get(position).getProductPrice()));
        holder.quantity.setText(String.valueOf(productSalesPoints.get(position).getProductQuantity()));
    }

    @Override
    public int getItemCount() {
            return productSalesPoints.size();
            }

    @Override
    public void onItemDismiss(int position, int direction) {
        final ProductSalesPoint productSalesPoint = productSalesPoints.get(position);

        if (direction == ItemTouchHelper.START) {
            if (Utils.isNetworkAvailable(context)) {
                if (!Utils.isUserConnected(context)) {
                    Utils.showConnectDialog(context);
                } else {
                    PfeRx.addToNotificationsList(productSalesPoint.getSalesPointId(), productSalesPoint.getProductBarcode());
                    Snackbar.make(rootView,rootView.getResources().getString(R.string.notification_added), Snackbar.LENGTH_LONG)
                            .setAction(context.getResources().getString(R.string.cancel), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    PfeRx.removeFromNotificationsList(productSalesPoint.getSalesPointId(), productSalesPoint.getProductBarcode());

                                }
                            })
                            .setActionTextColor(context.getResources().getColor(R.color.colorPrimary))
                            .show();
                }
            } else {
                Toast.makeText(context, context.getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
            }
        } else if (direction == ItemTouchHelper.END) {
            db = AppRoomDatabase.getInstance(ProductSalesPointListAdapter.this.context);
            String salespointID = productSalesPoints.get(position).getSalesPointId();
            String productBarcode = productSalesPoints.get(position).getProductBarcode();
            db.productSalesPointDao().deleteById(productBarcode, salespointID);
            productSalesPoints.remove(position);
            notifyItemRemoved(position);
            PfeRx.removeFromNotificationsList(productSalesPoint.getSalesPointId(), productSalesPoint.getProductBarcode());
        }
    }

    @Override
    public void onItemDismiss(int position) {

    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(productSalesPoints, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(productSalesPoints, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView salesPointid;
        public TextView quantity;
        public TextView price;
        public RelativeLayout viewBackground, viewForeground;

        private String sSalesPointId;

        public ViewHolder(final View itemView) {
            super(itemView);
            context = itemView.getContext();
            salesPointid = itemView.findViewById(R.id.sales_point_name_details);
            price = itemView.findViewById(R.id.product_price_marker);
            quantity = itemView.findViewById(R.id.product_qte_marker);

            viewBackground = itemView.findViewById(R.id.psp_view_background);
            viewForeground = itemView.findViewById(R.id.psp_view_foreground);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            sSalesPointId = productSalesPoints.get(getLayoutPosition()).getSalesPointId();
            Intent intent = new Intent(context, DescSalesPointActivity.class);
            intent.putExtra("salesPointID", sSalesPointId);
            intent.putExtra("productQuantity", productSalesPoints.get(getLayoutPosition()).getProductQuantity());
            intent.putExtra("productPrice", productSalesPoints.get(getLayoutPosition()).getProductPrice());
            context.startActivity(intent);
        }
    }
}

