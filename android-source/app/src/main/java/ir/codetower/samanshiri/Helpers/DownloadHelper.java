package ir.codetower.samanshiri.Helpers;

import android.widget.ImageView;

import com.github.ybq.android.spinkit.SpinKitView;

/**
 * Created by Mr-R00t on 12/25/2017.
 */

public class DownloadHelper {
    private int id;
    private SpinKitView loading;
    private ImageView image;
    public DownloadHelper(int id,SpinKitView loading,ImageView image){
        this.id=id;
        this.image=image;
        this.loading=loading;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SpinKitView getLoading() {
        return loading;
    }

    public void setLoading(SpinKitView loading) {
        this.loading = loading;
    }

    public ImageView getImage() {
        return image;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }
}
