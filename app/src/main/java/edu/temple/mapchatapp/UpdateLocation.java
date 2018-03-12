package edu.temple.mapchatapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class UpdateLocation extends Service {
    public UpdateLocation() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
