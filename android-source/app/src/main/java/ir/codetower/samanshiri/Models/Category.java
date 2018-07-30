package ir.codetower.samanshiri.Models;

import java.util.ArrayList;

import ir.codetower.samanshiri.R;


public class Category {
    private int id;
    private String title;
    private String image;
    private String description;
    private String created_at;
    private String updated_at;
    private int icon;
    private int u_id;
    private String update_token;
    private int section;

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }

    public String getUpdate_token() {
        return update_token;
    }

    public void setUpdate_token(String update_token) {
        this.update_token = update_token;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getU_id() {
        return u_id;
    }

    public void setU_id(int u_id) {
        this.u_id = u_id;
    }

    public static ArrayList<Category> getFakeCategory(){
        ArrayList<Category> categories=new ArrayList<>();
        for (int i=0;i<4;i++){
            Category category=new Category();
            category.setId(i+1);
            category.setTitle("لورم ایپسوم یا طرح نما"+" "+i);
            category.setIcon(R.drawable.splash_bach);
            category.setDescription("طراح گرافیک از این متن به عنوان عنصری از ترکیب بندی برای پر کردن صفحه و ارایه اولیه شکل ظاهری و کلی طرح سفارش گرفته شده استفاده می نماید، تا از نظر گرافیکی نشانگر چگونگی نوع و اندازه فونت و ظاهر متن باشد.");
            categories.add(category);
        }
        return categories;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", description='" + description + '\'' +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                ", icon=" + icon +
                ", u_id=" + u_id +
                ", update_token='" + update_token + '\'' +
                ", section=" + section +
                '}';
    }
}

