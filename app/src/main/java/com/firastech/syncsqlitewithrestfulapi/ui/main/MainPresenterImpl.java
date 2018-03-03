package com.firastech.syncsqlitewithrestfulapi.ui.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.EditText;
import android.widget.ListView;

import com.firastech.syncsqlitewithrestfulapi.R;
import com.firastech.syncsqlitewithrestfulapi.adapter.NameAdapter;
import com.firastech.syncsqlitewithrestfulapi.db.DatabaseHelper;
import com.firastech.syncsqlitewithrestfulapi.model.DataItem;
import com.firastech.syncsqlitewithrestfulapi.model.DataResponse;
import com.firastech.syncsqlitewithrestfulapi.model.Nama;
import com.firastech.syncsqlitewithrestfulapi.model.SendResponse;
import com.firastech.syncsqlitewithrestfulapi.network.ApiInterface;
import com.firastech.syncsqlitewithrestfulapi.network.InitRetrofit;
import com.firastech.syncsqlitewithrestfulapi.receiver.NetworkStateChecker;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Firas Luthfi on 3/3/2018.
 */

public class MainPresenterImpl
        implements MainPresenter<MainView> {


    public static final int NAME_SYNCED_WITH_SERVER = 1;
    public static final int NAME_NOT_SYNCED_WITH_SERVER = 0;

    private MainView view;
    private List<Nama> names;
    private DatabaseHelper db;
    private NameAdapter nameAdapter;
    private ListView listViewNames;
    private EditText editTextName;
    private Intent intent;
    private Context context;

    MainPresenterImpl(List<Nama> names,
                      DatabaseHelper db,
                      NameAdapter nameAdapter,
                      ListView listViewNames,
                      EditText editTextName,
                      Intent intent, Context context) {
        this.names = names;
        this.db = db;
        this.nameAdapter = nameAdapter;
        this.listViewNames = listViewNames;
        this.editTextName = editTextName;
        this.intent = intent;
        this.context = context;
    }

    @Override
    public void onAttach(MainView view) {
        this.view = view;
    }

    @Override
    public void onDetach() {
        view = null;
    }

    @Override
    public void loadNames() {
        view.requestProcess();
        names.clear();
        Cursor cursor = db.getNames();
        if (cursor.moveToFirst()) {
            do {
                Nama name = new Nama(
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS))
                );
                names.add(name);
            } while (cursor.moveToNext());
        }
        view.requestFinish();
        nameAdapter = new NameAdapter(context, R.layout.names, names);
        listViewNames.setAdapter(nameAdapter);
    }

    @Override
    public void loadNewData() {
        view.requestProcess();
        try {
            ApiInterface service = InitRetrofit.getClient().create(ApiInterface.class);
            retrofit2.Call<DataResponse> call = service.getName();
            call.enqueue(new Callback<DataResponse>() {
                @Override
                public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                    names.clear();
                    view.requestFinish();
                    DataResponse dataResponse = response.body();

                    if (!dataResponse.isError()) {
                        List<DataItem> dataItemList = dataResponse.getData();
                        for (int i = 0; i < dataItemList.size(); i++) {
                            DataItem dataItem = dataItemList.get(i);
                            int newId = Integer.parseInt(dataItem.getId());
                            String newNama = dataItem.getName();

                            boolean ada = getOneNames(newId);
                            if (ada) {
                                updateNameToLocalStorage(newId, newNama, NAME_SYNCED_WITH_SERVER);
                            } else {
                                saveNameToLocalStorage(newNama, NAME_SYNCED_WITH_SERVER);
                            }
                        }
                        nameAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<DataResponse> call, Throwable t) {
                    view.onErrorMsg(t);
                    view.requestFinish();
                }
            });
        } catch (Exception e) {
            view.onCatchError(e);
            view.requestFinish();
        }
    }

    private void saveNameToLocalStorage(String name, int status) {
        view.requestProcess();
        editTextName.setText("");
        db.addName(name, status);
        Nama n = new Nama(name, status);
        names.add(n);
        refreshListAdapter();
    }

    private void updateNameToLocalStorage(int id, String name, int status) {
        view.requestProcess();
        db.updateName(id, name, status);
        Nama n = new Nama(name, status);
        names.add(n);
        view.requestFinish();
//        refreshListAdapter();
    }

    private boolean getOneNames(int id) {
        Cursor cursor = db.getOneNames(id);
        if (cursor.moveToFirst()) {
            return true;
        } else {
            return false;
        }
    }

    private void deleteAllNames(){
        view.requestProcess();
        db.deleteAllNames();
        view.refreshList(intent);
        refreshListAdapter();
    }

    private void refreshListAdapter() {
        nameAdapter.notifyDataSetChanged();
        view.requestFinish();
    }

    @Override
    public void saveToServer(final String nama) {
        view.requestProcess();
        try {
            ApiInterface service = InitRetrofit.getClient().create(ApiInterface.class);
            retrofit2.Call<SendResponse> call = service.sendName(nama);
            call.enqueue(new Callback<SendResponse>() {
                @Override
                public void onResponse(Call<SendResponse> call, Response<SendResponse> response) {

                    boolean error = response.body().isError();
                    if (error == false) {
                        saveNameToLocalStorage(nama, NAME_SYNCED_WITH_SERVER);
//                        view.refreshList(intent);
//                        startActivity(new Intent(MainActivity.this, MainActivity.class));
                        view.requestFinish();
                        view.onResponseMsg(response.body());
                    } else {
                        view.requestFinish();
                        saveNameToLocalStorage(nama, NAME_NOT_SYNCED_WITH_SERVER);
                        view.onResponseMsg(response.body());
                    }

                }

                @Override
                public void onFailure(Call<SendResponse> call, Throwable t) {
                    saveNameToLocalStorage(nama, NAME_NOT_SYNCED_WITH_SERVER);
                    view.onErrorMsg(t);
                    view.requestFinish();
                }
            });

        } catch (Exception e) {
            view.onCatchError(e);
            view.requestFinish();
        }
    }

    @Override
    public void unregisterReceiverOn(BroadcastReceiver broadcastReceiver,
                                     NetworkStateChecker networkStateChecker) {
        if (broadcastReceiver != null) {
            context.unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }

        if (networkStateChecker != null) {
            context.unregisterReceiver(networkStateChecker);
            networkStateChecker = null;
        }
    }

    @Override
    public void deleteAllData() {
        deleteAllNames();
    }

}
