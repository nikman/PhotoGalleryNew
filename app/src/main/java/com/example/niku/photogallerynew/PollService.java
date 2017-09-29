package com.example.niku.photogallerynew;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by niku on 29.09.2017.
 */

public class PollService extends IntentService {

    private static final String TAG = "PollService";

    public PollService() {
        super(TAG);
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, PollService.class);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(TAG, "Received an intent:" + intent);
    }
}
