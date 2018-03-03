package com.firastech.syncsqlitewithrestfulapi.ui.main;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.firastech.syncsqlitewithrestfulapi.R;
import com.firastech.syncsqlitewithrestfulapi.adapter.NameAdapter;
import com.firastech.syncsqlitewithrestfulapi.db.DatabaseHelper;
import com.firastech.syncsqlitewithrestfulapi.model.Nama;
import com.firastech.syncsqlitewithrestfulapi.model.SendResponse;
import com.firastech.syncsqlitewithrestfulapi.receiver.NetworkStateChecker;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity
        implements MainView {

    @BindView(R.id.editTextName)
    EditText editTextName;
    @BindView(R.id.buttonSave)
    Button buttonSave;
    @BindView(R.id.listViewNames)
    ListView listViewNames;
    @BindView(R.id.btnDelete)
    Button btnDelete;
    private MainPresenter presenter;
    private DatabaseHelper db;
    private List<Nama> names;
    private Intent intent;
    private Context mContext;

    public static final String DATA_SAVED_BROADCAST = "com.firastech.syncsqlitewithrestfulapi";

    private BroadcastReceiver broadcastReceiver;
    private NetworkStateChecker networkStateChecker;

    private NameAdapter nameAdapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        initPresenter();
        onAttachView();
        presenter.loadNames();
    }

    @Override
    protected void onResume() {
        networkStateChecker = new NetworkStateChecker();
        registerReceiver(networkStateChecker, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                presenter.loadNames();
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter(DATA_SAVED_BROADCAST));
        super.onResume();
    }

    private void initView() {
        db = new DatabaseHelper(this);
        names = new ArrayList<>();
        progressDialog = new ProgressDialog(MainActivity.this);
        intent = getIntent();
        mContext = this;
    }

    private void initPresenter() {
        presenter = new MainPresenterImpl(names, db, nameAdapter,
                listViewNames, editTextName, intent, mContext);
    }

    @Override
    public void onAttachView() {
        presenter.onAttach(this);
    }

    @Override
    public void onDetachView() {
        presenter.onDetach();
    }

    @OnClick({R.id.buttonSave, R.id.btnDelete})
    public void onViewClicked(View view) {
        final String nama = editTextName.getText().toString();
        switch (view.getId()) {
            case R.id.buttonSave:
                presenter.saveToServer(nama);
                break;
            case R.id.btnDelete:
                presenter.deleteAllData();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menumain) {
            presenter.loadNewData();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void requestProcess() {
        progressDialog.setMessage("Fetching name from server. . .");
        progressDialog.show();
    }

    @Override
    public void requestFinish() {
        progressDialog.dismiss();
    }

    @Override
    public void onResponseMsg(SendResponse sendResponse) {
        Toast.makeText(MainActivity.this, sendResponse.getMessage().toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onErrorMsg(Throwable error) {
        Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCatchError(Exception error) {
        Toast.makeText(this, "Erro " + error.toString(), Toast.LENGTH_SHORT).show();
        Log.d("", "onViewClicked: " + error.toString());
    }

    @Override
    public void refreshList(Intent intent) {
        finish();
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        presenter.unregisterReceiverOn(broadcastReceiver, networkStateChecker);
        super.onPause();
    }
}
