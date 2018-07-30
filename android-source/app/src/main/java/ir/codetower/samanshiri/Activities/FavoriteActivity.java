package ir.codetower.samanshiri.Activities;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;

import ir.codetower.samanshiri.Adapters.FavoriteListAdapter;
import ir.codetower.samanshiri.App;
import ir.codetower.samanshiri.CustomViews.CustomTextView;
import ir.codetower.samanshiri.Helpers.DeleteItemListener;
import ir.codetower.samanshiri.Models.Product;
import ir.codetower.samanshiri.Models.Setting;
import ir.codetower.samanshiri.R;
import ru.rambler.libs.swipe_layout.SwipeLayout;

public class FavoriteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        RecyclerView fav_list = findViewById(R.id.fav_list);
        fav_list.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<Product> products = App.dbHelper.getFavorites();
        if (products != null && products.size() > 0) {
            FavoriteListAdapter adapter = new FavoriteListAdapter(products, new DeleteItemListener() {
                @Override
                public void OnDelete(int itemId, int pos, SwipeLayout swipeLayout) {

                }
            });
            fav_list.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        }
        toolbar.setBackgroundColor(Color.parseColor(Setting.getPColor()));
        toolbar.setTitle("");
        CustomTextView top_title= (CustomTextView) findViewById(R.id.head_name);
        top_title.setText("نشان شده ها");
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= 21)
        {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(Setting.getPColor()));
        }
        AppCompatImageView back= (AppCompatImageView) findViewById(R.id.back);
        back.setColorFilter(Color.parseColor(Setting.getSColor()));

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
