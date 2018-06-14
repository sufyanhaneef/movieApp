package sufyan.com.mymovieapp.model;

import com.google.gson.annotations.SerializedName;

public class Reviews {

    @SerializedName("author")
    private String author;

    @SerializedName("content")
    private String content;

    public Reviews(String author, String content){
        this.author=author;
        this.content=content;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
