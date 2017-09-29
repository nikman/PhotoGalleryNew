package com.example.niku.photogallerynew;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by niku on 23.09.2017.
 */

public class PhotoGalleryFragment extends Fragment {

    private RecyclerView mPhotoRecyclerView;
    private List<GalleryItem> mItems = new ArrayList<>();
    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;

    private static final String TAG = "PhotoGalleryFragment";

    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemsTask().execute();

        Intent i = PollService.newIntent(getActivity());
        getActivity().startService(i);

        Handler responseHandler = new Handler();
        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
        mThumbnailDownloader.setThumbnailDownloadListener(
                new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>() {
                    @Override
                    public void onThumbnailDownloaded(PhotoHolder photoHolder, Bitmap thumbnail) {
                        Drawable drawable = new BitmapDrawable(getResources(), thumbnail);
                        photoHolder.bindDrawable(drawable);
                    }
                }
        );
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        Log.i(TAG, "Background thread started");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        //return super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        mPhotoRecyclerView = v.findViewById(R.id.fragment_photo_gallery_recycler_view);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        setupAdapter();

        return v;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailDownloader.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailDownloader.quit();
        Log.i(TAG, "Background thread destroyed");
    }

    private void setupAdapter() {
        if (isAdded()) {
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
        }
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>> {
        @Override
        protected List<GalleryItem> doInBackground(Void... params) {

            /*try {
                String result = new FlickrFetch().getUrlString("https://bignerdranch.com");
                Log.i(TAG, "Fetched contents of URL: " + result);
            } catch (IOException ioe) {
                Log.e(TAG, "Failed to fetch URL: " + ioe.toString());
            }*/

            //List<GalleryItem> galleryItemList = new FlickrFetch().fetchItems();

            return new FlickrFetch().fetchItems();
        }

        @Override
        protected void onPostExecute(List<GalleryItem> galleryItems) {
            mItems = galleryItems;
            setupAdapter();
        }
    }

    // Предоставляет ссылку на вьюхи для каждого элемента данных
    private class PhotoHolder extends RecyclerView.ViewHolder {

        //private TextView mTitleTextView;
        private ImageView mItemImageView;

        public PhotoHolder(View itemView) {
            super(itemView);
            //mTitleTextView = (TextView) itemView;
            mItemImageView = itemView.findViewById(R.id.fragment_photo_gallery_image_view);
        }

        /*public void bindGalleryItem(GalleryItem galleryItem) {
            mTitleTextView.setText(galleryItem.toString());
        }*/

        public void bindDrawable(Drawable drawable) {
            mItemImageView.setImageDrawable(drawable);
        }

        public void bindGalleryItem(GalleryItem galleryItem) {
            Picasso.with(getActivity())
                    .load(galleryItem.getUrl())
                    .placeholder(R.drawable.empty_image_gray)
                    .into(mItemImageView);
        }

    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        private List<GalleryItem> mGalleryItems;

        public PhotoAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }

        // Создаёт новые вьюхи. Вызывается из LayoutManager'a
        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            /*TextView textView = new TextView(getActivity());
            return new PhotoHolder(textView);*/
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.gallery_item, parent, false);
            return new PhotoHolder(view);
        }

        // Заменяет содержимое вьюхи (invoked by the layout manager)
        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {

            GalleryItem galleryItem = mGalleryItems.get(position);
            //holder.bindGalleryItem(galleryItem);
            //holder.bindDrawable(galleryItem);
            /*Drawable placeholder;
            if (position % 2 == 0)
            {
                placeholder = getResources().getDrawable(R.drawable.empty_image_snowman);
            }
            else
            {
                placeholder = getResources().getDrawable(R.drawable.empty_image_bell);
            }
            holder.bindDrawable(placeholder);
            mThumbnailDownloader.queueThumbnail(holder, galleryItem.getUrl());*/

            holder.bindGalleryItem(galleryItem);

        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }

}
