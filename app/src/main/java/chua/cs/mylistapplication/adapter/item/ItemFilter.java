package chua.cs.mylistapplication.adapter.item;

import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

import chua.cs.mylistapplication.model.remote.Item;

/**
 * Created by christopherchua on 11/15/17.
 * Used for filtering Items
 */

public class ItemFilter extends Filter {

    private ItemFilterTransaction itemFilterTransaction;

    public ItemFilter(final ItemFilterTransaction itemFilterTransaction) {
        this.itemFilterTransaction = itemFilterTransaction;
    }

    @Override
    protected FilterResults performFiltering(CharSequence query) {
        final FilterResults filterResults = new FilterResults();
        if (query != null && query.length() > 0) {
            final List<Item> tempList = new ArrayList<>();
            for (Item item : itemFilterTransaction.getItemList()) {
                if (isItemValid(item) && item.getName().contains(query.toString())) {
                    tempList.add(item);
                }
            }
            filterResults.count = tempList.size();
            filterResults.values = tempList;
        }
        return filterResults;
    }

    private boolean isItemValid(final Item item) {
        return item != null && item.getName() != null && item.getName().length() > 0;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        itemFilterTransaction.onFilterUpdate((List<Item>) results.values);
    }

    public void onDestroy() {
        itemFilterTransaction = null;
    }

    public interface ItemFilterTransaction extends Filterable {
        List<Item> getItemList();
        void onFilterUpdate(final List<Item> itemList);
    }
}
