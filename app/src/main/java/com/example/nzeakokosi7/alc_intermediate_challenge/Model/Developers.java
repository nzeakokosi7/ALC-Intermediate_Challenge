package com.example.nzeakokosi7.alc_intermediate_challenge.Model;

/**
 * Created by nzeakokosi7 on 8/28/17.
 */

public class Developers {
    private String mNames;
    private String mImageUrl;
    private String mGithubUrl;

    public Developers(String mNames, String mImageUrl, String mGithubUrl) {
        this.mNames = mNames;
        this.mImageUrl = mImageUrl;
        this.mGithubUrl = mGithubUrl;
    }

    public String getmNames() {
        return mNames;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public String getmGithubUrl() {
        return mGithubUrl;
    }
}