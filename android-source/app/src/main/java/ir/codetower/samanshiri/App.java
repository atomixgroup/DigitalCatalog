package ir.codetower.samanshiri;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import ir.codetower.samanshiri.Helpers.DbHelper;
import ir.codetower.samanshiri.Helpers.SharedPrefManager;
import ir.codetower.samanshiri.Helpers.WebService;
import ir.codetower.samanshiri.Models.CartItem;
import ir.codetower.samanshiri.Models.Category;
import ir.codetower.samanshiri.Models.Product;
import ir.codetower.samanshiri.Models.Setting;
import ir.codetower.samanshiri.Models.Slide;


public class App extends Application {



    public static Handler handler = new Handler();
    public static WebService webService;
    public static Context context;
    private static Typeface irsans;
    private static Typeface ojan;
    private static Typeface yekan;
    public static SharedPrefManager prefManager;
    public static AssetManager assetManager;
    public static DbHelper dbHelper;
    public static String IMEI = "0";
    public static ArrayList<CartItem> shoppingCart;
    private static Point widthAndHeight;
    public static String aboutDesigner="";
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        webService = new WebService();
        dbHelper = new DbHelper(context);
        assetManager = getAssets();
        prefManager = new SharedPrefManager(Configurations.prefName);
        //config fonts
        yekan = Typeface.createFromAsset(assetManager, "fonts/yekan.ttf");
        ojan = Typeface.createFromAsset(assetManager, "fonts/ojan.ttf");
        irsans = Typeface.createFromAsset(assetManager, "fonts/irsans.ttf");
        shoppingCart = dbHelper.getCartItems();
    }

    public static void setXy(Point xy) {
        widthAndHeight = xy;
    }

    public static Point getXy() {
        return widthAndHeight;
    }

    public static Typeface getFontWithName(String name) {
        switch (name) {
            case "ojan":
                return ojan;
            case "yekan":
                return yekan;
            case "irsans":
                return irsans;
            default:
                return yekan;
        }
    }

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String getExcerpt(String text, int length, int fromEnd) {
        String temp = text;
        if (text.length() > length + fromEnd + 5) {
            temp = text.substring(0, length) + " ...";
            temp += text.substring(text.length() - fromEnd);
        }
        return temp;
    }

    public static boolean isAwaibleFormat(String fileName) {
        String ext = getFileExtension(fileName);
        if (ext.equals("pdf")) {
            return true;
        } else {
            return false;
        }

    }

    public static String getFileExtension(String file_uri) {
        return file_uri.substring(file_uri.lastIndexOf(".") + 1);
    }

    public static void getDataFromServerAndSaveToDataBase(final WebService.OnGetDataCompleteListener onGetDataCompleteListener) {
        webService.postRequest("sh2", Configurations.sh2, Configurations.apiUrl + "getAllData", new WebService.OnPostReceived() {
            @Override
            public void onReceived(String message) {
                try {
                    JSONArray jsonArray = new JSONArray(message);
                    ArrayList<Product> products = dbHelper.jsonArrayToProduct(jsonArray.getString(0));
                    ArrayList<Category> categories = dbHelper.jsonArrayToCategory(jsonArray.getString(1));
                    ArrayList<Slide> slides = dbHelper.jsonArrayToSlide(jsonArray.getString(2));
                    processCategoriesPhoto(categories);
                    processProductsPhoto(products);
                    processSlidersPhoto(slides);

                    JSONObject jsonItem = new JSONObject(jsonArray.getString(3));

                    Setting setting = new Setting();
                    setting.setTitle(jsonItem.getString("title"));
                    setting.setDescription(jsonItem.getString("description"));
                    setting.setPhone(jsonItem.getString("phone"));
                    setting.setPrimary_color(jsonItem.getString("primary_color"));
                    setting.setSecond_color(jsonItem.getString("second_color"));
                    setting.setAbout(jsonItem.getString("about"));
                    setting.setLinkedin(jsonItem.getString("linkedin"));
                    setting.setInstagram(jsonItem.getString("instagram"));
                    setting.setTelegram(jsonItem.getString("telegram"));
                    setting.setCartSwitch((jsonItem.getString("is_cart").equals("true")));
                    App.prefManager.saveSettings(setting);
                    dbHelper.deleteAllData();
                    dbHelper.batch_insert_category(categories);
                    String[] fav=prefManager.getPreference("favorites").split("-");
                    for (String item:fav){
                        for (int i=0;i<products.size();i++){
                            if(item.equals(products.get(i).getId()+"")){
                                Product temp=new Product();
                                temp=products.get(i);
                                temp.setFavorite(true);
                                products.set(i,temp);
                            }
                        }
                    }
                    dbHelper.batch_insert_product(products);
                    dbHelper.batch_insert_slides(slides);
                    onGetDataCompleteListener.onGetDataCompleted("");
                } catch (JSONException e) {
                    e.printStackTrace();
                    onGetDataCompleteListener.onGetDataError("E1");
                }
            }

            @Override
            public void onReceivedError(String message) {
                onGetDataCompleteListener.onGetDataError("");
            }
        });
    }

    private static void processSlidersPhoto(ArrayList<Slide> slides) {
        for (Slide slide : slides) {
            File file = new File(Configurations.sdTempFolderAddress + "slider");
            if (file.exists() && file.isDirectory()) {
                for (File temp : file.listFiles()) {
                    if (temp.exists() && temp.isFile()) {
                        String[] myfile = temp.getName().split("-");
                        if (myfile[0].equals(slide.getId() + "")) {
                            try{
                                if (!myfile[1].equals(slide.getUpdate_token())) {
                                    deleteFileWithPath(temp.getAbsolutePath());
                                    deleteFileWithPath(temp.getAbsolutePath() + "-thumb");
                                }

                            } catch (ArrayIndexOutOfBoundsException e){
                                e.printStackTrace();
                                deleteFileWithPath(temp.getAbsolutePath());
                            }
                        }
                    }
                }
            }
        }
    }

    private static void processCategoriesPhoto(ArrayList<Category> categories) {
        for (Category category : categories) {
            File file = new File(Configurations.sdTempFolderAddress);
            if (file.exists() && file.isDirectory()) {
                for (File temp : file.listFiles()) {
                    if (temp.isDirectory()) {
                        String[] myfile = temp.getName().split("-");
                        if (myfile[0].equals(category.getId() + "")) {
                            try {
                                if (!myfile[1].equals(category.getUpdate_token())) {
                                    deleteFileWithPath(temp.getAbsolutePath() + "/primary");
                                    deleteFileWithPath(temp.getAbsolutePath() + "/primary-thumb");
                                }
                            } catch (ArrayIndexOutOfBoundsException e) {
                                e.printStackTrace();
                                deleteFolder(temp);
                            }

                        }
                    }
                }
            }
        }
    }

    private static void processProductsPhoto(ArrayList<Product> products) {
        for (Product product : products) {
            File file = new File(Configurations.sdTempFolderAddress + product.getCat_id());
            if (file.exists() && file.isDirectory()) {
                for (File temp : file.listFiles()) {
                    if (temp.isDirectory()) {
                        String[] myfile = temp.getName().split("-");
                        if (myfile[0].equals(product.getId() + "")) {
                            try{
                                if (!myfile[1].equals(product.getUpdate_token())) {
                                    deleteFolder(temp);
                                }
                            } catch (ArrayIndexOutOfBoundsException e){
                                e.printStackTrace();
                                deleteFolder(temp);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void deleteFileWithPath(String path) {

        File currentFile = new File(path);
        if(currentFile.exists()){
            currentFile.delete();
        }
    }

    public static void deleteFolder(File index) {
        File[] entries = index.listFiles();
        for (File s : entries) {
            if(s.isDirectory()){
                deleteFolder(s);
            }
            boolean deleted=s.delete();
        }
        index.delete();
    }

    public static void makeDirectories() {
        new File(Configurations.sdTempFolderAddress).mkdirs();
    }

    public static void showToast(final String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static int getResourceId(String name) {
        return context.getResources().getIdentifier(name, "id", context.getPackageName());
    }

    public static void updateCart() {
        shoppingCart = App.dbHelper.getCartItems();
    }
}
