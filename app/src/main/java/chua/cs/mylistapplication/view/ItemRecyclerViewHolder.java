package chua.cs.mylistapplication.view;

import android.view.View;
import android.widget.TextView;

import chua.cs.mylistapplication.R;

/**
 * Created by christopherchua on 11/15/17.
 */

public class ItemRecyclerViewHolder extends RecyclerViewHolder {
    private final TextView idTextView;
    private final TextView nameTextView;

    public ItemRecyclerViewHolder(final View itemView) {
        super(itemView);
        idTextView = itemView.findViewById(R.id.id_textview);
        nameTextView = itemView.findViewById(R.id.name_textview);
    }

    public void updateId(final String id) {
        idTextView.setText(id);
    }

    public void updateName(final String name) {
        nameTextView.setText(name);
    }
}
