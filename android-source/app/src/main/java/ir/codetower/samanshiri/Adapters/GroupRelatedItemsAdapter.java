package ir.codetower.samanshiri.Adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ybq.android.spinkit.SpinKitView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import ir.codetower.samanshiri.Activities.ProductActivity;
import ir.codetower.samanshiri.App;
import ir.codetower.samanshiri.Configurations;
import ir.codetower.samanshiri.CustomViews.CustomTextView;
import ir.codetower.samanshiri.CustomViews.RoundRectCornerImageView;
import ir.codetower.samanshiri.Helpers.ImageDownloader;
import ir.codetower.samanshiri.Helpers.Util;
import ir.codetower.samanshiri.Models.AdapterHelper;
import ir.codetower.samanshiri.Models.Product;
import ir.codetower.samanshiri.Models.Setting;
import ir.codetower.samanshiri.R;

/**
 * Created by Mr-R00t on 8/1/2017.
 */

public class GroupRelatedItemsAdapter extends RecyclerView.Adapter<GroupRelatedItemsAdapter.RelatedItemsViewHolder> {
    private ArrayList<Product> products;
    private ArrayList<AdapterHelper> adapterHelpers=new ArrayList<>();
    public GroupRelatedItemsAdapter(ArrayList<Product> products) {
        this.products = products;
        for (Product pro :products) {
            AdapterHelper adapterHelper=new AdapterHelper();
            adapterHelper.setId(pro.getId());
            adapterHelper.setVisible(false);
            adapterHelpers.add(adapterHelper);
        }
    }

    @Override
    public RelatedItemsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RelatedItemsViewHolder(LayoutInflater.from(App.context).inflate(R.layout.group_adapter, parent, false));
    }

    @Override
    public void onBindViewHolder(RelatedItemsViewHolder holder, int position) {
        holder.bind(products.get(position),position);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class RelatedItemsViewHolder extends RecyclerView.ViewHolder {
        private RoundRectCornerImageView item_image;
        private SpinKitView item_load;
        private CustomTextView item_title;
        private CardView item_root;

        public RelatedItemsViewHolder(View itemView) {
            super(itemView);
            item_load = (SpinKitView) itemView.findViewById(R.id.item_load);
            Setting setting=App.prefManager.getSettings();
            item_load.setColor(Color.parseColor(setting.getPrimary_color()));
            item_image = (RoundRectCornerImageView) itemView.findViewById(R.id.item_image);
            item_title = (CustomTextView) itemView.findViewById(R.id.item_title);
            item_root = (CardView) itemView.findViewById(R.id.item_root);
            item_root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(App.context, ProductActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("id", products.get(getLayoutPosition()).getId());
                    App.context.startActivity(intent);
                }
            });
        }

        public void bind(final Product product, final int position) {
            item_load.setVisibility(View.VISIBLE);
            item_image.setImageResource(0);
            File path = new File(Configurations.sdTempFolderAddress + product.getCat_id()+"-"+ product.getCat_update_token()+ "/" + product.getId()+"-"+product.getUpdate_token());
            path.mkdirs();
            final File temp = new File(path, "primary");
            final File tempT = new File(path, "primary-thumb");
            if(product.isVideo()){
                Picasso.with(App.context).load(product.getVideoThumb()).placeholder(R.drawable.placeholder).into(item_image);
                item_load.setVisibility(View.GONE);
            }
            else {
                if (temp.exists()) {
                    item_load.setVisibility(View.GONE);
                    Picasso.with(App.context).load(tempT).into(item_image);
                } else {
                    if (adapterHelpers.get(position).isVisible()) {
                        item_image.setImageResource(R.drawable.placeholder);
                        item_load.setVisibility(View.GONE);

                    } else {
                        ImageDownloader.downloadImage(Configurations.apiUrl + "getImage?id=" + product.getId() + "&name=primary", new ImageDownloader.OnDownloadImageCompleteListener() {
                            @Override
                            public void OnDownloadComplete(Bitmap bitmap) {

                                try {
                                    File tem2p = Util.writeToFile(bitmap, temp, 100, false);
                                    tem2p = tem2p.getParentFile();
                                    tem2p = new File(tem2p.getAbsolutePath() + "/" + "primary-thumb");
                                    Picasso.with(App.context).load(tem2p).memoryPolicy(MemoryPolicy.NO_CACHE).into(item_image);
                                    AdapterHelper tempHelper = adapterHelpers.get(position);
                                    tempHelper.setVisible(true);
                                    adapterHelpers.set(position, tempHelper);
                                    item_load.setVisibility(View.GONE);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void OnDownloadError() {
                                item_load.setVisibility(View.GONE);
                                item_image.setImageResource(R.drawable.placeholder);
                                AdapterHelper tempHelper = adapterHelpers.get(position);
                                tempHelper.setVisible(true);
                                adapterHelpers.set(position, tempHelper);
                            }
                        });
                    }
                }
            }

            item_title.setText(product.getTitle());
        }
    }
}
