package com.example.niku.photogallerynew;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

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

    public List<GalleryItem> fetchItems() {

        List<GalleryItem> items = new ArrayList<>();

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

            JSONObject jsonBody = new JSONObject(jsonString);

            parseItems(items, jsonBody);

            //parseItems(items, jsonString);

        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse json", je);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        }

        return items;

    }

    private void parseItems_v1(List<GalleryItem> items, JSONObject jsonBody)
                throws IOException, JSONException {

        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
        JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");

        for (int i = 0; i < photoJsonArray.length(); i++) {

            JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);

            GalleryItem item = new GalleryItem();
            item.setId(photoJsonObject.getString("id"));
            item.setCaption(photoJsonObject.getString("title"));

            if (!photoJsonObject.has("url_s")) {
                continue;
            }

            item.setUrl(photoJsonObject.getString("url_s"));

            items.add(item);

        }

    }

    private void parseItems(List<GalleryItem> items, JSONObject jsonBody)
            throws IOException, JSONException {

        Gson gson = new Gson();

        /*gson.fromJson(jsonString, )*/

        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
        JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");

        for (int i = 0; i < photoJsonArray.length(); i++) {

            /*JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);*/

            GalleryItem item = gson.fromJson(photoJsonArray.getJSONObject(i).toString(), GalleryItem.class); // new GalleryItem();
             /*item.setId(photoJsonObject.getString("id"));
            item.setCaption(photoJsonObject.getString("title"));

            if (!photoJsonObject.has("url_s")) {
                continue;
            }

            item.setUrl(photoJsonObject.getString("url_s"));*/

            items.add(item);

        }

    }

}
