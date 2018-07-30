package ir.codetower.samanshiri.Adapters;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.ybq.android.spinkit.SpinKitView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import ir.codetower.samanshiri.App;
import ir.codetower.samanshiri.Configurations;
import ir.codetower.samanshiri.Helpers.ImageDownloader;
import ir.codetower.samanshiri.Helpers.Util;
import ir.codetower.samanshiri.Models.AdapterHelper;
import ir.codetower.samanshiri.Models.Product;
import ir.codetower.samanshiri.Models.Setting;
import ir.codetower.samanshiri.R;


public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {
    private Product product;
    private ArrayList<AdapterHelper> adapterHelpers=new ArrayList<>();
    private ClickOnAlbumImageListener clickOnAlbumImageListener;
    public AlbumAdapter(Product product,ClickOnAlbumImageListener clickOnAlbumImageListener) {
        this.clickOnAlbumImageListener=clickOnAlbumImageListener;
        this.product=product;
        for(int i=0;i<6;i++){
            AdapterHelper adapterHelper=new AdapterHelper();
            adapterHelper.setId(i);
            adapterHelpers.add(adapterHelper);
        }
    }

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(App.context).inflate(R.layout.album_adapter, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlbumViewHolder holder, int position) {
        holder.bind();
    }

    @Override
    public int getItemCount() {
        return 6;
    }

    public class AlbumViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private SpinKitView loading ;
        private ImageView item_image;
        private String path;
        private LinearLayout item_root;
        public AlbumViewHolder(View itemView) {
            super(itemView);
            item_image=itemView.findViewById(R.id.item_image);
            loading=itemView.findViewById(R.id.item_load);
            item_root=itemView.findViewById(R.id.item_root);
            item_root.setOnClickListener(this);
            loading.setColor(Color.parseColor(Setting.getPColor()));
            path = Configurations.sdTempFolderAddress + product.getCat_id()+"-"+product.getCat_update_token()+ "/" + product.getId()+"-"+product.getUpdate_token();
        }

        public void bind() {
            final int position=getLayoutPosition();
            final String name="p" + (position+1);
            File temp = new File(path, "/"+name);

            if (temp.exists()) {
               Picasso.with(App.context).load(temp).into(item_image);
               loading.setVisibility(View.GONE);
            } else {
                if (adapterHelpers.get(position).isVisible()) {
                    item_image.setImageResource(R.drawable.placeholder);
                    loading.setVisibility(View.GONE);
                } else {
                    ImageDownloader.downloadImage(Configurations.apiUrl + "getImage?id=" + product.getId() + "&name=" + name, new ImageDownloader.OnDownloadImageCompleteListener() {
                        @Override
                        public void OnDownloadComplete(Bitmap bitmap) {

                            loading.setVisibility(View.GONE);
                            File pathFile = new File(path);
                            pathFile.mkdirs();
                            File temp = new File(path, name);

                            try {
                                File newLoad=Util.writeToFile(bitmap, temp, 100, false);
                                Picasso.with(App.context).load(newLoad).into(item_image);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            AdapterHelper tempHelper = adapterHelpers.get(position);
                            tempHelper.setVisible(true);
                            adapterHelpers.set(position, tempHelper);
                        }

                        @Override
                        public void OnDownloadError() {
                            loading.setVisibility(View.GONE);
                            item_image.setImageResource(R.drawable.placeholder);
                            AdapterHelper tempHelper = adapterHelpers.get(position);
                            tempHelper.setVisible(true);
                            adapterHelpers.set(position, tempHelper);
                        }
                    });

                }
            }
        }

        @Override
        public void onClick(View v) {
            if(clickOnAlbumImageListener!=null){
                final String name="p" + (getLayoutPosition()+1);
                File temp = new File(path, "/"+name);
                clickOnAlbumImageListener.onClick(temp);
            }
        }
    }
    public interface ClickOnAlbumImageListener{
        public void onClick(File image);

    }

}