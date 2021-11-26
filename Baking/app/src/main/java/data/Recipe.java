package data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Recipe implements Parcelable {
    private String mName;
    private List<String> mIngredients;
    private ArrayList<Step> mSteps;
    private int mServings;
    private String mImage;

    public Recipe () {}

    public Recipe (String name, List<String> ingredients, ArrayList<Step> steps, int servings, String image) {
        mName = name;
        mIngredients = ingredients;
        mSteps = steps;
        mServings = servings;
        mImage = image;
    }

    protected Recipe (Parcel in) {
        mName = in.readString();
        mIngredients = new ArrayList<>();
        in.readStringList(mIngredients);
        mSteps = new ArrayList<Step>();
        in.readTypedList(mSteps, Step.CREATOR);
        mServings = in.readInt();
        mImage = in.readString();
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    public static class Step implements Parcelable{
        private String mShortDescription;
        private String mDescription;
        private String mVideoUrl;
        private String mThumbnailUrl;

        public Step () {}

        public Step(String shortDescription, String description, String videoUrl, String thumbnailUrl) {
            mShortDescription = shortDescription;
            mDescription = description;
            mVideoUrl = videoUrl;
            mThumbnailUrl = thumbnailUrl;
        }

        protected Step (Parcel in) {
            mShortDescription = in.readString();
            mDescription = in.readString();
            mVideoUrl = in.readString();
            mThumbnailUrl = in.readString();
        }

        public static final Creator<Step> CREATOR = new Creator<Step>() {
            @Override
            public Step createFromParcel(Parcel in) {
                return new Step(in);
            }

            @Override
            public Step[] newArray(int size) {
                return new Step[size];
            }
        };

        public String getShortDescription () { return mShortDescription; }

        public void setShortDescription (String shortDescription) { mShortDescription = shortDescription; }

        public String getDescription() { return mDescription; }

        public void setDescription (String description) { mDescription = description; }

        public String getVideoUrl () { return mVideoUrl; }

        public void setVideoUrl (String videoUrl) { mVideoUrl = videoUrl; }

        public String getThumbnailUrl () { return mThumbnailUrl; }

        public void setThumbnailUrl (String thumbnailUrl) { mThumbnailUrl = thumbnailUrl; }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(mShortDescription);
            dest.writeString(mDescription);
            dest.writeString(mVideoUrl);
            dest.writeString(mThumbnailUrl);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeStringList(mIngredients);
        dest.writeTypedList(mSteps);
        dest.writeInt(mServings);
        dest.writeString(mImage);
    }

    public String getName () { return mName; }

    public void setName (String name) { mName = name; }

    public List<String> getIngredients () { return mIngredients; }

    public void setIngredients (List<String> ingredients) { mIngredients = ingredients; }

    public ArrayList<Step> getSteps () { return mSteps; }

    public void setSteps (ArrayList<Step> steps) { mSteps = steps; }

    public int getServings () { return mServings; }

    public void setServings (int servings) { mServings = servings; }

    public String getImage () { return mImage; }

    public void setImage (String image) { mImage = image; }
}
