package ir.codetower.samanshiri.Models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ir.codetower.samanshiri.App;

/**
 * Created by Mr-R00t on 11/25/2017.
 */

public class Order {
    private int id;
    private double amount;
    private String ref_id;
    private String authority;
    private String type;
    private ArrayList<Product> products;
    private String address;
    private String postal;
    private String phone;
    private String status;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getRef_id() {
        return ref_id;
    }

    public void setRef_id(String ref_id) {
        this.ref_id = ref_id;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostal() {
        return postal;
    }

    public void setPostal(String postal) {
        this.postal = postal;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public static Order jsonArrayToOrderItem(String text) throws JSONException {
        JSONObject jsonItem = new JSONObject(text);
        Order order = new Order();
        order.setId(jsonItem.getInt("id"));
        order.setAddress(jsonItem.getString("address"));
        order.setAuthority(jsonItem.getString("authority"));
        order.setPhone(jsonItem.getString("phone"));
        order.setRef_id(jsonItem.getString("ref_id"));
        order.setStatus(jsonItem.getString("status"));
        order.setPostal(jsonItem.getString("postal"));
        order.setType(jsonItem.getString("type"));
        order.setAmount(Double.parseDouble(jsonItem.getString("amount")));
        order.setProducts(App.dbHelper.jsonArrayToProduct(jsonItem.getString("products")));
        return order;
    }

}
