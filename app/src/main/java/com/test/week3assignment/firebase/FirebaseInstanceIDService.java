package com.test.week3assignment.firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by Sujan on 05/03/2018.
 */

public class FirebaseInstanceIDService extends com.google.firebase.iid.FirebaseInstanceIdService{

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Send any registration to your app's servers.
    }


}
