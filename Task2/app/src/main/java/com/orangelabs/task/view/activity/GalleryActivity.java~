package com.orangelabs.task.view.activity;

import android.content.res.Configuration;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orangelabs.task.R;
import com.orangelabs.task.model.object.Image;
import com.orangelabs.task.model.storage.sqlite.FlickrCache;
import com.orangelabs.task.presenter.ImageGetterPresenter;
import com.orangelabs.task.utility.RowItemsCalculator;
import com.orangelabs.task.utility.UserCommunication;
import com.orangelabs.task.view.adapter.GalleryAdapter;
import com.orangelabs.task.view.fragment.ImageFragment;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class GalleryActivity extends AppCompatActivity
        implements ImageGetterPresenter.SearchForImagesCallback,
        GalleryAdapter.OnItemClickListener,
        ImageGetterPresenter.GetThumbnailSizeImageCallback,
        TextView.OnEditorActionListener {

    private static final int GRID_ITEM_WIDTH = 120;

    @BindView(R.id.activity_gallery)
    LinearLayout mRootView;

    @BindView(R.id.edit_text_search)
    EditText mSearchEditText;

    @BindView(R.id.gallery_recycler_view)
    RecyclerView mRecyclerView;

    private ImageGetterPresenter mImageGetterPresenter;
    private FlickrCache mFlickrCache;
    private GalleryAdapter mGalleryAdapter;
    private UserCommunication mUserCommunication;
    private GridLayoutManager mGridLayoutManager;

    @BindString(R.string.api_key)
    String APIKey;

    private int pageNumber = 0;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mGridLayoutManager = new GridLayoutManager(GalleryActivity.this,
                RowItemsCalculator.getNumberOfColumns(this, GRID_ITEM_WIDTH));
        mRecyclerView.setLayoutManager(mGridLayoutManager);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        initialize();
    }

    private void initialize(){
        ButterKnife.bind(this);
        mImageGetterPresenter = new ImageGetterPresenter();
        mFlickrCache = new FlickrCache(this);
        mUserCommunication = new UserCommunication(mRootView);
        mGridLayoutManager = new GridLayoutManager(GalleryActivity.this,
                RowItemsCalculator.getNumberOfColumns(this, GRID_ITEM_WIDTH));
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mGalleryAdapter = new GalleryAdapter(this);
        mRecyclerView.setAdapter(mGalleryAdapter);
        mSearchEditText.setOnEditorActionListener(this);
    }

    private String getSearchKeyword() {
        return mSearchEditText != null ? mSearchEditText.getText().toString() : "";
    }

    @Override
    public void onSearchForImagesSuccess(List<Image> images) {
        if (images != null) {
            if (mUserCommunication != null) {
                mUserCommunication.dismissMessageIfAny();
            }
            mGalleryAdapter.updateImages(images);
            for (Image image : images) {
                mImageGetterPresenter.getThumbnailSizeImage(this, this, image);
            }
        }
    }

    @Override
    public void onSearchForImagesFailure(String message) {
        if (mUserCommunication != null) {
            mUserCommunication.showMessage(message);
        }
    }

    @Override
    public void onImageClicked(View view, Image image) {
        Toast.makeText(this, new FlickrCache(this).getLastQuery().getImagesIds().size() + "", Toast.LENGTH_LONG).show();
        if (image != null) {
            ImageFragment fragment = ImageFragment.getInstance(mFlickrCache, image);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            fragment.show(transaction, "Image_Fragment");
        }
    }

    @Override
    public void onGetThumbnailSizeImageSuccess(Image fullSizeImage) {
        if (fullSizeImage != null) {
            mGalleryAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onGetThumbnailSizeImageFailure(String message) {
        if (mUserCommunication != null) {
            mUserCommunication.showMessage(message);
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            pageNumber = 0;
            mUserCommunication.showMessage("Loading...", Snackbar.LENGTH_INDEFINITE);
            mImageGetterPresenter.searchForImages(
                    GalleryActivity.this,
                    APIKey,
                    this,
                    getSearchKeyword(),
                    pageNumber
            );
            return true;
        }
        return false;
    }
}
