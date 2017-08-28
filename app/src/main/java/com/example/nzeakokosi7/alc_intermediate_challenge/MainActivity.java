package com.example.nzeakokosi7.alc_intermediate_challenge;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.nzeakokosi7.alc_intermediate_challenge.Activities.ProfileActivity;
import com.example.nzeakokosi7.alc_intermediate_challenge.Activities.SettingsActivity;
import com.example.nzeakokosi7.alc_intermediate_challenge.Adapter.DevelopersAdapter;
import com.example.nzeakokosi7.alc_intermediate_challenge.Model.Developers;
import com.example.nzeakokosi7.alc_intermediate_challenge.Utils.DeveloperJsonUtil;
import com.example.nzeakokosi7.alc_intermediate_challenge.Utils.NetworkUtil;

import org.json.JSONException;

import java.net.URL;

import static com.example.nzeakokosi7.alc_intermediate_challenge.Utils.NetworkUtil.isConnected;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>, DevelopersAdapter.OnItemClickedListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private DevelopersAdapter mDeveloperAdapter;
    private RecyclerView mDevelopersRecyclerView;
    private static final int DEVELOPERS_LOADER = 22;
    private ProgressDialog mProgressDialog;
    private int mViewType;
    private String mData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDevelopersRecyclerView = (RecyclerView) findViewById(R.id.developers_recylerview);
        mProgressDialog=new ProgressDialog(this);
        mDevelopersRecyclerView.setHasFixedSize(true);
        checkConnection();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        mViewType = Integer.parseInt(sharedPreferences.getString(getString(R.string.pref_view_key), getString(R.string.pref_view_list_value)));

    }


    @Override
    public void Onclick(Developers clicked) {
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("name", clicked.getmNames());
        bundle.putString("image", clicked.getmImageUrl());
        bundle.putString("url", clicked.getmGithubUrl());
        intent.putExtras(bundle);
        startActivity(intent);

    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        mProgressDialog.setTitle("Fetching Java Developers");
        mProgressDialog.setMessage("Please wait while we fetch the list of java developers");
        mProgressDialog.show();
        return new AsyncTaskLoader<String>(this) {

            @Override
            public String loadInBackground() {
                String response;
                URL url = NetworkUtil.buildUrl();
                try {
                    response = NetworkUtil.makeHttpRequest(url);
                    return response;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        mProgressDialog.dismiss();
        if (data != null) {
            showDevelopersList(data);
            mData = data;
        } else {
            loader.forceLoad();
        }

    }


    private void showDevelopersList(String data) {

        try {
            mDeveloperAdapter = new DevelopersAdapter(MainActivity.this, DeveloperJsonUtil.getDevelopers(data), mViewType);
            if (mViewType == Integer.parseInt(getString(R.string.pref_view_grid_value))) {
                mDevelopersRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            } else if (mViewType == Integer.parseInt(getString(R.string.pref_view_list_value))) {
                mDevelopersRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

            }
            mDevelopersRecyclerView.setAdapter(mDeveloperAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDevelopersRecyclerView.setAdapter(null);
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    void checkConnection() {

        if (isConnected(this)) {
            getSupportLoaderManager().initLoader(DEVELOPERS_LOADER, null, MainActivity.this).forceLoad();
        } else {
            Toast.makeText(this, "No internet", Toast.LENGTH_LONG).show();

            final AlertDialog alertDialog = new AlertDialog.Builder(
                    this).create();

            // Setting Dialog Message
            alertDialog.setMessage("Internet connection not available ,Please turn on your internet and restart the app");

            // Setting Icon to Dialog
            alertDialog.setIcon(android.R.drawable.ic_dialog_alert);

            // Setting OK Button
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to execute after dialog closed
                    alertDialog.cancel();
                    finish();
                }
            });

            // Showing Alert Message
            alertDialog.show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem search=menu.findItem(R.id.searchView);
        SearchView searchView= (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            startActivity(new Intent(this, SettingsActivity.class));

        }
        return super.onOptionsItemSelected(item);
    }
    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {


            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mDeveloperAdapter.getFilter().filter(newText);
                return true;
            }

        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        mViewType = Integer.parseInt(sharedPreferences.getString(getString(R.string.pref_view_key), getString(R.string.pref_view_list_label)));
        mDevelopersRecyclerView.setAdapter(null);
        try {
            mDeveloperAdapter = new DevelopersAdapter(MainActivity.this, DeveloperJsonUtil.getDevelopers(mData), mViewType);
            if (mViewType == Integer.parseInt(getString(R.string.pref_view_grid_value))) {
                mDevelopersRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            } else if (mViewType == Integer.parseInt(getString(R.string.pref_view_list_value))) {
                mDevelopersRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mDevelopersRecyclerView.setAdapter(mDeveloperAdapter);

    }


}
