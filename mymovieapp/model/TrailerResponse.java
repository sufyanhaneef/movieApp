package sufyan.com.mymovieapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrailerResponse {

    @SerializedName("id")
    private int trailer_id;
    @SerializedName("results")
    private List<Trailer> results;

    public TrailerResponse(int trailer_id, List<Trailer> results){
        this.trailer_id=trailer_id;
        this.results=results;
    }

    public int getTrailer_id() {
        return trailer_id;
    }

    public void setTrailer_id(int trailer_id) {
        this.trailer_id = trailer_id;
    }

    public void setResults(List<Trailer> results) {
        this.results = results;
    }

    public List<Trailer> getResults() {
        return results;
    }
}
