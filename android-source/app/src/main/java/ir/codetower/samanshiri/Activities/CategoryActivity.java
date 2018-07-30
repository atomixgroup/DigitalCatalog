package ir.codetower.samanshiri.Activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.HashMap;

import ir.codetower.samanshiri.Adapters.CategoriesAdapter;
import ir.codetower.samanshiri.App;
import ir.codetower.samanshiri.Configurations;
import ir.codetower.samanshiri.CustomTypefaceSpan;
import ir.codetower.samanshiri.CustomViews.CustomButton;
import ir.codetower.samanshiri.CustomViews.CustomTextView;
import ir.codetower.samanshiri.Helpers.DeleteItemListener;
import ir.codetower.samanshiri.Helpers.WebService;
import ir.codetower.samanshiri.Models.Category;
import ir.codetower.samanshiri.Models.Setting;
import ir.codetower.samanshiri.R;
import ru.rambler.libs.swipe_layout.SwipeLayout;


public class CategoryActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private RecyclerView category_list;
    private CategoriesAdapter adapter;
    private DrawerLayout drawer;
    private int section;
    private Setting setting;
    private NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        if(getIntent().hasExtra("section")){
            section=getIntent().getExtras().getInt("section");
        }
        else{
            finish();
        }
        setupViews();
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
        if(App.prefManager.isAdmin()){
            AppCompatImageView add=(AppCompatImageView) findViewById(R.id.add);

            add.setVisibility(View.VISIBLE);
            add.setColorFilter(Color.parseColor(Setting.getSColor()));
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(CategoryActivity.this,CategoryEditorActivity.class);
                    intent.putExtra("id",-1);
                    intent.putExtra("section",section);
                    startActivity(intent);
                }
            });
            FloatingActionButton float_add=findViewById(R.id.float_add);
            float_add.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(Setting.getPColor())));

            float_add.setVisibility(View.VISIBLE);
            float_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(CategoryActivity.this,CategoryEditorActivity.class);
                    intent.putExtra("id",-1);
                    intent.putExtra("section",section);
                    startActivity(intent);
                }
            });
        }

        setupMenus();
        setting=App.prefManager.getSettings();
        GradientDrawable gd3 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]  {Color.parseColor("#ffffff"), Color.parseColor(setting.getPrimary_color())});

        gd3.setShape(GradientDrawable.RECTANGLE);

        View headerView = navigationView.inflateHeaderView(R.layout.nav_header);
        headerView.findViewById(R.id.head_back).setBackgroundColor(Color.parseColor(setting.getPrimary_color()));


        CustomTextView app_title=(CustomTextView) headerView.findViewById(R.id.app_title);
        CustomTextView app_description=(CustomTextView) headerView.findViewById(R.id.app_description);
        app_title.setTextColor(Color.parseColor(setting.getSecond_color()));
        app_description.setText(setting.getDescription());
        app_description.setTextColor(Color.parseColor(setting.getSecond_color()));
        app_title.setText(setting.getTitle());



    }
    private void setupMenus() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();
                switch (id) {
                    case R.id.menu_about_us:
                        Intent tempIntent = new Intent(CategoryActivity.this, AboutActivity.class);
                        startActivity(tempIntent);
                        break;
                    case R.id.menu_about_designer:
                        tempIntent = new Intent(CategoryActivity.this, AboutActivity.class);
                        startActivity(tempIntent);
                        break;
                    case R.id.menu_contact_us:
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:"+App.prefManager.getPreference("phone")));
                        startActivity(intent);
                        break;
                    case R.id.menu_login:
                        tempIntent = new Intent(CategoryActivity.this, LoginActivity.class);
                        startActivity(tempIntent);
                        break;
                    case R.id.menu_logout:
                        App.prefManager.removePreference("sh1");
                        tempIntent = new Intent(CategoryActivity.this, SplashScreenActivity.class);
                        startActivity(tempIntent);
                        finish();
                        break;
                    case R.id.menu_settings:
                        tempIntent = new Intent(CategoryActivity.this, SettingsActivity.class);
                        startActivity(tempIntent);
                        break;
                        case R.id.menu_message_box:
                        tempIntent = new Intent(CategoryActivity.this, MessageActivity.class);
                        startActivity(tempIntent);
                        break;

//                    case R.id.menu_list_gheymat:
//                        Uri uri = Uri.parse(Configurations.apiUrl + "getPdf?sh2=" + Configurations.sh2);
//                        DownloadManager mgr = (DownloadManager) App.context.getSystemService(DOWNLOAD_SERVICE);
//                        mgr.enqueue(new DownloadManager.Request(uri)
//                                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
//                                        DownloadManager.Request.NETWORK_MOBILE)
//                                .setAllowedOverRoaming(false)
//                                .setTitle("liste_gheymat.pdf")
//                                .setDescription("pdf")
//                                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
//                                        "liste_gheymat.pdf"));
//                        registerReceiver(onComplete,
//                                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
//                        break;
                }
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });
        Menu m = navigationView.getMenu();
        CustomTypefaceSpan typefaceSpan = new CustomTypefaceSpan();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);
            if(mi.getItemId()==R.id.menu_exit){
                mi.setVisible(false);
            }
            if (App.prefManager.isAdmin()) {
                if (mi.getItemId() == R.id.menu_login) {
                    mi.setVisible(false);
                }
                if (mi.getItemId() == R.id.menu_logout) {
                    mi.setVisible(true);
                }
                if(mi.getItemId()==R.id.menu_settings){
                    mi.setVisible(true);
                }
                if(mi.getItemId()==R.id.menu_message_box){
                    mi.setVisible(true);
                }
            } else {
                if (mi.getItemId() == R.id.menu_login) {
                    mi.setVisible(true);
                }
                if (mi.getItemId() == R.id.menu_logout) {
                    mi.setVisible(false);
                }
                if(mi.getItemId()==R.id.menu_settings){
                    mi.setVisible(false);
                }
                if(mi.getItemId()==R.id.menu_message_box){
                    mi.setVisible(false);
                }
            }
            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    typefaceSpan.applyFontToMenuItem(subMenuItem);
                }
            }

            //the method we have create in activity
            typefaceSpan.applyFontToMenuItem(mi);
        }


    }
    private void setupViews() {
        setupNavigationViews();
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        category_list= (RecyclerView) findViewById(R.id.category_list);
        final View dialogView = LayoutInflater.from(this).inflate(R.layout.confirm_dialog, null);
        final AlertDialog dialog = new AlertDialog.Builder(CategoryActivity.this).create();
        final CustomButton btn_accept= (CustomButton) dialogView.findViewById(R.id.btn_accept);
        final CustomButton btn_decline= (CustomButton) dialogView.findViewById(R.id.btn_decline);
        btn_decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setView(dialogView);
        ArrayList<Category> categories=App.dbHelper.getCategories(section);
        if(categories!=null){
            adapter=new CategoriesAdapter(categories, new DeleteItemListener() {
                @Override
                public void OnDelete(final int itemId, final int pos, final SwipeLayout swipeLayout) {
                    btn_decline.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(swipeLayout!=null){
                                swipeLayout.animateReset();
                            }
                            dialog.dismiss();
                        }
                    });

                    btn_accept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            adapter.deleteProductItem(pos);
                            adapter.notifyItemRemoved(pos);
                            HashMap<String,String> params=new HashMap<>();
                            params.put("id", itemId+"");
                            params.put("username", App.prefManager.getPreference("username"));
                            params.put("password", App.prefManager.getPreference("password"));
                            params.put("sh2", Configurations.sh2);
                            params.put("sh1", App.prefManager.getPreference("sh1"));
                            App.webService.postRequest(params, Configurations.apiUrl + "removeCategory", new WebService.OnPostReceived() {
                                @Override
                                public void onReceived(String message) {
                                    if(message.equals("OK:1")){
                                        App.showToast("عملیات با موفقیت انجام شد.");
                                        App.dbHelper.delete(App.dbHelper.category_table,"id",itemId+"");
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
                    });
                    dialog.show();
                }
            });
            category_list.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            category_list.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
        else{
            findViewById(R.id.noItem).setVisibility(View.VISIBLE);
        }

    }

    private void setupNavigationViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor(Setting.getPColor()));
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.



        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks he
        return true;
    }
}
