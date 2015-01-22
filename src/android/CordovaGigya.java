package us.platan.gigya;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.gigya.socialize.GSObject;
import com.gigya.socialize.GSResponse;
import com.gigya.socialize.GSResponseListener;
import com.gigya.socialize.android.GSAPI;
import com.gigya.socialize.android.GSSession;
import com.gigya.socialize.android.event.GSLoginUIListener;
import com.gigya.socialize.android.event.GSSocializeEventListener;

public class CordovaGigya extends CordovaPlugin {

    private static final String GIGYA_API_KEY = "3_UeTmNrbx3Lr2j32fV6xLYlEIcPDc4fG6N8j2WbuMZgcxW_sZCJOb4hcS89zwAIR_";
    private static final String TAG = "CordovaGigya";

    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        Log.d(TAG, action);

        if ("initialize".equals(action)) {

            GSAPI.getInstance().initialize(cordova.getActivity(), args.getString(0));
            callbackContext.success();

            return true;

        }
        else if ("showLoginUI".equals(action)) {

            // Present the Login user interface.
            GSAPI.getInstance().showLoginUI(null, new GSLoginUIListener() {
                @Override
                public void onLoad(Object context) {
                    Log.d(TAG, "Gigya loginUI was loaded");
                }

                @Override
                public void onError(GSResponse response, Object context) {
                    Log.d(TAG, "Gigya loginUI had an error - " + response.getErrorMessage());
                    callbackContext.error(response.getErrorMessage());
                }

                @Override
                public void onClose(boolean canceled, Object context) {
                    Log.d(TAG, "Gigya loginUI was closed");
                }

                @Override
                public void onLogin(String provider, GSObject user, Object context) {
                    Log.d(TAG, "Gigya loginUI has logged in");
                    callbackContext.success(user.toJsonString());
                }
            }, null);

            return true;
        }
        else if ("getSession".equals(action)) {

            GSSession session = GSAPI.getInstance().getSession();
            callbackContext.success(session.toString());

            return true;
        }
        else if ("getUserInfo".equals(action)){

            GSAPI.getInstance().sendRequest("socialize.getUserInfo", null, new GSResponseListener() {
                @Override
                public void onGSResponse(String method, GSResponse response, Object context) {
                    if (response.getErrorCode() == 0) {
                        callbackContext.success(response.getData().toJsonString());
                    } else {
                        callbackContext.error(response.getErrorCode());
                    }
                }
            }, null);

            return true;
        }
        else if ("logout".equals(action)){

            GSAPI.getInstance().logout();
            callbackContext.success();

            return true;
        }
        return false;  // Returning false results in a "MethodNotFound" error.
    }
}