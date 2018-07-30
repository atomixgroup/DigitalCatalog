package ir.codetower.samanshiri.Activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.HashMap;

import ir.codetower.samanshiri.Adapters.GroupCategoriesAdapter;
import ir.codetower.samanshiri.Adapters.GroupRelatedItemsAdapter;
import ir.codetower.samanshiri.Adapters.ImagePagerAdapter;
import ir.codetower.samanshiri.AnimThread;
import ir.codetower.samanshiri.App;
import ir.codetower.samanshiri.Configurations;
import ir.codetower.samanshiri.CustomTypefaceSpan;
import ir.codetower.samanshiri.CustomViews.ActionSheet;
import ir.codetower.samanshiri.CustomViews.CustomCircleButton;
import ir.codetower.samanshiri.CustomViews.CustomTextView;
import ir.codetower.samanshiri.Helpers.WebService;
import ir.codetower.samanshiri.Models.Category;
import ir.codetower.samanshiri.Models.Product;
import ir.codetower.samanshiri.Models.Setting;
import ir.codetower.samanshiri.Models.Slide;
import ir.codetower.samanshiri.R;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private AnimThread animThread;
    private ViewPager viewPager;
    private RecyclerView categories, related, products,seens;
    private GroupRelatedItemsAdapter relatedItemsAdapter, productListAdapter,seensAdapter;
    private GroupCategoriesAdapter categoriesAdapter;
    private DrawerLayout drawer;

    private ArrayList<Slide> slides;
    private Toolbar toolbar;
    public static final int FINISH_REQUEST_CODE = 183;
    private Setting setting;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent.hasExtra("type")) {
            Bundle extras = intent.getExtras();
            if (extras.getString("type").equals("product")) {
                Intent goIntent = new Intent(MainActivity.this, ProductActivity.class);
                goIntent.putExtra("id", Integer.parseInt(extras.getString("id")));
                startActivity(goIntent);
            } else if (extras.getString("type").equals("exit")) {
                finish();
            }
        }
        setContentView(R.layout.activity_main);
        AppCompatImageView search = (AppCompatImageView) findViewById(R.id.btn_search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
        setupMenus();
        setupViews();

//        GradientDrawable gd3 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]  {Color.parseColor("#ffffff"), Color.parseColor(setting.getPrimary_color())});
//
//        gd3.setShape(GradientDrawable.RECTANGLE);

        View headerView = navigationView.inflateHeaderView(R.layout.nav_header);
//        headerView.findViewById(R.id.head_back).setBackground(gd3);
        headerView.findViewById(R.id.head_back).setBackgroundColor(Color.parseColor(setting.getPrimary_color()));

        CustomTextView app_title = (CustomTextView) headerView.findViewById(R.id.app_title);
        CustomTextView app_description = (CustomTextView) headerView.findViewById(R.id.app_description);
        final CustomTextView app_statistics = (CustomTextView) headerView.findViewById(R.id.statistics);

        app_statistics.setText(setting.getDescription());
        app_title.setTextColor(Color.parseColor(setting.getSecond_color()));
        app_description.setText(setting.getDescription());
        app_description.setTextColor(Color.parseColor(setting.getSecond_color()));
        app_title.setText(setting.getTitle());
        if (App.prefManager.isAdmin()) {
            HashMap<String, String> params = new HashMap<>();
            params.put("username", App.prefManager.getPreference("username"));
            params.put("password", App.prefManager.getPreference("password"));
            params.put("sh2", Configurations.sh2);
            params.put("sh1", App.prefManager.getPreference("sh1"));

            App.webService.postRequest(params, Configurations.apiUrl + "getStatistics", new WebService.OnPostReceived() {
                @Override
                public void onReceived(String message) {
                    app_statistics.setText("آمار نصب : " + message);
                    app_statistics.setVisibility(View.VISIBLE);
                }

                @Override
                public void onReceivedError(String message) {

                }
            });
        }

    }


    private void setupViews() {

        findViewById(R.id.ten).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,CategoryActivity.class);
                intent.putExtra("section",2);
                startActivity(intent);
            }
        });
        findViewById(R.id.eleven).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,CategoryActivity.class);
                intent.putExtra("section",3);
                startActivity(intent);
            }
        });
        findViewById(R.id.twelve).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,CategoryActivity.class);
                intent.putExtra("section",4);
                startActivity(intent);
            }
        });

        CustomTextView viewCategories = (CustomTextView) findViewById(R.id.viewCategories);
        viewCategories.setText("<    ");
        viewCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
                intent.putExtra("section",1);
                startActivity(intent);
            }
        });

        final ActionSheet.Builder actionSheet = ActionSheet.createBuilder(MainActivity.this, getSupportFragmentManager())
                .setCancelButtonTitle("برگشت")
                .setOtherButtonTitles("Instagram", "Telegram", "Website")
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {

                        switch (index) {
                            case 0: {
                                Uri uri = Uri.parse("http://instagram.com/_u/" + setting.getInstagram());
                                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                                likeIng.setPackage("com.instagram.android");

                                try {
                                    startActivity(likeIng);
                                } catch (ActivityNotFoundException e) {
                                    startActivity(new Intent(Intent.ACTION_VIEW,
                                            Uri.parse("http://instagram.com/" + setting.getInstagram())));
                                }
                            }
                            break;
                            case 1: {
                                Intent telegram = new Intent(Intent.ACTION_VIEW, Uri.parse("https://telegram.me/" + setting.getTelegram()));
                                startActivity(telegram);
                            }
                            break;
                            case 2: {


                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(setting.getLinkedin()));

                                startActivity(intent);
                            }
                            break;
                        }
                    }
                });

        CustomCircleButton btn1, btn2, btn3, btn4;
        btn1 = findViewById(R.id.btn_bottom1);
        btn2 = findViewById(R.id.btn_bottom2);
        btn3 = findViewById(R.id.btn_bottom3);
        btn4 = findViewById(R.id.btn_bottom4);

        btn1.setCircleColor("#0295d9");
        btn1.setIconImage(R.drawable.ic_sup);
        btn2.setCircleColor("#9b59b6");
        btn2.setIconImage(R.drawable.ic_ribbon);
        btn3.setCircleColor("#F07C38");
        btn3.setIconImage(R.drawable.ic_team);
        btn4.setCircleColor("#c0392b");
        btn4.setIconImage(R.drawable.ic_shopping_cart);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionSheet.show();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FavoriteActivity.class);
                startActivity(intent);
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(App.context,AboutActivity.class);
                startActivity(intent);
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(App.context,CartActivity.class);
                startActivity(intent);
            }
        });
//        CustomTextView viewRelatedItems= (CustomTextView) findViewById(R.id.viewRelatedItems);
//        viewRelatedItems.setText("همه"+" "+">");
//        viewRelatedItems.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//         comunicate = (RelativeLayout) findViewById(R.id.communicate);
//        comunicate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ActionSheet.createBuilder(MainActivity.this, getSupportFragmentManager())
//                        .setCancelButtonTitle("برگشت")
//                        .setOtherButtonTitles("Instagram", "Telegram", "Website")
//                        .setCancelableOnTouchOutside(true)
//                        .setListener(new ActionSheet.ActionSheetListener() {
//                            @Override
//                            public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
//
//                            }
//
//                            @Override
//                            public void onOtherButtonClick(ActionSheet actionSheet, int index) {
//
//                                switch (index){
//                                    case 0:{
//                                        Uri uri = Uri.parse("http://instagram.com/_u/"+setting.getInstagram());
//                                        Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
//
//                                        likeIng.setPackage("com.instagram.android");
//
//                                        try {
//                                            startActivity(likeIng);
//                                        } catch (ActivityNotFoundException e) {
//                                            startActivity(new Intent(Intent.ACTION_VIEW,
//                                                    Uri.parse("http://instagram.com/"+setting.getInstagram())));
//                                        }
//                                    }
//                                        break;
//                                    case 1:{
//                                        Intent telegram = new Intent(Intent.ACTION_VIEW , Uri.parse("https://telegram.me/"+setting.getTelegram()));
//                                        startActivity(telegram);
//                                    }
//                                        break;
//                                    case 2:{
//
//
//                                          Intent  intent = new Intent(Intent.ACTION_VIEW, Uri.parse(setting.getLinkedin()));
//
//                                        startActivity(intent);
//                                    }
//                                        break;
//                                }
//                            }
//                        }).show();
//            }
//        });

        setupNavigationbar();
        setupSlider();
        setupCategoriesAndRelatedGroupViews();
        setupColorToViews();
    }

    private void setupColorToViews() {
        setting = App.prefManager.getSettings();
//        int strokeWidth = 4; // 3px not dp
//        int roundRadius = 5; // 8px not dp
//        int strokeColor = Color.parseColor(setting.getPrimary_color());
//
//        GradientDrawable gd = new GradientDrawable();
//        gd.setCornerRadius(roundRadius);
//        gd.setStroke(strokeWidth, strokeColor);
//        GradientDrawable gd2 = new GradientDrawable();
//        gd.setCornerRadius(roundRadius);
//        gd.setStroke(strokeWidth, strokeColor);
//
//
//        categories.setBackground(gd);
//        related.setBackground(gd);
//        products.setBackground(gd);
//        viewPager.setBackground(gd2);

//        comunicate.setBackgroundColor(Color.parseColor(setting.getPrimary_color()));
        toolbar.setBackgroundColor(Color.parseColor(setting.getPrimary_color()));
        toolbar.setTitle("");
        CustomTextView top_title = (CustomTextView) findViewById(R.id.top_title);
        top_title.setText(setting.getTitle());
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(setting.getPrimary_color()));
        }
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

    }

    private void setupCategoriesAndRelatedGroupViews() {
        categories = (RecyclerView) findViewById(R.id.categories);
        categories.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
        related = (RecyclerView) findViewById(R.id.related);
        related.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
        products = (RecyclerView) findViewById(R.id.all_products);
        seens = (RecyclerView) findViewById(R.id.seen);
        seens.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));

        products.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
        CustomTextView categoryNoItem = (CustomTextView) findViewById(R.id.categoryNoItem);
        CustomTextView relatedNoItem = (CustomTextView) findViewById(R.id.relatedNoItem);
        CustomTextView productNoItem = (CustomTextView) findViewById(R.id.productNoItem);
        CustomTextView seenNoItem = (CustomTextView) findViewById(R.id.seenNoItem);

        ArrayList<Category> categoriesData = App.dbHelper.getCategories(1);
        ArrayList<Product> seensData = App.dbHelper.getProductOrderBySeen(5);
        ArrayList<Product> relatedData = App.dbHelper.getTopProducts();
        ArrayList<Product> productsData = App.dbHelper.getOnlyProducts();

        if (seensData != null && seensData.size() > 0) {
            seensAdapter = new GroupRelatedItemsAdapter(seensData);
            seens.setAdapter(seensAdapter);
            seensAdapter.notifyDataSetChanged();
            seenNoItem.setVisibility(View.GONE);
        } else {
            seenNoItem.setVisibility(View.VISIBLE);
        }

        if (categoriesData != null && categoriesData.size() > 0) {
            categoriesAdapter = new GroupCategoriesAdapter(categoriesData);
            categories.setAdapter(categoriesAdapter);
            categoriesAdapter.notifyDataSetChanged();
            categoryNoItem.setVisibility(View.GONE);
        } else {
            categoryNoItem.setVisibility(View.VISIBLE);
        }
        if (relatedData != null && relatedData.size() > 0) {
            relatedItemsAdapter = new GroupRelatedItemsAdapter(relatedData);
            related.setAdapter(relatedItemsAdapter);
            relatedItemsAdapter.notifyDataSetChanged();
            relatedNoItem.setVisibility(View.GONE);

        } else {
            relatedNoItem.setVisibility(View.VISIBLE);
        }
        if (productsData != null && productsData.size() > 0) {
            productListAdapter = new GroupRelatedItemsAdapter(productsData);
            products.setAdapter(productListAdapter);
            productListAdapter.notifyDataSetChanged();
            productNoItem.setVisibility(View.GONE);
        } else {
            productNoItem.setVisibility(View.VISIBLE);
        }
    }

    private void setupNavigationbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    }

    private void setupSlider() {

        slides = App.dbHelper.getSlides();




        viewPager = (ViewPager) findViewById(R.id.slider);
        ImagePagerAdapter adapter = new ImagePagerAdapter(slides);
        viewPager.setAdapter(adapter);

        int col = slides.size() / 2;


    }

    private void setupAnimationOfSlider() {
        animThread = new AnimThread();
        animThread.setSlideDelayTime(3000);
        animThread.setSlideSize(slides.size());

        animThread.start();
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

//        if (animThread != null) {
//            animThread.cancel();
//            setupViews();
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (animThread != null) {
//            animThread.cancel();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return false;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.


        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupMenus() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();
                switch (id) {
                    case R.id.menu_about_us:
                        Intent tempIntent = new Intent(MainActivity.this, AboutActivity.class);
                        startActivity(tempIntent);
                        break;
                    case R.id.menu_about_designer:
                        tempIntent = new Intent(MainActivity.this, AboutDesignerActivity.class);
                        startActivity(tempIntent);
                        break;
                    case R.id.menu_contact_us:
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + App.prefManager.getPreference("phone")));
                        startActivity(intent);
                        break;
                    case R.id.menu_login:
                        tempIntent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(tempIntent);
                        break;
                    case R.id.menu_favorite:
                        tempIntent = new Intent(MainActivity.this, FavoriteActivity.class);
                        startActivity(tempIntent);
                        break;
                    case R.id.menu_logout:
                        App.prefManager.removePreference("sh1");
                        tempIntent = new Intent(MainActivity.this, SplashScreenActivity.class);
                        startActivity(tempIntent);
                        finish();
                        break;
                    case R.id.menu_settings:
                        tempIntent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(tempIntent);
                        break;
                    case R.id.menu_message_box:
                        tempIntent = new Intent(MainActivity.this, MessageActivity.class);
                        startActivity(tempIntent);
                        break;
                    case R.id.menu_exit:
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
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
            if (App.prefManager.isAdmin()) {
                if (mi.getItemId() == R.id.menu_login) {
                    mi.setVisible(false);
                }
                if (mi.getItemId() == R.id.menu_logout) {
                    mi.setVisible(true);
                }
                if (mi.getItemId() == R.id.menu_settings) {
                    mi.setVisible(true);
                }
                if (mi.getItemId() == R.id.menu_message_box) {
                    mi.setVisible(true);
                }
            } else {
                if (mi.getItemId() == R.id.menu_login) {
                    mi.setVisible(true);
                }
                if (mi.getItemId() == R.id.menu_logout) {
                    mi.setVisible(false);
                }
                if (mi.getItemId() == R.id.menu_settings) {
                    mi.setVisible(false);
                }
                if (mi.getItemId() == R.id.menu_message_box) {
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


}
