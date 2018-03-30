package edu.temple.mapchatapp;

import android.location.Location;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nmale_000 on 3/12/2018.
 */

public class Partner implements Comparable {
    private LatLng latLng;
    String user;
    double distanceFromMainUser;

    public Partner(JSONObject obj, Location mainUserLoc){
        try {
            latLng = new LatLng(obj.getDouble("latitude"), obj.getDouble("longitude"));
            user = obj.getString("username");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //set the distance from the main user
        Location partnerLoc = new Location("partner");
        partnerLoc.setLatitude(latLng.latitude);
        partnerLoc.setLongitude(latLng.longitude);
        distanceFromMainUser = partnerLoc.distanceTo(mainUserLoc);
    }

    @Override
    public int compareTo(@NonNull Object o) {
        Partner comparingTo = (Partner) o;
        double comparingDistance = comparingTo.getDistanceFromMainUser();
        if(comparingDistance == distanceFromMainUser) {
            return 0;
        }else if(comparingDistance > distanceFromMainUser){
            return 1;
        }else{
            return -1;
        }
    }

    public double getDistanceFromMainUser(){
        return distanceFromMainUser;
    }

    @Override
    public String toString() {
        return user + ": " + distanceFromMainUser;
    }

    public LatLng getLatLng(){
        return latLng;
    }
}
