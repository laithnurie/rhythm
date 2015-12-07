package com.laithlab.core.dto;


import android.os.Parcel;
import android.os.Parcelable;

public class SearchResult implements Parcelable {

    final private String id;
    final private String mainTitle;
    final private String subTitle;
    final private ResultType resultType;

    public enum ResultType {
        HEADER, ARTIST, ALBUM, SONG
    }

    private SearchResult(SearchResultBuilder builder){
        this.id = builder.id;
        this.mainTitle = builder.mainTitle;
        this.subTitle = builder.subTitle;
        this.resultType = builder.resultType;
    }

    public String getId() {
        return id;
    }

    public String getMainTitle() {
        return mainTitle;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public ResultType getResultType(){
        return resultType;
    }


    public static class SearchResultBuilder {
        public String id;
        private String mainTitle;
        private String subTitle;
        public ResultType resultType;

        public SearchResultBuilder id(String id){
            this.id = id;
            return this;
        }

        public SearchResultBuilder mainTitle(String mainTitle){
            this.mainTitle = mainTitle;
            return this;
        }

        public SearchResultBuilder subTitle(String subTitle){
            this.subTitle = subTitle;
            return this;
        }

        public SearchResultBuilder setResultType(ResultType resultType){
            this.resultType = resultType;
            return this;
        }

        public SearchResult build(){
            return new SearchResult(this);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.mainTitle);
        dest.writeString(this.subTitle);
        dest.writeString(String.valueOf(this.resultType));
    }

    protected SearchResult(Parcel in) {
        this.id = in.readString();
        this.mainTitle = in.readString();
        this.subTitle = in.readString();
        this.resultType = ResultType.valueOf(in.readString());
    }

    public static final Parcelable.Creator<SearchResult> CREATOR = new Parcelable.Creator<SearchResult>() {
        public SearchResult createFromParcel(Parcel source) {
            return new SearchResult(source);
        }

        public SearchResult[] newArray(int size) {
            return new SearchResult[size];
        }
    };

    @Override
    public String toString() {
        return "SearchResult{" +
                "id='" + id + '\'' +
                "mainTitle='" + mainTitle + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", resultType='" + resultType + '\'' +
                '}';
    }
}


