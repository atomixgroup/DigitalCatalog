package ir.codetower.samanshiri.MessageService;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;

import ir.codetower.samanshiri.App;
import ir.codetower.samanshiri.Configurations;
import ir.codetower.samanshiri.Helpers.WebService;

/**
 * Created by Mr R00t on 6/10/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseIIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private WebService.OnPostReceived onPostReceived;
    private void sendRegistrationToServer(final String token) {
        String oldToken= App.prefManager.getPreference("token");
        if(oldToken.equals(token)){
            return;
        }
        final HashMap<String,String> params=new HashMap<>();
        params.put("token",token);
        params.put("sh2", Configurations.sh2);
        onPostReceived=new WebService.OnPostReceived() {
            @Override
            public void onReceived(String message) {
                if(message.equals("ERROR:1")){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(10000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            App.webService.postRequest(params, Configurations.apiUrl + "setToken", onPostReceived);
                        }
                    }).start();

                }
                else if(message.equals("OK:0")){
                    App.prefManager.savePreference("token",token);
                }
            }

            @Override
            public void onReceivedError(String message) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        App.webService.postRequest(params, Configurations.apiUrl + "setToken", onPostReceived);
                    }
                }).start();

            }
        };
        App.webService.postRequest(params, Configurations.apiUrl + "setToken", onPostReceived);

    }
}
