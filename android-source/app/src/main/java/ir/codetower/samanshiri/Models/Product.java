package ir.codetower.samanshiri.Models;

/**
 * Created by Mr-R00t on 7/31/2017.
 */

public class Product {
    private int id;
    private String title;
    private String image;
    private String a_image1;
    private String a_image2;
    private String a_image3;
    private String a_image4;
    private String a_image5;
    private String a_image6;
    private String description;
    private int cat_id;
    private String cat_update_token;
    private int u_id;
    private String created_at;
    private String updated_at;
    private int icon;
    private boolean top;
    private String zipFile;
    private boolean visible;
    public static final int TYPE_NEWS=0;
    public static final int TYPE_PRODUCT=1;
    private int type;
    private String downloadLink;
    private int price;
    private boolean download;
    private boolean video;
    private String video_url;
    private String videoThumb;
    private String update_token;
    private boolean favorite;
    private int seen;

    public int getSeen() {
        return seen;
    }

    public void setSeen(int seen) {
        this.seen = seen;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getCat_update_token() {
        return cat_update_token;
    }

    public void setCat_update_token(String cat_update_token) {
        this.cat_update_token = cat_update_token;
    }

    public String getUpdate_token() {
        return update_token;
    }

    public void setUpdate_token(String update_token) {
        this.update_token = update_token;
    }

    public String getVideoThumb() {
        return videoThumb;
    }

    public void setVideoThumb(String videoThumb) {
        this.videoThumb = videoThumb;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public boolean isDownload() {
        return download;
    }

    public void setDownload(boolean download) {
        this.download = download;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getZipFile() {
        return zipFile;
    }

    public void setZipFile(String zipFile) {
        this.zipFile = zipFile;
    }

    public boolean isTop() {
        return top;
    }

    public void setTop(boolean top) {
        this.top = top;
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

    public String getA_image1() {
        return a_image1;
    }

    public void setA_image1(String a_image1) {
        this.a_image1 = a_image1;
    }

    public String getA_image2() {
        return a_image2;
    }

    public void setA_image2(String a_image2) {
        this.a_image2 = a_image2;
    }

    public String getA_image3() {
        return a_image3;
    }

    public void setA_image3(String a_image3) {
        this.a_image3 = a_image3;
    }

    public String getA_image4() {
        return a_image4;
    }

    public void setA_image4(String a_image4) {
        this.a_image4 = a_image4;
    }

    public String getA_image5() {
        return a_image5;
    }

    public void setA_image5(String a_image5) {
        this.a_image5 = a_image5;
    }

    public String getA_image6() {
        return a_image6;
    }

    public void setA_image6(String a_image6) {
        this.a_image6 = a_image6;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCat_id() {
        return cat_id;
    }

    public void setCat_id(int cat_id) {
        this.cat_id = cat_id;
    }

    public int getU_id() {
        return u_id;
    }

    public void setU_id(int u_id) {
        this.u_id = u_id;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", a_image1='" + a_image1 + '\'' +
                ", a_image2='" + a_image2 + '\'' +
                ", a_image3='" + a_image3 + '\'' +
                ", a_image4='" + a_image4 + '\'' +
                ", a_image5='" + a_image5 + '\'' +
                ", a_image6='" + a_image6 + '\'' +
                ", description='" + description + '\'' +
                ", cat_id=" + cat_id +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                ", icon=" + icon +
                '}';
    }
}
