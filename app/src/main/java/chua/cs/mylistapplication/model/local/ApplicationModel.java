package chua.cs.mylistapplication.model.local;

import java.util.ArrayList;
import java.util.List;

import chua.cs.mylistapplication.model.remote.Item;

/**
 * Created by christopherchua on 11/15/17.
 * This class is only used for saving state of the data when switching between screens,
 * relaunching app, orientation change.  We can replace it with syncing with the server or/and saving
 * data locally (file or db).
 */

public class ApplicationModel {
    private final PaginationCount paginationCount;
    private List<Item> itemList;

    private ApplicationModel() {
        paginationCount = new PaginationCount(200);
        itemList = new ArrayList<>();
    }

    public static ApplicationModel getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public void replaceItemList(List<Item> itemList) {
        synchronized (this.itemList) {
            this.itemList = new ArrayList<>(itemList);
        }
    }

    public List<Item> getItemList() {
        synchronized (this.itemList) {
            final List<Item> itemList = new ArrayList<>(this.itemList);
        }
        return itemList;
    }

    public PaginationCount getPaginationCount() {
        return paginationCount;
    }

    private static class SingletonHelper {
        private static final ApplicationModel INSTANCE = new ApplicationModel();
    }
}
