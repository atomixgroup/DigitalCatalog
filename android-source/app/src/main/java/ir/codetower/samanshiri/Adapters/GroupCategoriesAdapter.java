package ir.codetower.samanshiri.Adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ybq.android.spinkit.SpinKitView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import ir.codetower.samanshiri.Activities.ProductListActivity;
import ir.codetower.samanshiri.App;
import ir.codetower.samanshiri.Configurations;
import ir.codetower.samanshiri.CustomViews.CustomTextView;
import ir.codetower.samanshiri.CustomViews.RoundRectCornerImageView;
import ir.codetower.samanshiri.Helpers.ImageDownloader;
import ir.codetower.samanshiri.Helpers.Util;
import ir.codetower.samanshiri.Models.Category;
import ir.codetower.samanshiri.R;

/**
 * Created by Mr-R00t on 8/1/2017.
 */

public class GroupCategoriesAdapter extends RecyclerView.Adapter<GroupCategoriesAdapter.CategoryViewHolder> {
    private ArrayList<Category> categories;

    public GroupCategoriesAdapter(ArrayList<Category> categories)
    {

        this.categories=categories;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(App.context).inflate(R.layout.group_adapter,parent,false));
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        holder.bind(categories.get(position));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder{
        private RoundRectCornerImageView item_image;
        private CustomTextView item_title;
        private CardView item_root;
        private SpinKitView item_load;
        public CategoryViewHolder(View itemView) {
            super(itemView);
            item_load= (SpinKitView) itemView.findViewById(R.id.item_load);
            item_image= (RoundRectCornerImageView) itemView.findViewById(R.id.item_image);
            item_title= (CustomTextView) itemView.findViewById(R.id.item_title);
            item_root= (CardView) itemView.findViewById(R.id.item_root);
            item_root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(App.context, ProductListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("id",categories.get(getLayoutPosition()).getId());
                    App.context.startActivity(intent);
                }
            });
        }

        public void bind(Category category) {

            File path = new File(Configurations.sdTempFolderAddress + category.getId()+"-"+category.getUpdate_token());
            path.mkdirs();
            final File temp = new File(path, "primary");

            final File tempT = new File(path, "primary-thumb");

            if (temp.exists()) {
                item_load.setVisibility(View.GONE);
                Picasso.with(App.context).load(tempT).into(item_image);
            } else {
                ImageDownloader.downloadImage(Configurations.apiUrl + "getCatImage?id=" + category.getId(), new ImageDownloader.OnDownloadImageCompleteListener() {
                    @Override
                    public void OnDownloadComplete(Bitmap bitmap) {

                        try {
                            File tem2p = Util.writeToFile(bitmap, temp, 100, false);
                            tem2p = tem2p.getParentFile();
                            tem2p = new File(tem2p.getAbsolutePath() + "/" + "primary-thumb");
                            Picasso.with(App.context).load(tem2p).into(item_image);
                            item_load.setVisibility(View.GONE);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void OnDownloadError() {
                        item_load.setVisibility(View.GONE);
                        item_image.setImageResource(R.drawable.placeholder);
                    }
                });
            }
            item_title.setText(category.getTitle());

        }
    }

}
