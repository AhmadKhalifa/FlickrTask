package com.orangelabs.task.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.orangelabs.task.R;
import com.orangelabs.task.model.object.Image;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ImageViewHolder> {

    private List<Image> images;
    private OnItemClickListener mOnItemClickListener;

    public GalleryAdapter(OnItemClickListener onItemClickListener){
        mOnItemClickListener = onItemClickListener;
    }

    public void updateImages(List<Image> images) {
        this.images = images;
        notifyDataSetChanged();
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.image_thumbnail,
                        parent, false));
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        holder.setContent(images.get(position));
    }

    @Override
    public int getItemCount() {
        return images == null ? 0 : images.size();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.thumbnail)
        ImageView thumbnail;

        private Image mImage;

        ImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void setContent(Image image){
            mImage = image;
            thumbnail.setImageBitmap(image.getThumbnail());
        }

        @OnClick({R.id.thumbnail})
        public void onClick(View view) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onImageClicked(view, mImage);
            }
        }
    }

    public interface OnItemClickListener {
        void onImageClicked(View view, Image image);
    }
}