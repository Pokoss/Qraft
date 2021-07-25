package ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lehub.qraft.R;

import Interface.ItemClickListener;

public class Catergory1ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView textViewCategory1;
    public ItemClickListener listener;

    public Catergory1ViewHolder(@NonNull View itemView) {
        super(itemView);

        textViewCategory1 = (TextView) itemView.findViewById(R.id.textViewCategory1);

    }

    public void setItemClickListener(ItemClickListener listener){

        this.listener = listener;
    }

    @Override
    public void onClick(View v) {

        listener.onClick(v,getAdapterPosition(),false);

    }
}
