package ir.codetower.samanshiri.Adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.github.ybq.android.spinkit.SpinKitView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import ir.codetower.samanshiri.Activities.CategoryEditorActivity;
import ir.codetower.samanshiri.Activities.ProductListActivity;
import ir.codetower.samanshiri.App;
import ir.codetower.samanshiri.Configurations;
import ir.codetower.samanshiri.CustomViews.CustomTextView;
import ir.codetower.samanshiri.CustomViews.RoundRectCornerImageView;
import ir.codetower.samanshiri.Helpers.DeleteItemListener;
import ir.codetower.samanshiri.Helpers.ImageDownloader;
import ir.codetower.samanshiri.Helpers.Util;
import ir.codetower.samanshiri.Models.AdapterHelper;
import ir.codetower.samanshiri.Models.Category;
import ir.codetower.samanshiri.Models.Setting;
import ir.codetower.samanshiri.R;

/**
 * Created by Mr-R00t on 8/1/2017.
 */

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {
    private ArrayList<Category> categories;
    private DeleteItemListener deleteItemListener;
    private ArrayList<AdapterHelper> adapterHelpers=new ArrayList<>();
    public CategoriesAdapter(ArrayList<Category> categories, DeleteItemListener deleteItemListener){
        this.categories=categories;
        this.deleteItemListener=deleteItemListener;
        for (Category pro :categories) {
            AdapterHelper adapterHelper=new AdapterHelper();
            adapterHelper.setId(pro.getId());
            adapterHelper.setVisible(false);
            adapterHelpers.add(adapterHelper);
        }
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(App.context).inflate(R.layout.category_new_adapter,parent,false));
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        holder.bind(categories.get(position),position);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
    public void deleteProductItem(final int id){
        categories.remove(id);
    }
    public class CategoryViewHolder extends RecyclerView.ViewHolder{
        private RoundRectCornerImageView item_image;
        private SpinKitView item_load;
        private AppCompatImageView item_edit,item_remove;
        private CustomTextView item_title;
        private RelativeLayout item_root;
//        private SwipeLayout swipeLayout;
        public CategoryViewHolder(View itemView) {
            super(itemView);
            item_load= (SpinKitView) itemView.findViewById(R.id.item_load);
            Setting setting=App.prefManager.getSettings();
            item_load.setColor(Color.parseColor(setting.getPrimary_color()));
            if(App.prefManager.isAdmin()){
                item_edit= (AppCompatImageView) itemView.findViewById(R.id.item_edit);
                item_edit.setVisibility(View.VISIBLE);

                item_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(App.context, CategoryEditorActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("id",categories.get(getLayoutPosition()).getId());
                        App.context.startActivity(intent);
                    }
                });
                item_remove= (AppCompatImageView) itemView.findViewById(R.id.item_remove);
                item_remove.setVisibility(View.VISIBLE);
                item_remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos=getLayoutPosition();
                        deleteItemListener.OnDelete(categories.get(pos).getId(),pos,null);

                    }
                });


            }


            item_image= (RoundRectCornerImageView) itemView.findViewById(R.id.item_image);
//            item_description=(CustomTextView)itemView.findViewById(R.id.item_description);
            item_title= (CustomTextView) itemView.findViewById(R.id.item_title);
            item_root= (RelativeLayout) itemView.findViewById(R.id.item_root);
            RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    App.getXy().x/2-10);
            item_root.setLayoutParams(parms);
            item_root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(App.context, ProductListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("id",categories.get(getLayoutPosition()).getId());
                    intent.putExtra("section",categories.get(getLayoutPosition()).getSection());
                    App.context.startActivity(intent);
                }
            });

//            swipeLayout= (SwipeLayout) itemView.findViewById(R.id.swipe);
//            if(App.prefManager.isAdmin()){
//                swipeLayout.setSwipeEnabled(true);
//            }
//            swipeLayout.setOnSwipeListener(new SwipeLayout.OnSwipeListener() {
//                @Override
//                public void onBeginSwipe(SwipeLayout swipeLayout, boolean moveToRight) {
//
//                }
//
//                @Override
//                public void onSwipeClampReached(SwipeLayout swipeLayout, boolean moveToRight) {
//                    int pos=getLayoutPosition();
//                    deleteItemListener.OnDelete(categories.get(pos).getId(),pos,swipeLayout);
//                }
//
//                @Override
//                public void onLeftStickyEdge(SwipeLayout swipeLayout, boolean moveToRight) {
//
//                }
//
//                @Override
//                public void onRightStickyEdge(SwipeLayout swipeLayout, boolean moveToRight) {
//
//                }
//            });

        }

        public void bind(Category category, final int position) {
//            swipeLayout.reset();
            File path = new File(Configurations.sdTempFolderAddress + category.getId()+"-"+category.getUpdate_token() );
            path.mkdirs();
            final File temp = new File(path, "primary");
            final File tempT = new File(path, "primary-thumb");

            if (temp.exists()) {
                item_load.setVisibility(View.GONE);
                Picasso.with(App.context).load(tempT).into(item_image);
            } else {
                if (adapterHelpers.get(position).isVisible()) {
                    item_image.setImageResource(R.drawable.placeholder);
                    item_load.setVisibility(View.GONE);

                } else {
                    ImageDownloader.downloadImage(Configurations.apiUrl + "getCatImage?id=" + category.getId(), new ImageDownloader.OnDownloadImageCompleteListener() {
                        @Override
                        public void OnDownloadComplete(Bitmap bitmap) {

                            try {
                                File tem2p = Util.writeToFile(bitmap, temp, 100, false);
                                tem2p = tem2p.getParentFile();
                                tem2p = new File(tem2p.getAbsolutePath() + "/" + "primary-thumb");
                                Picasso.with(App.context).load(tem2p).memoryPolicy(MemoryPolicy.NO_CACHE).into(item_image);
                                AdapterHelper tempHelper=adapterHelpers.get(position);
                                adapterHelpers.set(position,tempHelper);
                                item_load.setVisibility(View.GONE);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void OnDownloadError() {
                            item_load.setVisibility(View.GONE);
                            item_image.setImageResource(R.drawable.placeholder);
                            AdapterHelper tempHelper=adapterHelpers.get(position);
                            tempHelper.setVisible(true);
                            adapterHelpers.set(position,tempHelper);
                        }
                    });
                }
            }
            item_title.setText(category.getTitle());
//            item_description.setText(category.getDescription());
        }

    }

}
