package ir.codetower.samanshiri.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ir.codetower.samanshiri.BuildConfig;
import ir.codetower.samanshiri.Models.CartItem;
import ir.codetower.samanshiri.Models.Category;
import ir.codetower.samanshiri.Models.Product;
import ir.codetower.samanshiri.Models.Slide;

/**
 * Created by Mr R00t on 4/22/2017.
 */
//id 	name 	en_name
// scientific_name 	family 	nature
// specifications 	ingredients 	properties
// 	contraindications 	organs 	habitat 	construction
public class DbHelper
        extends SQLiteOpenHelper {
    public String product_table = "products";
    public String category_table = "categories";
    public String slide_table = "slides";
    public String cart_table = "cart";
    private final String SQL_CREATE_PRODUCT = "CREATE TABLE IF NOT EXISTS " + product_table + "( " +
            "id INTEGER UNIQUE ," +
            "title VARCHAR(256)," +
            "description TEXT," +
            "cat_id INTEGER," +
            "top VARCHAR(5)," +
            "created_at VARCHAR(256)," +
            "updated_at VARCHAR(256),"+
            "type INTEGER,"+
            "link VARCHAR(512)," +
            "download VARCHAR(10)," +
            "price INTEGER ,"+
            "video VARCHAR(10) ,"+
            "video_url VARCHAR(512),"+
            "video_thumb VARCHAR(512)," +
            "update_token VARCHAR(50)," +
            "favorite VARCHAR(5)," +
            "seen INTEGER )";
    private final String SQL_CREATE_CATEGORY = "CREATE TABLE IF NOT EXISTS " + category_table + "( " +
            "id INTEGER UNIQUE ," +
            "title VARCHAR(256)," +
            "image VARCHAR(256)," +
            "description TEXT," +
            "created_at VARCHAR(256)," +
            "updated_at VARCHAR(256)," +
            "update_token VARCHAR(50)," +
            "section INTEGER )";


    private final String SQL_CREATE_SLIDER = "CREATE TABLE IF NOT EXISTS " + slide_table + "( " +
            "id INTEGER UNIQUE ," +
            "link VARCHAR(512)," +
            "update_token VARCHAR(50)," +
            "title VARCHAR(512)," +
            "tab_title VARCHAR(512)," +
            "description VARCHAR(512))";
    private final String SQL_CREATE_CART = "CREATE TABLE IF NOT EXISTS " + cart_table + "( " +
            "id INTEGER PRIMARY KEY AUTOINCREMENT ," +
            "title VARCHAR(512) ," +
            "p_id INTEGER ," +
            "count INTEGER ," +
            "price INTEGER)";
    private SQLiteDatabase db = getWritableDatabase();

    public void onCreate(SQLiteDatabase paramSQLiteDatabase) {
        paramSQLiteDatabase.execSQL(SQL_CREATE_CATEGORY);
        paramSQLiteDatabase.execSQL(SQL_CREATE_PRODUCT);
        paramSQLiteDatabase.execSQL(SQL_CREATE_CART);
        paramSQLiteDatabase.execSQL(SQL_CREATE_SLIDER);
    }

    public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2) {
        paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS " + product_table);
        paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS " + category_table);
        paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS " + slide_table);
        paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS " + cart_table);
        onCreate(paramSQLiteDatabase);
    }

    public DbHelper(Context paramContext) {
        super(paramContext, "arabi.db", null, BuildConfig.VERSION_CODE);
    }

    private Product parseProduct(Cursor paramCursor) {
        ArrayList<Product> products = parseProducts(paramCursor, true);
        if (products.isEmpty()) {
            return null;
        }
        return products.get(0);
    }

    private ArrayList<Product> parseProducts(Cursor paramCursor, boolean single) {
        ArrayList<Product> products = new ArrayList<>();
        if (paramCursor != null) {
            while (paramCursor.moveToNext()) {
                Product product = new Product();
                product.setId(paramCursor.getInt(0));
                product.setTitle(paramCursor.getString(1));
                product.setDescription(paramCursor.getString(2));
                product.setCat_id(paramCursor.getInt(3));
                if (paramCursor.getString(4).equals("yes")) {
                    product.setTop(true);
                } else {
                    product.setTop(false);
                }
                product.setCreated_at(paramCursor.getString(5));
                product.setUpdated_at(paramCursor.getString(6));
                product.setType(paramCursor.getInt(7));
                product.setDownloadLink(paramCursor.getString(8));
                product.setDownload(paramCursor.getString(9).equals("true"));
                product.setPrice(paramCursor.getInt(10));
                if (paramCursor.getString(11).equals("yes")) {
                    product.setVideo(true);
                } else {
                    product.setVideo(false);
                }
                product.setVideo_url(paramCursor.getString(12));
                product.setVideoThumb(paramCursor.getString(13));
                product.setUpdate_token(paramCursor.getString(14));
                product.setSeen(paramCursor.getInt(16));
                product.setCat_update_token(paramCursor.getString(17));

                if (paramCursor.getString(15).equals("yes")) {
                    product.setFavorite(true);
                } else {
                    product.setFavorite(false);
                }
                products.add(product);
                if (single) {
                    break;
                }
            }
            paramCursor.close();
        }
        return products;
    }

    private ContentValues parseToContent(Product product) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", product.getId());
        contentValues.put("title", product.getTitle());
        contentValues.put("description", product.getDescription());
        contentValues.put("cat_id", product.getCat_id());
        contentValues.put("created_at", product.getCreated_at());
        contentValues.put("updated_at", product.getUpdated_at());
        contentValues.put("type",product.getType());
        contentValues.put("link",product.getDownloadLink());
        contentValues.put("download",(product.isDownload())?"true":"false");
        contentValues.put("price",product.getPrice());
        contentValues.put("video_url",product.getVideo_url());
        contentValues.put("video_thumb",product.getVideoThumb());
        contentValues.put("update_token",product.getUpdate_token());
        contentValues.put("seen",product.getSeen());
        if (product.isTop()) {
            contentValues.put("top", "yes");
        } else {
            contentValues.put("top", "no");
        }
        if (product.isVideo()) {
            contentValues.put("video", "yes");
        } else {
            contentValues.put("video", "no");
        }
        if (product.isFavorite()) {
            contentValues.put("favorite", "yes");
        } else {
            contentValues.put("favorite", "no");
        }
        return contentValues;
    }

    public boolean batch_insert_product(ArrayList<Product> products) {
        db.beginTransaction();
        try {
            for (Product item : products) {
                this.insert(item);
            }
            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor customQuery(String table, String select, String where) {
        String sql = "SELECT " + select + " FROM " + table;
        if (where != null) {
            sql += " WHERE " + where;
        }
        return this.db.rawQuery(sql, null);
    }
    public int delete(String table, String field, String value){
        HashMap<String,String> params=new HashMap<>();
        params.put(field,value);
        return delete(table,params);
    }
    public int delete(String table, HashMap<String, String> where) {
        String keys = "";
        boolean flag = false;
        ArrayList<String> values = new ArrayList<>();
        String[] vals;
        if (where == null) {
            vals = null;
            keys = null;
        } else {

            for (Map.Entry<String, String> entry : where.entrySet()) {
                if (flag) {
                    keys += " and ";
                }
                keys += entry.getKey() + "=?";
                values.add(entry.getValue());
                flag = true;
            }
            vals = new String[values.size()];
            for (int i = 0; i < values.size(); i++) {
                vals[i] = values.get(i);
            }
        }
        return db.delete(table, keys, vals);
    }

    protected void finalize()
            throws Throwable {
        this.db.close();
        super.finalize();
    }

    public Cursor getQuery(String table, HashMap<String, String> where) { //get data from database in cursor
        String query = "SELECT * FROM " + table;
        String keys = "";
        boolean flag = false;
        ArrayList<String> values = new ArrayList<>();
        String[] vals;
        if (where == null) {
            vals = null;
        } else {
            query += " WHERE ";
            for (Map.Entry<String, String> entry : where.entrySet()) {
                if (flag) {
                    keys += " and ";
                }
                keys += entry.getKey() + "=?";
                values.add(entry.getValue());
                flag = true;
            }
            query += keys;
            vals = new String[values.size()];
            for (int i = 0; i < values.size(); i++) {
                vals[i] = values.get(i);
            }
        }
        query+=" ORDER BY id DESC";

        return db.rawQuery(query, vals);
    }
    private Cursor getProductWithCatToken(String where){
        String sql = "SELECT " + product_table+".*,"+category_table+".update_token as cat_update_token" + " FROM " + product_table+
                " join "+category_table+" ON "+category_table+".id="+product_table+".cat_id";
        if (where != null) {
            sql += " WHERE " + where;
        }
        return this.db.rawQuery(sql, null);
    }
    public ArrayList<Product> getProducts() {
        Cursor cursor = getProductWithCatToken(null);
        ArrayList<Product> products = parseProducts(cursor, false);
        cursor.close();
        if (products.isEmpty()) {
            return null;
        } else {
            return products;
        }

    }
    public ArrayList<Product> getFavorites() {
        Cursor cursor = getProductWithCatToken("favorite='yes'");
        ArrayList<Product> products = parseProducts(cursor, false);
        cursor.close();
        if (products.isEmpty()) {
            return null;
        } else {
            return products;
        }
    }
    public ArrayList<Product> getOnlyProducts() {
        Cursor cursor = getProductWithCatToken("type="+Product.TYPE_PRODUCT);
        ArrayList<Product> products = parseProducts(cursor, false);
        cursor.close();
        if (products.isEmpty()) {
            return null;
        } else {
            return products;
        }

    }

    public Product getProduct(int id) {
        Cursor cursor = getProductWithCatToken(product_table+".id="+id);
        return parseProduct(cursor);
    }

    public ArrayList<Product> getTopProducts() {
        Cursor cursor = getProductWithCatToken("top='yes'");
        return parseProducts(cursor, false);
    }

    public Long insert(Product product) {
        int res = this.update(product);
        if (res == 0) {
            return db.insert(product_table, null, parseToContent(product));
        } else {
            return (long) res;
        }
    }
    public int update(Product product) {
        ContentValues contentValues = parseToContent(product);
        String keys = "id = ?";
        String[] vals = {product.getId() + ""};
        return db.update(product_table, contentValues, keys, vals);
    }

    public ArrayList<Product> jsonArrayToProduct(String json) {
        ArrayList<Product> products = new ArrayList<>();
        JSONArray array = null;
        try {
            array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonItem = array.getJSONObject(i);
                Product product = new Product();

                if(jsonItem.getString("type").equals("news")){
                    product.setType(Product.TYPE_NEWS);
                }
                else if(jsonItem.getString("type").equals("product")){
                    product.setType(Product.TYPE_PRODUCT);
                }
                product.setDownload(jsonItem.getString("download").equals("true"));
                product.setDownloadLink(jsonItem.getString("link"));
                product.setId(jsonItem.getInt("id"));
                product.setTitle(jsonItem.getString("title"));
                product.setDescription(jsonItem.getString("description"));
                product.setCat_id(jsonItem.getInt("cat_id"));
                product.setVideo_url(jsonItem.getString("video_url"));
                product.setVideoThumb(jsonItem.getString("video_thumb"));
                product.setPrice(Integer.parseInt(jsonItem.getString("price")));
                if(jsonItem.getString("top").equals("yes")){
                    product.setTop(true);
                }
                else{
                    product.setTop(false);
                }
                if(jsonItem.getString("video").equals("yes")){
                    product.setVideo(true);
                }
                else{
                    product.setVideo(false);
                }
                product.setCreated_at(jsonItem.getString("created_at"));
                product.setUpdated_at(jsonItem.getString("updated_at"));
                product.setUpdate_token(jsonItem.getString("update_token"));
                product.setSeen(jsonItem.getInt("seen"));

                products.add(product);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return products;
    }

    public boolean batch_update_Item(ArrayList<Product> products) {
        db.beginTransaction();
        try {
            for (Product item : products) {
                this.update(item);
            }
            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
        }
    }
    public ArrayList<Product> getProductOrderBySeen(int count){
        String sql = "SELECT " + product_table+".*,"+category_table+".update_token as cat_update_token" + " FROM " + product_table+
                " join "+category_table+" ON "+category_table+".id="+product_table+".cat_id ORDER BY seen DESC LIMIT "+count;
        Cursor cursor=this.db.rawQuery(sql, null);
        return  parseProducts(cursor,false);
    }
    private Category parseCategory(Cursor paramCursor) {
        ArrayList<Category> categories = parseCategories(paramCursor, true);
        if (categories.isEmpty()) {
            return null;
        }
        return categories.get(0);
    }

    private ArrayList<Category> parseCategories(Cursor paramCursor, boolean single) {
        ArrayList<Category> categories = new ArrayList<>();
        if (paramCursor != null) {
            while (paramCursor.moveToNext()) {
                Category category = new Category();
                category.setId(paramCursor.getInt(0));
                category.setTitle(paramCursor.getString(1));
                category.setImage(paramCursor.getString(2));
                category.setDescription(paramCursor.getString(3));
                category.setCreated_at(paramCursor.getString(4));
                category.setUpdated_at(paramCursor.getString(5));
                category.setUpdate_token(paramCursor.getString(6));
                category.setSection(paramCursor.getInt(7));

                categories.add(category);
                if (single) {
                    break;
                }
            }
            paramCursor.close();
        }
        return categories;
    }

    private ContentValues parseToContent(Category category) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", category.getId());
        contentValues.put("title", category.getTitle());
        contentValues.put("image", category.getImage());
        contentValues.put("description", category.getDescription());
        contentValues.put("created_at", category.getCreated_at());
        contentValues.put("updated_at", category.getUpdated_at());
        contentValues.put("update_token", category.getUpdate_token());
        contentValues.put("section", category.getSection());

        return contentValues;
    }

    public boolean batch_insert_category(ArrayList<Category> categories) {
        db.beginTransaction();
        try {
            for (Category item : categories) {
                this.insert(item);
            }
            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
        }
    }

    public ArrayList<Category> getCategories(int section) {
        Cursor cursor=null;
        if(section==-1){
             cursor= getQuery(category_table, null);
        }
       else{
            HashMap<String,String> params=new HashMap<>();
            params.put("section",section+"");
            cursor=getQuery(category_table,params);
        }
        ArrayList<Category> categories = parseCategories(cursor, false);


        cursor.close();
        if (categories.isEmpty()) {
            return null;
        } else {
            return categories;
        }

    }

    public Category getCategory(int id) {
        HashMap<String, String> map = new HashMap<>();
        map.put("id", id + "");
        Cursor cursor = getQuery(category_table, map);
        return parseCategory(cursor);

    }

    public Long insert(Category category) {
        int res = this.update(category);
        if (res == 0) {
            return db.insert(category_table, null, parseToContent(category));
        } else {
            return (long) res;
        }
    }


    public int update(Category category) {
        ContentValues contentValues = parseToContent(category);
        String keys = "id = ?";
        String[] vals = {category.getId() + ""};
        return db.update(category_table, contentValues, keys, vals);
    }

    public ArrayList<Category> jsonArrayToCategory(String json) {
        ArrayList<Category> categories = new ArrayList<>();
        JSONArray array = null;
        try {
            array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonItem = array.getJSONObject(i);
                Category category = new Category();
                category.setId(jsonItem.getInt("id"));
                category.setTitle(jsonItem.getString("title"));
                category.setImage(jsonItem.getString("image"));
                category.setDescription(jsonItem.getString("description"));
                category.setCreated_at(jsonItem.getString("created_at"));
                category.setUpdated_at(jsonItem.getString("updated_at"));
                category.setUpdate_token(jsonItem.getString("update_token"));
                category.setSection(jsonItem.getInt("section"));
                categories.add(category);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return categories;
    }

    public boolean batch_update_category(ArrayList<Category> categories) {
        db.beginTransaction();
        try {
            for (Category item : categories) {
                this.update(item);
            }
            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
        }
    }

    public ArrayList<Product> getProductsWithCategoryId(int categoryId) {
        Cursor cursor = getProductWithCatToken("cat_id="+categoryId);
        ArrayList<Product> products = parseProducts(cursor, false);
        cursor.close();
        if (products.isEmpty()) {
            return null;
        } else {
            return products;
        }
    }

    public void deleteAllData() {
        db.execSQL("DELETE FROM " + category_table);
        db.execSQL("DELETE FROM " + product_table);
        db.execSQL("DELETE FROM " + slide_table);
    }







    public ArrayList<Slide> jsonArrayToSlide(String string) {
        ArrayList<Slide> slides = new ArrayList<>();
        JSONArray array = null;
        try {
            array = new JSONArray(string);
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonItem = array.getJSONObject(i);
                Slide slide = new Slide();
                slide.setId(jsonItem.getInt("id"));
                slide.setLink(jsonItem.getString("link"));
                slide.setUpdate_token(jsonItem.getString("update_token"));
                slide.setTabTitle(jsonItem.getString("tab_title"));
                slide.setTitle(jsonItem.getString("title"));
                slide.setDescription(jsonItem.getString("description"));
                slides.add(slide);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return slides;
    }

    public boolean batch_insert_slides(ArrayList<Slide> slides) {
        db.beginTransaction();
        try {
            for (Slide item : slides) {
                db.insert(slide_table, null, parseToContent(item));
            }
            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
        }
    }

    private ContentValues parseToContent(Slide slide) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", slide.getId());
        contentValues.put("link", slide.getLink());
        contentValues.put("update_token", slide.getUpdate_token());
        contentValues.put("tab_title", slide.getTabTitle());
        contentValues.put("title", slide.getTitle());
        contentValues.put("description", slide.getDescription());
        return contentValues;
    }


    public ArrayList<Slide> getSlides() {
        Cursor cursor = getQuery(slide_table, null);
        ArrayList<Slide> slides = parseSlides(cursor, false);
        cursor.close();
        if (slides.isEmpty()) {
            return null;
        } else {
            return slides;
        }

    }

    private ArrayList<Slide> parseSlides(Cursor paramCursor, boolean single) {
        ArrayList<Slide> slides = new ArrayList<>();
        if (paramCursor != null) {
            while (paramCursor.moveToNext()) {
                Slide slide = new Slide();
                slide.setId(paramCursor.getInt(0));
                slide.setLink(paramCursor.getString(1));
                slide.setUpdate_token(paramCursor.getString(2));
                slide.setTitle(paramCursor.getString(3));
                slide.setTabTitle(paramCursor.getString(4));
                slide.setDescription(paramCursor.getString(5));
                slides.add(slide);
                if (single) {
                    break;
                }
            }
            paramCursor.close();
        }
        return slides;
    }

    public Slide getSlide(int id) {
        HashMap<String, String> map = new HashMap<>();
        map.put("id", id + "");
        Cursor cursor = getQuery(slide_table, map);
        return parseSlides(cursor,true).get(0);
    }
    public int update(Slide slide) {
        ContentValues contentValues = parseToContent(slide);
        String keys = "id = ?";
        String[] vals = {slide.getId() + ""};
        return db.update(slide_table, contentValues, keys, vals);
    }
    private CartItem parseCartItem(Cursor paramCursor) {
        ArrayList<CartItem> cartItems = parseCartItems(paramCursor, true);
        if (cartItems.isEmpty()) {
            return null;
        }
        return cartItems.get(0);
    }
    public CartItem getCartItem(int p_id){
        Cursor cursor = customQuery(cart_table,"*","p_id="+p_id);
        return parseCartItem(cursor);
    }
    private ArrayList<CartItem> parseCartItems(Cursor paramCursor, boolean single) {
        ArrayList<CartItem> cartItems = new ArrayList<>();
        if (paramCursor != null) {
            while (paramCursor.moveToNext()) {
                CartItem cartItem = new CartItem();
                cartItem.setId(paramCursor.getInt(0));
                cartItem.setTitle(paramCursor.getString(1));
                cartItem.setpId(paramCursor.getInt(2));
                cartItem.setCount(paramCursor.getInt(3));
                cartItem.setPrice(paramCursor.getInt(4));
                cartItems.add(cartItem);
                if (single) {
                    break;
                }
            }
            paramCursor.close();
        }
        return cartItems;
    }

    private ContentValues parseToContent(CartItem cartItem) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", cartItem.getTitle());
        contentValues.put("count", cartItem.getCount());
        contentValues.put("price", cartItem.getPrice());
        contentValues.put("p_id", cartItem.getpId());
        return contentValues;
    }
    public ArrayList<CartItem> getCartItems() {
        Cursor cursor = getQuery(cart_table, null);
        ArrayList<CartItem> cartItems = parseCartItems(cursor, false);
        cursor.close();
        if (cartItems.isEmpty()) {
            return null;
        } else {
            return cartItems;
        }

    }
    public boolean batch_insert_cart(ArrayList<CartItem> cartItems) {
        db.beginTransaction();
        try {
            for (CartItem item : cartItems) {
                db.insert(cart_table, null, parseToContent(item));
            }
            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
        }
    }
    public Long insert(CartItem cartItem) {
        int res = this.update(cartItem);
        if (res == 0) {
            return db.insert(cart_table, null, parseToContent(cartItem));
        } else {
            return (long) res;
        }
    }


    public int update(CartItem cartItem) {

        CartItem cartItem1=getCartItem(cartItem.getpId());
        if(cartItem1!=null){
            cartItem1.setCount(cartItem.getCount()+cartItem1.getCount());

            ContentValues contentValues = parseToContent(cartItem1);
            String keys = "id = ?";
            String[] vals = {cartItem1.getId() + ""};
            return db.update(cart_table, contentValues, keys, vals);
        }
        else{
            return 0;
        }
    }
    public void clearCart() {
        db.execSQL("DELETE FROM " + cart_table);
    }
}
