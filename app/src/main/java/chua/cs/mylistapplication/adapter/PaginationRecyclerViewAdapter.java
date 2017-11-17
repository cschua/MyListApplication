package chua.cs.mylistapplication.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import chua.cs.mylistapplication.R;
import chua.cs.mylistapplication.view.RecyclerViewHolder;

/**
 * Created by christopherchua on 11/15/17.
 * Used to support pagination for RecyclerView.Adapter
 */

public abstract class PaginationRecyclerViewAdapter<V extends RecyclerViewHolder, T> extends RecyclerView.Adapter<V> {

    public final static int TYPE_ITEM = 0;
    private final static int TYPE_PROGRESS_LOADING = 1;
    private LayoutInflater inflater;
    private PaginationScrollListener scrollListener;
    private boolean isLoadingAdded;

    public PaginationRecyclerViewAdapter(final Context context) {
        inflater = LayoutInflater.from(context);
    }

    protected abstract View getInflatedView(final LayoutInflater inflater, final ViewGroup parent);

    protected abstract V getViewHolder(final View itemView);

    protected abstract void addAll(List<T> t);

    protected abstract void add(T t);

    protected abstract void remove(final int position);

    @Override
    public V onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView;
        if (TYPE_ITEM == viewType) { // default behavior, it's an item view
            itemView = getInflatedView(inflater, parent);
        } else { // otherwise it's the progress view (should be the last item)
            itemView = inflater.inflate(R.layout.item_progress_layout, parent, false);
        }
        return getViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        // TYPE_PROGRESS_LOADING is the last one if there are more items to load
        boolean isLoading = position == getItemCount() - 1 && isLoadingAdded();
        return isLoading ? TYPE_PROGRESS_LOADING : TYPE_ITEM;
    }

    public boolean isLoadingAdded() {
        return isLoadingAdded;
    }

    public void setScrollListener(final PaginationScrollListener onScrollListener) {
        this.scrollListener = onScrollListener;
    }

    public void updateList(final List<T> items) {
        if (scrollListener != null) {
            // reset the loading state of pagination
            scrollListener.updateIsLoading(false);
        }

        if (getItemCount() > 0) {
            // if we have items in the list, we need to make sure to remove the progress first
            removeLoadingView();
        }
        addAll(items);

        // we only support pagination if a listener is provided and there are more items to load
        if (scrollListener != null) {
            addLoadingView();
        } else {
            removeLoadingView();
        }
    }

    // add a progress item at the end of the list
    public void addLoadingView() {
        if (!isLoadingAdded && scrollListener.moreItemsToLoad()) {
            add(null);
            isLoadingAdded = true;
            notifyDataSetChanged();
        }
    }

    // remove the progress item (it's the last one in the list)
    public void removeLoadingView() {
        if (isLoadingAdded) {
            isLoadingAdded = false;
            int position = getItemCount() - 1;
            if (position >= 0) {
                remove(position);
                notifyItemRemoved(position);
            }
        }
    }

    public void onDestroy() {
        inflater = null;
        scrollListener = null;
    }
}