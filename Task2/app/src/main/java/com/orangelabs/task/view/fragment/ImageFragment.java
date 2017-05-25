package com.orangelabs.task.view.fragment;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.orangelabs.task.R;
import com.orangelabs.task.model.object.Image;
import com.orangelabs.task.model.storage.sqlite.FlickrCache;
import com.orangelabs.task.presenter.ImageGetterPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImageFragment extends DialogFragment
        implements ImageGetterPresenter.GetFullSizeImageCallback {

    public static final String ARGUMENT_FLICKR_CACHE = "argument_flickr_cache";
    public static final String ARGUMENT_IMAGE = "argument_image";

    @BindView(R.id.image_preview)
    ImageView mPreviewImageView;

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    private Image mImage;
    private FlickrCache mFlickrCache;
    private ImageGetterPresenter mImageGetterPresenter;

    public static ImageFragment getInstance(FlickrCache flickrCache, Image image) {
        ImageFragment fragment = new ImageFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(ARGUMENT_FLICKR_CACHE, flickrCache);
        arguments.putSerializable(ARGUMENT_IMAGE, image);
        fragment.setArguments(arguments);
        return fragment;
    }

    public ImageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        ButterKnife.bind(this, view);
        if (getArguments() != null && getArguments().containsKey(ARGUMENT_IMAGE) &&
                getArguments().containsKey(ARGUMENT_FLICKR_CACHE)) {
            mFlickrCache = (FlickrCache) getArguments().getSerializable(ARGUMENT_FLICKR_CACHE);
            mImage = (Image) getArguments().getSerializable(ARGUMENT_IMAGE);
            if (mFlickrCache != null && mImage != null) {
                if (mImage.getThumbnail() != null) {
                    mPreviewImageView.setImageBitmap(mImage.getThumbnail());
                    mProgressBar.setVisibility(View.VISIBLE);
                }
                mImageGetterPresenter = new ImageGetterPresenter();
                mImageGetterPresenter.getFullSizeImage(this, getContext(), mImage);
            }
        }
        return view;
    }

    @Override
    public void onGetFullSizeImageSuccess(Image fullSizeImage) {
        if (fullSizeImage != null) {
            mProgressBar.setVisibility(View.INVISIBLE);
            mPreviewImageView.setImageBitmap(fullSizeImage.getFullSized());
        }
    }

    @Override
    public void onGetFullSizeImageFailure(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
