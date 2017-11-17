package chua.cs.mylistapplication.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import chua.cs.mylistapplication.R;
import chua.cs.mylistapplication.adapter.DragSwipeItemTouchHelper;
import chua.cs.mylistapplication.adapter.PaginationScrollListener;
import chua.cs.mylistapplication.adapter.item.ItemAdapter;
import chua.cs.mylistapplication.model.local.ApplicationModel;
import chua.cs.mylistapplication.model.local.PaginationCount;
import chua.cs.mylistapplication.model.remote.Item;
import chua.cs.mylistapplication.model.remote.Menu;
import chua.cs.mylistapplication.network.RetrofitClient;
import chua.cs.mylistapplication.network.RetrofitService;
import chua.cs.mylistapplication.util.NetworkUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by christopherchua on 11/15/17.
 */

public class MainActivity extends AppCompatActivity implements MenuItem.OnActionExpandListener,
        SearchView.OnQueryTextListener, ItemAdapter.OnItemListener,
        PaginationScrollListener.OnLoadMoreItems {

    public final static String ARGS_TOTAL_PAGE_LOADED = "argTotalPageLoaded";
    public final static String ARGS_SEARCH_QUERY = "argSearchQuery";

    private RetrofitService retrofitService;
    private Callback<Menu> retrofitCallback;
    private Call<Menu> retrofitCall;

    private RecyclerView itemRecyclerView;
    private ItemAdapter itemAdapter;
    private PaginationScrollListener paginationScrollListener;
    private SearchView searchView;
    private ItemTouchHelper itemTouchHelper;
    private View startupProgressBar;

    private int totalPagesLoaded = 1;
    private String searchQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startupProgressBar = findViewById(R.id.startup_progressbar);

        initItemRecyclerView();

        // server call to onFilterUpdate recyclerview
        retrofitService = RetrofitClient.getInstance().getClient().create(RetrofitService.class);

        // TODO list should be maintained in a database and sync with server
        final List<Item> itemList = ApplicationModel.getInstance().getItemList();
        if (itemList == null || itemList.size() == 0) {
            updateItemList(getTotalPagesLoaded(), true);
        } else {
            itemAdapter.setList(itemList);
            itemAdapter.notifyDataSetChanged();
        }
    }

    private void initItemRecyclerView() {
        // setup recyclerview
        itemRecyclerView = findViewById(R.id.items_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        itemRecyclerView.setLayoutManager(layoutManager);
        itemRecyclerView.setHasFixedSize(true);

        // recyclerview ItemDecoration
        RecyclerView.ItemDecoration mDividerItemDecoration = new DividerItemDecoration(this,
                LinearLayoutManager.VERTICAL);
        itemRecyclerView.addItemDecoration(mDividerItemDecoration);

        // TODO the server should maintain and provide PaginationCount values but for now hard code it
        final PaginationCount pageCount = ApplicationModel.getInstance().getPaginationCount();

        // recyclerview pagination
        paginationScrollListener = new PaginationScrollListener(layoutManager, this);
        paginationScrollListener.setTotalPagesToLoad(pageCount.getTotalPages());
        paginationScrollListener.setPagesLoaded(getTotalPagesLoaded());
        itemRecyclerView.addOnScrollListener(paginationScrollListener);

        // recyclerview adapter
        itemAdapter = new ItemAdapter(this);
        itemAdapter.setScrollListener(paginationScrollListener);
        itemRecyclerView.setAdapter(itemAdapter);

        // recylerview drag, drop, swipe
        ItemTouchHelper.Callback callback = new DragSwipeItemTouchHelper(itemAdapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(itemRecyclerView);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO Support orientation / configuration change
        ApplicationModel.getInstance().replaceItemList(itemAdapter.getItemList());
        outState.putInt(ARGS_TOTAL_PAGE_LOADED, getTotalPagesLoaded());
        searchQuery = searchView.getQuery().toString();
        outState.putString(ARGS_SEARCH_QUERY, searchQuery);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState == null) {
            totalPagesLoaded = 1;
            searchQuery = null;
        } else {
            totalPagesLoaded = savedInstanceState.getInt(ARGS_TOTAL_PAGE_LOADED, 1);
            searchQuery = savedInstanceState.getString(ARGS_SEARCH_QUERY);
        }
    }

    @Override
    public void onDestroy() {
        if (retrofitCall != null) {
            retrofitCall.cancel();
        }
        retrofitCallback = null;
        retrofitService = null;

        if (paginationScrollListener != null) {
            paginationScrollListener.onDestroy();
        }
        paginationScrollListener = null;

        if (itemAdapter != null) {
            itemAdapter.onDestroy();
        }
        itemAdapter = null;
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(final android.view.Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        initSearchMenu(menu);
        return true;
    }

    private void initSearchMenu(final android.view.Menu menu) {
        //TODO Retain search criteria when going back to main screen after clicking item from searched list.
        final MenuItem searchMenuItem = menu.findItem(R.id.menu_item_search);
        final SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView) searchMenuItem.getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);

        searchMenuItem.setOnActionExpandListener(this);

        //focus the SearchView
        focusSearchView(searchMenuItem);
    }

    private void focusSearchView(final MenuItem searchMenuItem) {
        if (searchQuery != null && !searchQuery.isEmpty()) {
            searchMenuItem.expandActionView();
            searchView.setQuery(searchQuery, true);
            searchView.clearFocus();
        }
    }

    /**
     *
     * Below are Overriden methods from MenuItem.OnActionExpandListener
     *
     */

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_search:
                // disable drag, swipe, pagination while searching
                itemTouchHelper.attachToRecyclerView(null);
                paginationScrollListener.setIsEnabled(false);
                itemAdapter.removeLoadingView();
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_search:
                // enable drag, swip, pagination
                itemTouchHelper.attachToRecyclerView(itemRecyclerView);
                paginationScrollListener.setIsEnabled(true);
                // pass null to remove filter, refreshes the recyclerview to it's original list
                itemAdapter.getFilter().filter(null);
                itemAdapter.addLoadingView();
                break;
            default:
                return false;
        }
        return true;
    }

    /**
     *
     * Below are Overriden methods from SearchView.OnQueryTextListener
     *
     */

    @Override
    public boolean onQueryTextSubmit(String query) {
        // this is not fully implemented so return false
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        itemAdapter.getFilter().filter(query);
        return true;
    }

    /**
     *
     * Below are Overriden methods from ItemAdapter.OnItemListener
     *
     */

    @Override
    public void onItemClick(final Item item) {
        final Intent intent = new Intent(this, ItemDetailActivity.class);
        intent.putExtra(ItemDetailActivity.ARGS_ITEM, item);
        startActivity(intent);
    }

    /**
     *
     * Below are Overriden methods from PaginationScrollListener.OnLoadMoreItems
     *
     */

    @Override
    public void loadMoreItems(final int currentPage) {
        updateItemList(getTotalPagesLoaded(), false);
    }

    private void updateItemList(final int pageNumber, final boolean forceShowLoading) {
        if (!NetworkUtil.isInternetConnected(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), getString(R.string.error_connection),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (forceShowLoading) {
            startupProgressBar.setVisibility(View.VISIBLE);
        }
        retrofitCallback = new Callback<Menu>() {
            @Override
            public void onResponse(Call<Menu> call, Response<Menu> response) {
                if (forceShowLoading) {
                    startupProgressBar.setVisibility(View.GONE);
                }

                if (call.isCanceled()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_network) +
                            "cancelled", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (response.isSuccessful()) {
                    final Menu menu = response.body();
                    itemAdapter.updateList(menu.itemList);
                    itemAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_network) +
                            "server error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Menu> call, Throwable t) {
                if (forceShowLoading) {
                    startupProgressBar.setVisibility(View.GONE);
                }
                if (call.isCanceled()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_network) +
                            "cancelled", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        };

        retrofitCall = retrofitService.getMenu("?page=" + pageNumber);
        retrofitCall.enqueue(retrofitCallback);
    }

    public int getTotalPagesLoaded() {
        if (paginationScrollListener == null) {
            return totalPagesLoaded;
        }
        return paginationScrollListener.getPagesLoaded();
    }
}
