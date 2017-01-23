package com.app.movie.cinephilia.CastandCrew;

/**
 * Created by GAURAV on 20-05-2016.
 */

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by GAURAV on 22-01-2016.
 */
public class MovieCreditsModel implements Parcelable {
    public String mCharacter;
    public String mName;
    public String mProfile_path;
    private String posterUrl = null;

    public MovieCreditsModel(String character, String name, String profile_path){
        this.mCharacter = character;
        this.mName = name;
        this.mProfile_path = profile_path;
    }

    MovieCreditsModel(Parcel parcel){
        this.mCharacter = parcel.readString();
        this.mName = parcel.readString();
        this.mProfile_path = parcel.readString();
    }

    public String getPosterUrl(){
        this.posterUrl = "http://image.tmdb.org/t/p/w185" + mProfile_path;
        return this.posterUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeString(mCharacter);
        dest.writeString(mName);
        dest.writeString(mProfile_path);
    }

    public static final Parcelable.Creator<MovieCreditsModel> CREATOR = new Parcelable.Creator<MovieCreditsModel>() {

        @Override
        public MovieCreditsModel createFromParcel(Parcel parcel) {
            return new MovieCreditsModel(parcel);
        }

        @Override
        public MovieCreditsModel[] newArray(int size) {
            return new MovieCreditsModel[size];
        }
    };
}
