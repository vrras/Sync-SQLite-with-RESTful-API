package com.firastech.syncsqlitewithrestfulapi.ui.main;

import android.content.BroadcastReceiver;

import com.firastech.syncsqlitewithrestfulapi.receiver.NetworkStateChecker;
import com.firastech.syncsqlitewithrestfulapi.ui.base.BasePresenter;

/**
 * Created by Firas Luthfi on 3/3/2018.
 */

public interface MainPresenter<V>
        extends BasePresenter<V> {

    void loadNames();
    void loadNewData();
    void saveToServer(String nama);
    void unregisterReceiverOn(BroadcastReceiver broadcastReceiver,
                              NetworkStateChecker networkStateChecker);
    void deleteAllData();
}
