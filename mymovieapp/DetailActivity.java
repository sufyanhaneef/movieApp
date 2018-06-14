package sufyan.com.mymovieapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sufyan.com.mymovieapp.Adapter.ReviewAdapter;
import sufyan.com.mymovieapp.Adapter.TrailerAdapter;
import sufyan.com.mymovieapp.Api.Client;
import sufyan.com.mymovieapp.Api.Services;
import sufyan.com.mymovieapp.data.FDbHelper;
import sufyan.com.mymovieapp.data.favoriteContract;
import sufyan.com.mymovieapp.model.Movie;
import sufyan.com.mymovieapp.model.ReviewResponse;
import sufyan.com.mymovieapp.model.Reviews;
import sufyan.com.mymovieapp.model.Trailer;
import sufyan.com.mymovieapp.model.TrailerResponse;

public class DetailActivity extends AppCompatActivity {
    private final AppCompatActivity activity = DetailActivity.this;
    TextView movieTitle, PlotSynopsis, UserRating, ReleaseDate;
    ImageView movieImage;
    String nameOfMovie, synosis, Rating, ReleaseDAte, thumbnail;
    int movieId;
    Movie movie;
    private RecyclerView recyclerView, recyclerView2;
    private TrailerAdapter adapter;
    private List<Trailer> trailerList;
    private ReviewAdapter reviewAdapter;
    private List<Reviews> reviewsList;
    private SQLiteDatabase db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        iniCollapsingBar();
        movieTitle = (TextView) findViewById(R.id.tv_title);
        PlotSynopsis = findViewById(R.id.tv_plot_synopsis);
        UserRating = findViewById(R.id.tv_user_rating);
        ReleaseDate = findViewById(R.id.tv_Releas_date);
        movieImage = findViewById(R.id.image_poster);


        FDbHelper dbHelper = new FDbHelper(this);
        db = dbHelper.getWritableDatabase();

        //Getting INtent for showing data in Content detail activity
        Intent intent = getIntent();
        if (intent.hasExtra("movies")) {
            //  Toast.makeText(getApplicationContext(), "Details  ", Toast.LENGTH_SHORT).show();
            movie = getIntent().getParcelableExtra("movies");

            nameOfMovie = movie.getOriginalTitle();
            thumbnail = movie.getPosterPath();
            synosis = movie.getOverview();
            ReleaseDAte = movie.getReleaseDate();
            Rating = Double.toString(movie.getVoteAverage());
            movieId = movie.getId();

            String poster = "https://image.tmdb.org/t/p/w500" + thumbnail;
            //Picasso Library for Loading Photo
            Picasso.with(this)
                    .load(poster)
                    .placeholder(R.drawable.load)
                    .into(movieImage);

            movieTitle.setText(nameOfMovie);
            PlotSynopsis.setText(synosis);
            UserRating.setText(Rating);
            ReleaseDate.setText(ReleaseDAte);
        } else {
            Toast.makeText(getApplicationContext(), "No Data Found", Toast.LENGTH_SHORT).show();
        }

        MaterialFavoriteButton materialFavoriteButton = (MaterialFavoriteButton) findViewById(R.id.btn_favorite);
        if (checkExistance(nameOfMovie)) {
            materialFavoriteButton.setFavorite(true);
            materialFavoriteButton.setOnFavoriteChangeListener(
                    new MaterialFavoriteButton.OnFavoriteChangeListener() {
                        @Override
                        public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                            if (favorite == true) {
                                addFavorite(movie);
                                Snackbar.make(buttonView, "Added To Favorite", Snackbar.LENGTH_SHORT).show();
                            } else {
                                deleteFavorite(movieId);
                                Snackbar.make(buttonView, "Removed FAvorites", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            materialFavoriteButton.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
                @Override
                public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                    if (favorite == true) {
                        addFavorite(movie);
                        Snackbar.make(buttonView, "Favorite Added", Snackbar.LENGTH_SHORT).show();
                    } else {
                        deleteFavorite(movieId);
                        Snackbar.make(buttonView, "Removed Favorite", Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }
        initViews();
        UserReviewViews();
    }

    private void iniCollapsingBar() {
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.CollapsingLayout);
        collapsingToolbarLayout.setTitle("");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBar_layout);
        appBarLayout.setExpanded(true);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle("Movie Details");
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle("");
                    isShow = false;
                }
            }
        });

    }

    private void initViews() {
        trailerList = new ArrayList<>();
        adapter = new TrailerAdapter(this, trailerList);

        recyclerView = findViewById(R.id.rv_trailer);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        LoadJson();
    }

    private void UserReviewViews() {
        reviewsList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(this, reviewsList);
        recyclerView2 = findViewById(R.id.rv_review);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView2.setLayoutManager(layoutManager);
        recyclerView2.setAdapter(reviewAdapter);
        reviewAdapter.notifyDataSetChanged();
        LoadReview();
    }

    private void LoadJson() {
        try {
            if (BuildConfig.Api_Key.isEmpty()) {
                Toast.makeText(getApplicationContext(), "get Your Api Key", Toast.LENGTH_SHORT).show();
            }
            Client client = new Client();
            Services apiService = Client.getClient().create(Services.class);
            Call<TrailerResponse> call = apiService.getMovieTrailer(movieId, BuildConfig.Api_Key);
            call.enqueue(new Callback<TrailerResponse>() {
                @Override
                public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                    List<Trailer> trailer = response.body().getResults();
                    recyclerView.setAdapter(new TrailerAdapter(getApplicationContext(), trailer));
                    recyclerView.smoothScrollToPosition(0);
                }

                @Override
                public void onFailure(Call<TrailerResponse> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.d("error", e.getMessage());
        }
    }

    private void LoadReview() {
        try {
            if (BuildConfig.Api_Key.isEmpty()) {
                Toast.makeText(getApplicationContext(), "get your api", Toast.LENGTH_SHORT).show();
            }
            Client client = new Client();
            Services apiService = Client.getClient().create(Services.class);
            Call<ReviewResponse> call = apiService.getUserReview(movieId, BuildConfig.Api_Key);
            call.enqueue(new Callback<ReviewResponse>() {
                @Override
                public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                    List<Reviews> reviews = response.body().getResults();
                    recyclerView2.setAdapter(new ReviewAdapter(getApplicationContext(), reviews));
                }

                @Override
                public void onFailure(Call<ReviewResponse> call, Throwable t) {

                }
            });

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean checkExistance(String searchItem) {
        String[] Projection = {
                favoriteContract.Entries._ID,
                favoriteContract.Entries.COLUMN_MOVIEID,
                favoriteContract.Entries.COLUMN_POSTER_PATH,
                favoriteContract.Entries.COLUMN_PLOT_SYNOPSIS,
                favoriteContract.Entries.COLUMN_TITLE,
                favoriteContract.Entries.COLUMN_USERRATING
        };
        String Selection = favoriteContract.Entries.COLUMN_TITLE + "=?";
        String[] SelectionArgs = {searchItem};
        String limit = "1";

        Cursor cursor = db.query(favoriteContract.Entries.TABLE_NAME, Projection, Selection, SelectionArgs, null, null, null, limit);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public void addFavorite(Movie movie) {

        ContentValues values = new ContentValues();
        values.put(favoriteContract.Entries.COLUMN_MOVIEID, movie.getId());
        values.put(favoriteContract.Entries.COLUMN_TITLE, movie.getOriginalTitle());
        values.put(favoriteContract.Entries.COLUMN_USERRATING, movie.getVoteAverage());
        values.put(favoriteContract.Entries.COLUMN_POSTER_PATH, movie.getPosterPath());
        values.put(favoriteContract.Entries.COLUMN_PLOT_SYNOPSIS, movie.getOverview());

        this.getContentResolver().insert(favoriteContract.Entries.CONTENT_URI, values);
    }

    public int deleteFavorite(int id) {

        return this.getContentResolver().delete(favoriteContract.Entries.CONTENT_URI,
                favoriteContract.Entries.COLUMN_MOVIEID + "=?", new String[]{Integer.toString(id)});

    }
}