package com.byteshaft.itourguide;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by fi8er1 on 19/10/2015.
 */
public class ReceiveTransitionsIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ReceiveTransitionsIntentService() {
        super("test");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
