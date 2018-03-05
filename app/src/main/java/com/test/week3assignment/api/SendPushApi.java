package com.test.week3assignment.api;


import com.test.week3assignment.model.PushRequestBody;
import com.test.week3assignment.model.PushResponse;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by Sujan on 05/03/2018.
 */

public interface SendPushApi {

    @Headers({
            "Content-Type: application/json",
            "Authorization: key=AAAAemK-n4k:APA91bGD-LGgt3zBwYKR_q2lQYEDeKEwcel9rHZqr0KlwCQeYXQCO3U0dV1R_HsZJJtxsKnZjoOC97DC5JxKERD9Wwd-6Q2l1f8APfSkKlLZM18nw_z5DGwWtCX6-LgnUmbObHmulaPk"
    })
    @POST("/fcm/send")
    Observable<PushResponse>  sendPush(@Body PushRequestBody pushRequestBody);
}
