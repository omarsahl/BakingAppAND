package com.os.bakingapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Omar on 14-May-18 11:17 AM.
 */
public class RecipePreview implements Parcelable {
    @SerializedName("id")
    public long id;

    @SerializedName("name")
    public String recipeName;


    @SerializedName("image")
    public String imageUrl;

    public int servings;

    public RecipePreview() { }

    public RecipePreview(long id, String recipeName, int servings, String imageUrl) {
        this.id = id;
        this.recipeName = recipeName;
        this.servings = servings;
        this.imageUrl = imageUrl;
    }

    protected RecipePreview(Parcel in) {
        id = in.readLong();
        recipeName = in.readString();
        imageUrl = in.readString();
        servings = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(recipeName);
        dest.writeString(imageUrl);
        dest.writeInt(servings);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RecipePreview> CREATOR = new Creator<RecipePreview>() {
        @Override
        public RecipePreview createFromParcel(Parcel in) {
            return new RecipePreview(in);
        }

        @Override
        public RecipePreview[] newArray(int size) {
            return new RecipePreview[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecipePreview that = (RecipePreview) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
