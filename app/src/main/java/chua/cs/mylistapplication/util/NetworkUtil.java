package chua.cs.mylistapplication.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by christopherchua on 11/15/17.
 */

public class NetworkUtil {
    public final static String TAG = NetworkUtil.class.getSimpleName();

    public static boolean isInternetConnected(final Context context) {
        try {
            final ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            final boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting() && activeNetwork.isAvailable();
            return isConnected;
        } catch (final Exception exception) {
            Log.w(TAG, exception);
        }
        return false;
    }
}
