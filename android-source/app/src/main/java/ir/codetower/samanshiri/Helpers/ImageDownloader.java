package ir.codetower.samanshiri.Helpers;

import android.graphics.Bitmap;
import android.support.v7.widget.AppCompatImageView;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import ir.codetower.samanshiri.App;
import ir.codetower.samanshiri.CustomViews.TouchImageView;


public class ImageDownloader {
    public void downloadImage(final String url, final AppCompatImageView imageView) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final AppCompatImageView finalImageview = imageView;
                ImageRequest request = new ImageRequest(url, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        finalImageview.setImageBitmap(response);
                    }
                }, 0, 0, null, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                Volley.newRequestQueue(App.context).add(request);
            }
        }).start();



    }

    public static void downloadImage(final String url, final ImageView imageView) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final ImageView finalImageview = imageView;
                ImageRequest request = new ImageRequest(url, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        finalImageview.setImageBitmap(response);
                    }
                }, 0, 0, null, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                Volley.newRequestQueue(App.context).add(request);
            }}).start();

    }

    public static void downloadImage(final String url, final TouchImageView imageView) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final TouchImageView finalImageview = imageView;
                ImageRequest request = new ImageRequest(url, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        finalImageview.setImageBitmap(response);
                    }
                }, 0, 0, null, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                Volley.newRequestQueue(App.context).add(request);
            }}).start();

    }

    public static void downloadImage(final String url, final OnDownloadImageCompleteListener onDownloadImageCompleteListener) {


        ImageRequest request = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                onDownloadImageCompleteListener.OnDownloadComplete(response);
            }
        }, 0, 0, null, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onDownloadImageCompleteListener.OnDownloadError();
            }
        });
        Volley.newRequestQueue(App.context).add(request);

    }


    public interface OnDownloadImageCompleteListener {
        public void OnDownloadComplete(Bitmap bitmap);

        public void OnDownloadError();
    }
}
