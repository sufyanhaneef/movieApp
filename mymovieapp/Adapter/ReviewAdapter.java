package sufyan.com.mymovieapp.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import sufyan.com.mymovieapp.DetailActivity;
import sufyan.com.mymovieapp.R;
import sufyan.com.mymovieapp.model.Reviews;
import sufyan.com.mymovieapp.model.Trailer;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder> {
    private Context mContext;
    private List<Reviews> reviewsList;

    public ReviewAdapter(Context mContext, List<Reviews> reviewsList) {
        this.mContext=mContext;
        this.reviewsList=reviewsList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_card,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
    holder.author.setText(reviewsList.get(position).getAuthor());
    holder.content.setText(reviewsList.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView author,content;

        public MyViewHolder(View itemView) {
            super(itemView);
            author=itemView.findViewById(R.id.txtAuthor);
            content=itemView.findViewById(R.id.txtContent);
        }
    }
}
