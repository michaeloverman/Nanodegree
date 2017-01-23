package com.app.movie.cinephilia;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by GAURAV on 13-12-2015.
 */
public class MovieModel implements Parcelable{
    private String image;
    private String title;
    private double userRating;
    private String releaseDate;
    private String synopsis;
    private String posterUrl;
    private String vote_count;
    private String backdropUrl;
    private int id;

    public MovieModel(String originalTitle, double userRating, String releaseDate, String plotSynopsis,
                      String vote_count, String backdropPath, int id,String posterPath) {
        this.title = originalTitle;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
        this.synopsis = plotSynopsis;
        this.vote_count = vote_count;
        this.backdropUrl = "http://image.tmdb.org/t/p/w185" + backdropPath;
        this.id = id;
        this.posterUrl = "http://image.tmdb.org/t/p/w185" + posterPath;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getUserRating(){
        return userRating;
    }

    public String getReleaseDate(){
        String[] formatDate = releaseDate.split("-");
        return formatDate[2]+"-"+formatDate[1]+"-"+formatDate[0];
    }

    public String getSynopsis(){
        return synopsis;
    }

    public String getPosterUrl(){
        return posterUrl;
    }

    public String getBackdropUrl(){
        return backdropUrl;
    }

    public String getVoteCount(){
        return vote_count;
    }

    public int getId(){
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(synopsis);
        dest.writeString(posterUrl);
        dest.writeString(title);
        dest.writeDouble(userRating);
        dest.writeString(releaseDate);
        dest.writeString(vote_count);
        dest.writeString(backdropUrl);
    }

    public static final Parcelable.Creator<MovieModel> CREATOR = new Parcelable.Creator<MovieModel>() {

        @Override
        public MovieModel createFromParcel(Parcel parcel) {
            return new MovieModel(parcel);
        }

        @Override
        public MovieModel[] newArray(int size) {
            return new MovieModel[size];
        }
    };

    public MovieModel(Parcel parcel){
        this.id = parcel.readInt();
        this.synopsis = parcel.readString();
        this.posterUrl = parcel.readString();
        this.title = parcel.readString();
        this.userRating = parcel.readDouble();
        this.releaseDate = parcel.readString();
        this.vote_count = parcel.readString();
        this.backdropUrl = parcel.readString();
    }
}
