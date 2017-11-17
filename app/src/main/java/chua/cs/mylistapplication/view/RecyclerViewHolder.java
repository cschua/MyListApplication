package chua.cs.mylistapplication.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by christopherchua on 11/15/17.
 */

public abstract class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ItemClickListener itemClickListener;

    public RecyclerViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        if (itemClickListener != null) {
            itemClickListener.onViewHolderClick(getAdapterPosition());
        }
    }

    public interface ItemClickListener {
        void onViewHolderClick(int position);
    }
}
