package chua.cs.mylistapplication.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import chua.cs.mylistapplication.R;
import chua.cs.mylistapplication.model.remote.Item;

/**
 * Created by christopherchua on 11/15/17.
 */

public class ItemDetailActivity extends AppCompatActivity {
    public final static String ARGS_ITEM = "argsItem";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_detail_layout);

        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            final Item item = extras.getParcelable(ARGS_ITEM);
            final TextView idTextView = findViewById(R.id.id_textview);
            final TextView nameTextView = findViewById(R.id.name_textview);
            idTextView.setText(Integer.toString(item.getId()));
            nameTextView.setText(item.getName());
        } else {
            throw new IllegalArgumentException(getString(R.string.error_no_extra_item_arg) +
                    ItemDetailActivity.class.getSimpleName());
        }
    }
}
