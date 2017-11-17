package chua.cs.mylistapplication.model.local;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by christopherchua on 11/15/17.
 */

public class PaginationCount implements Parcelable {

    public final static int TOTAL_ITEMS_PER_PAGE = 20; // better if this is from the server
    private final int totalItems;
    private final int totalPages;
    private final int lastPageItemCount;

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public PaginationCount createFromParcel(Parcel in) {
            return new PaginationCount(in);
        }

        public PaginationCount[] newArray(int size) {
            return new PaginationCount[size];
        }
    };

    public PaginationCount(final Parcel in) {
        this(in.readInt());
    }

    public PaginationCount(final int totalItems) {
        this.totalItems = totalItems;
        totalPages = totalItems / TOTAL_ITEMS_PER_PAGE;

        // this is typically used to display the last page
        lastPageItemCount = totalItems % TOTAL_ITEMS_PER_PAGE;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(totalItems);
    }

    public int getTotalPages() {
        // lastPageItemCount means we need to add another page for the remaining items
        return lastPageItemCount > 0 ? totalPages + 1 : totalPages;
    }

    public int getItemsCountForPage(final int pageNumber) {
        if (pageNumber == getTotalPages()) {
            return getLastPageItemCount();
        }
        return TOTAL_ITEMS_PER_PAGE;
    }

    private int getLastPageItemCount() {
        // totalPages might not be a perfect divisible of TOTAL_ITEMS_PER_PAGE, therefore
        // lastPageItemCount may contain the remainder count
        return lastPageItemCount > 0 ? lastPageItemCount : TOTAL_ITEMS_PER_PAGE;
    }
}
