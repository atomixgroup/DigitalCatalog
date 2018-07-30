package ir.codetower.samanshiri.Adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.github.ybq.android.spinkit.SpinKitView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import ir.codetower.samanshiri.Activities.ProductActivity;
import ir.codetower.samanshiri.Activities.ProductEditorActivity;
import ir.codetower.samanshiri.App;
import ir.codetower.samanshiri.Configurations;
import ir.codetower.samanshiri.CustomViews.CustomTextView;
import ir.codetower.samanshiri.Helpers.ImageDownloader;
import ir.codetower.samanshiri.Helpers.Util;
import ir.codetower.samanshiri.Helpers.WebService;
import ir.codetower.samanshiri.Models.AdapterHelper;
import ir.codetower.samanshiri.Models.Product;
import ir.codetower.samanshiri.Models.Setting;
import ir.codetower.samanshiri.R;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ProductListViewHolder> implements Filterable {
    private ArrayList<Product> products;
    private ArrayList<Product> mFilteredList;
    private ArrayList<AdapterHelper> adapterHelpers=new ArrayList<>();
    public SearchAdapter(ArrayList<Product> arrayList) {
        products = arrayList;
        mFilteredList = arrayList;
        for (Product pro :products) {
            AdapterHelper adapterHelper=new AdapterHelper();
            adapterHelper.setId(pro.getId());
            adapterHelper.setVisible(false);
            adapterHelpers.add(adapterHelper);
        }
    }

    @Override
    public SearchAdapter.ProductListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.category_adapter, viewGroup, false);
        return new SearchAdapter.ProductListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductListViewHolder viewHolder, int i) {
        viewHolder.bind(mFilteredList.get(i),i);
    }

    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    mFilteredList = products;
                } else {

                    ArrayList<Product> filteredList = new ArrayList<>();

                    for (Product product : products) {

                        if (product.getTitle().toLowerCase().contains(charString) || product.getDescription().toLowerCase().contains(charString) ) {

                            filteredList.add(product);
                        }
                    }

                    mFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (ArrayList<Product>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ProductListViewHolder extends RecyclerView.ViewHolder{
        private ImageView item_image;
        private AppCompatImageView item_edit;
        private CustomTextView item_title,item_description;
        private RelativeLayout item_remove;
        private LinearLayout item_root;
        private SpinKitView item_load;
        public ProductListViewHolder(View itemView) {
            super(itemView);
            item_load= (SpinKitView) itemView.findViewById(R.id.item_load);
            Setting setting= App.prefManager.getSettings();
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
                        intent.putExtra("id",mFilteredList.get(getLayoutPosition()).getId());
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
                    intent.putExtra("id",mFilteredList.get(getLayoutPosition()).getId());
                    App.context.startActivity(intent);
                }
            });

            item_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteItem(mFilteredList.get(getLayoutPosition()).getId());
                    mFilteredList.remove(getLayoutPosition());
                }
            });
        }

        public void bind(final Product product, final int position) {
            item_image.setImageResource(0);
            File path = new File(Configurations.sdTempFolderAddress + product.getCat_id()+"-"+product.getCat_update_token()+"/"+product.getId()+"-"+product.getUpdate_token() );
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
                    ImageDownloader.downloadImage(Configurations.apiUrl + "getImage?id=" + product.getId() + "&name=primary", new ImageDownloader.OnDownloadImageCompleteListener() {
                        @Override
                        public void OnDownloadComplete(Bitmap bitmap) {

                            try {
                                File tem2p = Util.writeToFile(bitmap, temp, 100, false);
                                tem2p = tem2p.getParentFile();
                                tem2p = new File(tem2p.getAbsolutePath() + "/" + "primary-thumb");
                                Picasso.with(App.context).load(tem2p).into(item_image);
                                AdapterHelper tempHelper=adapterHelpers.get(position);
                                tempHelper.setVisible(true);
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
            item_title.setText(product.getTitle());
            item_description.setText(product.getDescription());
        }
        private void deleteItem(final int id){
            notifyItemRemoved(getLayoutPosition());
            HashMap<String,String> params=new HashMap<>();
            params.put("id", id+"");
            params.put("username", App.prefManager.getPreference("username"));
            params.put("password", App.prefManager.getPreference("password"));
            params.put("sh2", Configurations.sh2);
            params.put("sh1", App.prefManager.getPreference("sh1"));
            App.webService.postRequest(params, Configurations.apiUrl + "removeProduct", new WebService.OnPostReceived() {
                @Override
                public void onReceived(String message) {
                    if(message.equals("OK:1")){
                        App.showToast("عملیات با موفقیت انجام شد.");
                        App.dbHelper.delete(App.dbHelper.product_table,"id",id+"");
                    }
                    else{
                        App.showToast("عملیات با مشکل مواجه شد.");
                    }
                }
                @Override
                public void onReceivedError(String message) {
                    App.showToast("عملیات با مشکل مواجه شد.");

                }
            });
        }
    }

}