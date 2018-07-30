package ir.codetower.samanshiri.Adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import ir.codetower.samanshiri.Activities.SliderEditorActivity;
import ir.codetower.samanshiri.App;
import ir.codetower.samanshiri.Configurations;
import ir.codetower.samanshiri.Helpers.DownloadHelper;
import ir.codetower.samanshiri.Helpers.ImageDownloader;
import ir.codetower.samanshiri.Helpers.Util;
import ir.codetower.samanshiri.Models.AdapterHelper;
import ir.codetower.samanshiri.Models.Setting;
import ir.codetower.samanshiri.Models.Slide;
import ir.codetower.samanshiri.R;

public class ImagePagerAdapter extends PagerAdapter {
    private ArrayList<Slide> slides;
    private ArrayList<AdapterHelper> adapterHelpers = new ArrayList<>();
    private ImageView slideImage;
    private AppCompatImageView item_edit;
    private SpinKitView item_load;
    private static ArrayList<DownloadHelper> downloadHelpers=new ArrayList<>();
    public ImagePagerAdapter(ArrayList<Slide> slides) {
        this.slides = slides;
        for (Slide pro : slides) {
            AdapterHelper adapterHelper = new AdapterHelper();
            adapterHelper.setId(pro.getId());
            adapterHelper.setVisible(false);
            adapterHelpers.add(adapterHelper);
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return slides.get(position).getTabTitle();
    }

    @Override
    public int getCount() {
        return slides.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final Slide slide = slides.get(position);

        View view = LayoutInflater.from(App.context).inflate(R.layout.slider_adapter, null);
        item_load = (SpinKitView) view.findViewById(R.id.item_load);
        Setting setting = App.prefManager.getSettings();
        item_load.setColor(Color.parseColor(setting.getPrimary_color()));
        item_load.setVisibility(View.VISIBLE);
        slideImage = (ImageView) view.findViewById(R.id.slide_image);
        item_edit = (AppCompatImageView) view.findViewById(R.id.item_edit);
        item_edit.setColorFilter(Color.parseColor(setting.getPrimary_color()));
        if (App.prefManager.isAdmin()) {
            item_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(App.context, SliderEditorActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("id", slide.getId());
                    App.context.startActivity(intent);
                }
            });
        } else {
            item_edit.setVisibility(View.INVISIBLE);
        }

        slideImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(slide.getLink().contains("http")){
                   Intent i = new Intent(Intent.ACTION_VIEW);
                   i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                   i.setData(Uri.parse(slide.getLink()));
                   App.context.startActivity(i);
               }

            }
        });
        String path = Configurations.sdTempFolderAddress + "slider";
        File temp = new File(path, "/" + slide.getId()+"-"+slide.getUpdate_token());
        if (temp.exists()) {
            Picasso.with(App.context).load(temp).into(slideImage);
            item_load.setVisibility(View.GONE);
        } else {
                DownloadHelper helper=new DownloadHelper(slide.getId(),item_load,slideImage);
                downloadHelpers.add(helper);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ImageDownloader.downloadImage(Configurations.apiUrl + "getSlideImage?id=" + slide.getId(), new ImageDownloader.OnDownloadImageCompleteListener() {
                            @Override
                            public void OnDownloadComplete(Bitmap bitmap) {
                                File path = new File(Configurations.sdTempFolderAddress + "slider");
                                path.mkdirs();
                                File temp = new File(path, slide.getId() + "-"+slide.getUpdate_token());
                                try {
                                    for (DownloadHelper item:downloadHelpers) {
                                        if(item.getId()==slide.getId()){
                                            item.getLoading().setVisibility(View.INVISIBLE);
                                            Picasso.with(App.context).load(Util.writeToFile(bitmap, temp, 100, false)).into(item.getImage());
                                        }
                                    }
                                    item_load.setVisibility(View.GONE);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void OnDownloadError() {
                                for (DownloadHelper item:downloadHelpers) {
                                    if(item.getId()==slide.getId()){
                                        item.getLoading().setVisibility(View.INVISIBLE);
                                        item.getImage().setImageResource(R.drawable.placeholder);
                                    }
                                }
                            }
                        });
                    }
                }).start();
            }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
