package ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lehub.qraft.R;

import Interface.ItemClickListener;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ItemClickListener listener;

    public TextView productNameCart, productQuantityCart,productPriceCart, productTotalCart;
    public ImageView cartImage;

    public CartViewHolder(@NonNull  View itemView) {
        super(itemView);

        cartImage = itemView.findViewById(R.id.cartImage);
        productNameCart = itemView.findViewById(R.id.productNameCart);
        productQuantityCart = itemView.findViewById(R.id.productQuantityCart);
        productPriceCart = itemView.findViewById(R.id.productPriceCart);
        productTotalCart = itemView.findViewById(R.id.productTotalCart);
    }

    @Override
    public void onClick(View v) {

        listener.onClick(v,getAdapterPosition(),false);

    }

    public void setItemClickListener(ItemClickListener listener){

        this.listener = listener;
    }
}
