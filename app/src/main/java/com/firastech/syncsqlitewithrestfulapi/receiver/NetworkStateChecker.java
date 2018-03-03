package com.firastech.syncsqlitewithrestfulapi.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.firastech.syncsqlitewithrestfulapi.db.DatabaseHelper;
import com.firastech.syncsqlitewithrestfulapi.model.SendResponse;
import com.firastech.syncsqlitewithrestfulapi.network.ApiInterface;
import com.firastech.syncsqlitewithrestfulapi.network.InitRetrofit;
import com.firastech.syncsqlitewithrestfulapi.ui.main.MainActivity;
import com.firastech.syncsqlitewithrestfulapi.ui.main.MainPresenterImpl;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Firas Luthfi on 3/3/2018.
 */

public class NetworkStateChecker extends BroadcastReceiver {
    private Context context;
    private DatabaseHelper db;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        db = new DatabaseHelper(context);

        //cek jaringan
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo acNetworkInfo = cm.getActiveNetworkInfo();
        //cek jaringan
        if (acNetworkInfo !=null){
            if (acNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI || acNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE){

                Cursor cursor = db.getUnsyncedNames();
                if (cursor.moveToFirst()){
                    do {
                        saveName(
                                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)),
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME))
                        );
                    }
                    while (cursor.moveToNext());
                }
            }
        }
    }

    private void saveName(final int id, final String nama) {
        ApiInterface service = InitRetrofit.getClient().create(ApiInterface.class);
        Call<SendResponse> call = service.sendName(nama);
        call.enqueue(new Callback<SendResponse>() {
            @Override
            public void onResponse(Call<SendResponse> call, Response<SendResponse> response) {
                boolean error = response.body().isError();
                if (error == false) {
                    db.updateNameStatus(id, MainPresenterImpl.NAME_SYNCED_WITH_SERVER);

                    //sending the broadcast to refresh the list
                    context.sendBroadcast(new Intent(MainActivity.DATA_SAVED_BROADCAST));

                }
            }

            @Override
            public void onFailure(Call<SendResponse> call, Throwable t) {

            }
        });
    }
}
