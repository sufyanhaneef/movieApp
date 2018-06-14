package sufyan.com.mymovieapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReviewResponse {

    @SerializedName("id")
    private int review_id;
    @SerializedName("page")
    private int page;
    @SerializedName("results")
    private List<Reviews> results;

    public ReviewResponse(int review_id, int page, List<Reviews> results){

        this.page=page;
        this.results=results;
        this.review_id=review_id;
    }

    public int getPage() {
        return page;
    }

    public int getReview_id() {
        return review_id;
    }

    public List<Reviews> getResults() {
        return results;
    }

    public void setResults(List<Reviews> results) {
        this.results = results;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setReview_id(int review_id) {
        this.review_id = review_id;
    }
}

