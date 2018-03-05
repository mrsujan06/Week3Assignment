package com.test.week3assignment.api;

import com.test.week3assignment.model.ParkingResponse;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by Sujan on 03/03/2018.
 */

public interface ApiObservableParkingService {


    @GET("/api/v1/parkinglocations/search?lat=37.781148&lng=-122.469589")
    Observable<List<ParkingResponse>> getData();

    @GET("/api/v1/parkinglocations/api/v1/parkinglocations/{id}")
    Observable<List<ParkingResponse>> getDataById();

    @POST("api/v1/parkinglocations/2034")
    Observable<List<ParkingResponse>> reserveParking(int id);

}
