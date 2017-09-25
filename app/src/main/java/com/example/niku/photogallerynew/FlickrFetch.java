package com.example.niku.photogallerynew;

import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by niku on 25.09.2017.
 */

public class FlickrFetch {

    private static final String TAG = "FlickrFetch";

    private static final String API_KEY = "3fa6469a4cdf11bf90493c2d344078ee";
    private static final String REST_URI = "https://api.flickr.com/services/rest/";
    private static final String REST_METHOD_GET_RECENT_PHOTOS = "flickr.photos.getRecent";

    public byte[] getUrlBytes(String urlSpec) throws IOException {

        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " + urlSpec);
            }

            int bytesRead;

            byte[] buffer = new byte[1024];

            while ( (bytesRead = in.read(buffer)) > 0 ) {
                out.write(buffer, 0, bytesRead);
            }

            out.close();

            return out.toByteArray();

        } finally {
            connection.disconnect();
        }

    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public void fetchItems() {
        try {
            String url = Uri.parse(REST_URI)
                    .buildUpon()
                    .appendQueryParameter("method", REST_METHOD_GET_RECENT_PHOTOS)
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("extras", "url_s")
                    .build().toString();

            String jsonString = getUrlString(url);

            Log.i(TAG, "Recieved json" + jsonString);

        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items:" + ioe.toString());
        }
    }

}