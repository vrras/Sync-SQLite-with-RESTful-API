package com.firastech.syncsqlitewithrestfulapi.ui.main;

import android.content.Intent;

import com.firastech.syncsqlitewithrestfulapi.model.SendResponse;
import com.firastech.syncsqlitewithrestfulapi.ui.base.BaseView;

/**
 * Created by Firas Luthfi on 3/3/2018.
 */

public interface MainView
        extends BaseView {

    void requestProcess();
    void requestFinish();
    void onResponseMsg(SendResponse sendResponse);
    void onErrorMsg(Throwable error);
    void onCatchError(Exception error);
    void refreshList(Intent intent);
}
