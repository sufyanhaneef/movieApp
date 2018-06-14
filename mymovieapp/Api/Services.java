package sufyan.com.mymovieapp.Api;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import sufyan.com.mymovieapp.model.MoviesResponse;
import sufyan.com.mymovieapp.model.ReviewResponse;
import sufyan.com.mymovieapp.model.TrailerResponse;

public interface Services {
    @GET("movie/popular")
    Call<MoviesResponse> getPopularMovies(@Query("api_key") String apiKey);

    @GET("movie/top_rated")
    Call<MoviesResponse> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("movie/{movie_id}/videos")
    Call<TrailerResponse> getMovieTrailer(@Path("movie_id") int id, @Query("api_key") String apiKey);
    @GET("movie/{movie_id}/reviews")
    Call<ReviewResponse> getUserReview(@Path("movie_id")int id,@Query("api_key") String apiKey);
}
