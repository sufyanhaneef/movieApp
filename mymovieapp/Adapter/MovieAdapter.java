package sufyan.com.mymovieapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import sufyan.com.mymovieapp.DetailActivity;
import sufyan.com.mymovieapp.R;
import sufyan.com.mymovieapp.model.Movie;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MyViewHolder> {
    private Context mContext;
    private List<Movie> movielist;

    private static final String SAVED_SUPER_STATE = "super-state";
    private static final String SAVED_LAYOUT_MANAGER = "layout-manager-state";

    public MovieAdapter(Context mContext, List<Movie> movielist) {
        this.mContext = mContext;
        this.movielist = movielist;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_card, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MovieAdapter.MyViewHolder holder, int position) {
        holder.title.setText(movielist.get(position).getOriginalTitle());
        String voting = Double.toString(movielist.get(position).getVoteAverage());
        holder.userRating.setText(voting);

        String poster = "https://image.tmdb.org/t/p/w500" + movielist.get(position).getPosterPath();

        Picasso.with(mContext)
                .load(poster).placeholder(R.drawable.load)
                .into(holder.movie_image);

    }

    @Override
    public int getItemCount() {
        return movielist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView userRating, title;
        public ImageView movie_image;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_card);
            userRating = itemView.findViewById(R.id.userRatig);
            movie_image = itemView.findViewById(R.id.card_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i = getAdapterPosition();
                    if (i != RecyclerView.NO_POSITION) {
                        Movie clickDataItem = movielist.get(i);
                        Intent intent = new Intent(mContext, DetailActivity.class);
                        intent.putExtra("movies", clickDataItem);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                           Toast.makeText(v.getContext(),"Details,,"+clickDataItem.getOriginalTitle() ,Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }
}