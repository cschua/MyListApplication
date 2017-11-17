package chua.cs.mylistapplication.model.remote;

import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by christopherchua on 11/15/17.
 */

@Root(name = "menu", strict = false)
public class Menu implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Menu createFromParcel(Parcel in) {
            return new Menu(in);
        }

        public Menu[] newArray(int size) {
            return new Menu[size];
        }
    };
    @ElementList(name = "item", type = Item.class, required = true, inline = true)
    public List<Item> itemList;

    public Menu() {
        // used by parser
    }

    public Menu(Parcel in) {
        in.readList(this.itemList, List.class.getClassLoader());
    }

    @Override
    public String toString() {
        return "Menu{" +
                "itemList=" + itemList +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(itemList);
    }
}
