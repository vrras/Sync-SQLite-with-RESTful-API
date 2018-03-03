package com.firastech.syncsqlitewithrestfulapi.ui.base;

/**
 * Created by Firas Luthfi on 3/3/2018.
 */

public interface BasePresenter<V> {
    void onAttach(V view);
    void onDetach();
}
