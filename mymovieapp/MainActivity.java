package sufyan.com.mymovieapp;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sufyan.com.mymovieapp.Adapter.MovieAdapter;
import sufyan.com.mymovieapp.Api.Client;
import sufyan.com.mymovieapp.Api.Services;
import sufyan.com.mymovieapp.data.FDbHelper;
import sufyan.com.mymovieapp.model.Movie;
import sufyan.com.mymovieapp.model.MoviesResponse;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String Log_tag = MovieAdapter.class.getName();
    private static final String STATE = "scrolState";
    public RecyclerView recyclerView;
    private Parcelable recyclerViewState = null;
    private RecyclerView.LayoutManager gridLayoutManager;
    private MovieAdapter adapter;
    private List<Movie> movieList;
    private SwipeRefreshLayout swipeContainer;
    private FDbHelper dbHelper;
    private AppCompatActivity activity = MainActivity.this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            gridLayoutManager.onRestoreInstanceState(savedInstanceState);
        }
        initViews();
        recyclerView = (RecyclerView) findViewById(R.id.rv_recycler_view);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE, recyclerView.getLayoutManager().onSaveInstanceState());
        // Toast.makeText(getApplicationContext(), "savedStated", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState instanceof Bundle) {
            recyclerViewState = ((Bundle) savedInstanceState).getParcelable(STATE);
            recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
            //   Toast.makeText(getApplicationContext(),"REstore",Toast.LENGTH_SHORT).show();
        }
        initViews();
    }

    public Activity getActivity() {
        Context context = this;
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    private void initViews() {

        recyclerView = (RecyclerView) findViewById(R.id.rv_recycler_view);

        movieList = new ArrayList<>();
        adapter = new MovieAdapter(this, movieList);

        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.main_activity);
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_light);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initViews();
                Toast.makeText(MainActivity.this, "Refreshing", Toast.LENGTH_SHORT).show();
            }
        });
        checkSortOrder();
    }

    private void loadMostPopularMovies() {
        try {
            if (BuildConfig.Api_Key.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Get api key", Toast.LENGTH_SHORT).show();
                return;
            }
            // For loading Most Popular Movies
            Client client = new Client();
            Services Service = client.getClient().create(Services.class);
            Call<MoviesResponse> call = Service.getPopularMovies(BuildConfig.Api_Key);
            call.enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                    //Getting Response from Json
                    List<Movie> movies = response.body().getResults();
                    adapter = new MovieAdapter(getApplicationContext(), movies);
                    //setting adapter to recyclerView
                    recyclerView.setAdapter(adapter);
                    recyclerView.smoothScrollToPosition(0);
                    if (swipeContainer.isRefreshing()) {
                        swipeContainer.setRefreshing(false);
                    }
                    if (recyclerViewState != null) {
                        Parcelable savedInstanceState = null;
                        gridLayoutManager.onRestoreInstanceState(savedInstanceState);
                    }
                }

                @Override
                public void onFailure(Call<MoviesResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(getApplicationContext(), "Fetching Error", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadTopRatedMovies() {

        try {
            if (BuildConfig.Api_Key.isEmpty()) {
                Toast.makeText(getApplicationContext(), "get the api key", Toast.LENGTH_SHORT).show();
                return;
            }
            //Json For Loading Top Rated Movies
            Client Client = new Client();
            Services Service = Client.getClient().create(Services.class);
            Call<MoviesResponse> call = Service.getTopRatedMovies(BuildConfig.Api_Key);
            call.enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                    List<Movie> movies = response.body().getResults();
                    recyclerView.setAdapter(new MovieAdapter(getBaseContext(), movies));
                    recyclerView.smoothScrollToPosition(0);
                    if (swipeContainer.isRefreshing()) {
                        swipeContainer.setRefreshing(false);
                    }

                }

                @Override
                public void onFailure(Call<MoviesResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                }
            });


        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void favoriteViews() {
        recyclerView = findViewById(R.id.rv_recycler_view);
        movieList = new ArrayList<>();
        adapter = new MovieAdapter(this, movieList);
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        dbHelper = new FDbHelper(activity);
        getFavorite();
    }

    private void checkSortOrder() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String order = sharedPreferences.getString(
                this.getString(R.string.pref_sort_order_key),
                this.getString(R.string.pref_most_popular)
        );
        if (order.equals(this.getString(R.string.pref_highest_rated))) {
            loadTopRatedMovies();
        } else if (order.equals(this.getString(R.string.pref_most_popular))) {
            loadMostPopularMovies();
        } else {
            favoriteViews();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:
                Intent intent = new Intent(this, settings.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getFavorite() {
        new AsyncTask<Void, Void, Void>() {
            Context mcontext;

            @Override
            protected Void doInBackground(Void... voids) {
                movieList.clear();
                movieList.addAll(dbHelper.getAllFavorite(mcontext));
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                adapter.notifyDataSetChanged();
            }
        }.execute();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        checkSortOrder();
    }
}
