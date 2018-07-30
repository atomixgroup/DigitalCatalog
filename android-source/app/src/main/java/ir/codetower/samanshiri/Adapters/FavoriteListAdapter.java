package ir.codetower.samanshiri.Adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.github.ybq.android.spinkit.SpinKitView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import ir.codetower.samanshiri.Activities.ProductActivity;
import ir.codetower.samanshiri.Activities.ProductEditorActivity;
import ir.codetower.samanshiri.App;
import ir.codetower.samanshiri.Configurations;
import ir.codetower.samanshiri.CustomViews.CustomTextView;
import ir.codetower.samanshiri.Helpers.DeleteItemListener;
import ir.codetower.samanshiri.Helpers.ImageDownloader;
import ir.codetower.samanshiri.Helpers.Util;
import ir.codetower.samanshiri.Models.AdapterHelper;
import ir.codetower.samanshiri.Models.Product;
import ir.codetower.samanshiri.Models.Setting;
import ir.codetower.samanshiri.R;
import ru.rambler.libs.swipe_layout.SwipeLayout;

/**
 * Created by Mr-R00t on 12/27/2017.
 */

public class FavoriteListAdapter extends RecyclerView.Adapter<FavoriteListAdapter.ProductListViewHolder> {
    private ArrayList<Product> products;
    private DeleteItemListener deleteItemListener;
    private ArrayList<AdapterHelper> adapterHelpers=new ArrayList<>();
    public FavoriteListAdapter(ArrayList<Product> products, DeleteItemListener deleteItemListener){
        this.products =products;
        this.deleteItemListener=deleteItemListener;
        for (Product pro :products) {
            AdapterHelper adapterHelper=new AdapterHelper();
            adapterHelper.setId(pro.getId());
            adapterHelper.setVisible(false);
            adapterHelpers.add(adapterHelper);
        }
    }

    @Override
    public FavoriteListAdapter.ProductListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FavoriteListAdapter.ProductListViewHolder(LayoutInflater.from(App.context).inflate(R.layout.category_adapter,parent,false));
    }

    @Override
    public void onBindViewHolder(FavoriteListAdapter.ProductListViewHolder holder, int position) {
        holder.bind(products.get(position),position);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }


    public void deleteProductItem(final int id){
        products.remove(id);
    }
    public class ProductListViewHolder extends RecyclerView.ViewHolder{
        private ImageView item_image;
        private AppCompatImageView item_edit;
        private CustomTextView item_title,item_description;
        private RelativeLayout item_remove;
        private LinearLayout item_root;
        private SpinKitView item_load;
        private SwipeLayout swipeLayout;
        public ProductListViewHolder(View itemView) {
            super(itemView);
            item_load= (SpinKitView) itemView.findViewById(R.id.item_load);
            Setting setting=App.prefManager.getSettings();
            item_load.setColor(Color.parseColor(setting.getPrimary_color()));
            if(App.prefManager.isAdmin()){
                item_edit= (AppCompatImageView) itemView.findViewById(R.id.item_edit);
                item_edit.setVisibility(View.VISIBLE);
                item_edit.setColorFilter(Color.parseColor(Setting.getPColor()));
                item_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(App.context, ProductEditorActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("id",products.get(getLayoutPosition()).getId());
                        App.context.startActivity(intent);
                    }
                });
            }
            item_remove= (RelativeLayout) itemView.findViewById(R.id.item_remove);
            item_image= (ImageView) itemView.findViewById(R.id.item_image);
            item_description=(CustomTextView)itemView.findViewById(R.id.item_description);
            item_title= (CustomTextView) itemView.findViewById(R.id.item_title);
            item_root= (LinearLayout) itemView.findViewById(R.id.item_root);
            item_root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(App.context, ProductActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("id",products.get(getLayoutPosition()).getId());
                    App.context.startActivity(intent);
                }
            });
            swipeLayout= (SwipeLayout) itemView.findViewById(R.id.swipe);
            if(App.prefManager.isAdmin()){
                swipeLayout.setSwipeEnabled(true);
            }
            swipeLayout.setOnSwipeListener(new SwipeLayout.OnSwipeListener() {
                @Override
                public void onBeginSwipe(SwipeLayout swipeLayout, boolean moveToRight) {

                }

                @Override
                public void onSwipeClampReached(SwipeLayout swipeLayout, boolean moveToRight) {
                    int pos=getLayoutPosition();
                    deleteItemListener.OnDelete(products.get(pos).getId(),pos,swipeLayout);
                }

                @Override
                public void onLeftStickyEdge(SwipeLayout swipeLayout, boolean moveToRight) {

                }

                @Override
                public void onRightStickyEdge(SwipeLayout swipeLayout, boolean moveToRight) {

                }
            });
            item_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos=getLayoutPosition();
                    deleteItemListener.OnDelete(products.get(pos).getId(),pos,swipeLayout);
                }
            });
        }

        public void bind(final Product product, final int position) {
            swipeLayout.reset();
            item_image.setImageResource(0);
            File path = new File(Configurations.sdTempFolderAddress + product.getCat_id()+"-"+product.getCat_update_token()+"/"+product.getId()+"-"+product.getUpdate_token() );
            path.mkdirs();
            final File temp = new File(path, "primary");
            final File tempT = new File(path, "primary-thumb");
            if(product.isVideo()){
                Picasso.with(App.context).load(product.getVideoThumb()).placeholder(R.drawable.placeholder).into(item_image);
                item_load.setVisibility(View.GONE);
            }else {
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
                                    Picasso.with(App.context).load(tem2p).into(item_image);
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
            item_description.setText(product.getDescription());
        }

    }
}
