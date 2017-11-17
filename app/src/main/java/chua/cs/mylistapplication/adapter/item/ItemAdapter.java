package chua.cs.mylistapplication.adapter.item;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import chua.cs.mylistapplication.R;
import chua.cs.mylistapplication.adapter.DragSwipeItemTouchHelper;
import chua.cs.mylistapplication.adapter.PaginationRecyclerViewAdapter;
import chua.cs.mylistapplication.model.remote.Item;
import chua.cs.mylistapplication.view.ItemRecyclerViewHolder;
import chua.cs.mylistapplication.view.RecyclerViewHolder;

/**
 * Created by christopherchua on 11/15/17.
 */

public class ItemAdapter extends PaginationRecyclerViewAdapter<ItemRecyclerViewHolder, Item>
        implements RecyclerViewHolder.ItemClickListener, DragSwipeItemTouchHelper.ItemTouchListener,
        ItemFilter.ItemFilterTransaction {

    private List<Item> items;
    private List<Item> filteredList;

    private OnItemListener itemListener;
    private ItemFilter filter;

    public ItemAdapter(final Context context) {
        super(context);
        items = new ArrayList<>();
        if (context instanceof OnItemListener) {
            itemListener = (OnItemListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + context.getString(R.string.error_no_onitemlistener_class));
        }
    }

    private Item getItem(final int position) {
        if (filteredList == null) {
            return items.get(position);
        } else {
            return filteredList.get(position);
        }
    }

    public void setList(final List<Item> items) {
        this.items = items;
    }

    /**
     *
     * Below are Overriden methods from RecyclerView
     *
     */

    @Override
    public void onBindViewHolder(ItemRecyclerViewHolder holder, int position) {
        if (TYPE_ITEM == getItemViewType(position)) {
            final Item item = getItem(position);
            holder.updateId(Integer.toString(item.getId()));
            holder.updateName(item.getName());
            holder.setItemClickListener(this);
        }
    }

    @Override
    public int getItemCount() {
        if (filteredList == null) {
            return items.size();
        }
        return filteredList.size();
    }

    /**
     *
     * Below are Overriden methods from PaginationRecyclerViewAdapter
     *
     */

    @Override
    protected View getInflatedView(final LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(R.layout.item_layout, parent, false);
    }

    @Override
    protected ItemRecyclerViewHolder getViewHolder(final View itemView) {
        return new ItemRecyclerViewHolder(itemView);
    }

    @Override
    protected void addAll(List<Item> itemList) {
        if (itemList == null) {
            return;
        }
        items.addAll(itemList);
    }

    @Override
    protected void add(Item item) {
        items.add(item);
    }

    @Override
    public void remove(final int position) {
        items.remove(position);
    }

    @Override
    public void onDestroy() {
        itemListener = null;
        if (filter != null) {
            filter.onDestroy();
        }
        filter = null;
        super.onDestroy();
    }

    /**
     *
     * Below are Overriden methods from RecyclerViewHolder.ItemClickListener
     *
     */

    @Override
    public void onViewHolderClick(int position) {
        itemListener.onItemClick(getItem(position));
    }

    /**
     *
     * Below are Overriden methods from DragSwipeItemTouchHelper.ItemTouchListener
     *
     */

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(items, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemRemove(int position) {
        remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onItemSelected(RecyclerView.ViewHolder viewHolder) {
        viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
    }

    @Override
    public void onItemClear(RecyclerView.ViewHolder viewHolder) {
        viewHolder.itemView.setBackgroundColor(0);
    }

    /**
     *
     * Below are Overriden methods from ItemFilter.ItemFilterTransaction
     *
     */

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new ItemFilter(this);
        }
        return filter;
    }

    @Override
    public List<Item> getItemList() {
        final List<Item> items = new ArrayList<>(this.items);
        if (isLoadingAdded()) {
            items.remove(items.size()-1);
        }
        return items;
    }

    @Override
    public void onFilterUpdate(final List<Item> filteredList) {
        this.filteredList = filteredList;
        notifyDataSetChanged();
    }

    public interface OnItemListener {
        void onItemClick(final Item item);
    }
}