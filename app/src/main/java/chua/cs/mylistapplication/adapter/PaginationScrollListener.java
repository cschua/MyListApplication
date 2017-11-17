package chua.cs.mylistapplication.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by christopherchua on 11/15/17.
 * Used to support paginations in a RecyclerView.OnScrollListener
 */

public class PaginationScrollListener extends RecyclerView.OnScrollListener {

    private static final int PAGE_START = 1;
    private LinearLayoutManager layoutManager;
    private OnLoadMoreItems onLoadMoreItems;
    private int totalPagesToLoad = PAGE_START;
    private int pagesLoaded = PAGE_START;
    private boolean isLoading = false;
    private boolean isEnabled = true;

    public PaginationScrollListener(final LinearLayoutManager layoutManager,
                                    final OnLoadMoreItems onLoadMoreItems) {
        this.layoutManager = layoutManager;
        this.onLoadMoreItems = onLoadMoreItems;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (!isLoading && moreItemsToLoad()) {

            // current number of child views attached to the RecyclerView
            int visibleItemCount = layoutManager.getChildCount();

            //number of items in the adapter bound to the RecyclerView
            int totalItemCount = layoutManager.getItemCount();

            // some views that are not necessarily visible. Those views are ignored in findFirstVisibleItemPosition
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0) {
                isLoading = true;
                pagesLoaded += 1; //Increment page index to load the next one
                onLoadMoreItems.loadMoreItems(pagesLoaded);
            }
        }
    }

    public void setTotalPagesToLoad(final int totalPagesToLoad) {
        this.totalPagesToLoad = totalPagesToLoad;
    }

    public void updateIsLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }

    public boolean moreItemsToLoad() {
        return isEnabled && pagesLoaded < totalPagesToLoad;
    }

    public int getPagesLoaded() {
        return pagesLoaded;
    }

    public void setPagesLoaded(final int pagesLoaded) {
        this.pagesLoaded = pagesLoaded;
    }

    public void onDestroy() {
        layoutManager = null;
        onLoadMoreItems = null;
    }

    public void setIsEnabled(final boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public interface OnLoadMoreItems {
        void loadMoreItems(final int currentPage);
    }
}