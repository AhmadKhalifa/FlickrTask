package com.orangelabs.task.model.object;

import java.util.ArrayList;
import java.util.List;

public class SearchQuery {

    private String mKeyword;
    private List<String> mImagesIds;

    public SearchQuery(String keyword) {
        this.mKeyword = keyword;
        this.mImagesIds = new ArrayList<>();
    }

    public String getKeyword() {
        return mKeyword;
    }

    public List<String> getImagesIds() {
        return mImagesIds;
    }

    public void addImageId(String imageID) {
        mImagesIds.add(imageID);
    }
}
