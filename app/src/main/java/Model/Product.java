package Model;

public class Product {

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    String pname;
    String price;
    String time;
    String pid;
    String image;
    String image1;
    String image2;
    String description;
    String date;
    String category;

    public Product(String pname, String price, String time, String pid, String image, String image1, String image2, String description, String date, String category) {
        this.pname = pname;
        this.price = price;
        this.time = time;
        this.pid = pid;
        this.image = image;
        this.image1 = image1;
        this.image2 = image2;
        this.description = description;
        this.date = date;
        this.category = category;
    }

    public Product(){

    }
}
