package Model;

public class Cart {

    String pname;
    String image;
    String pid;
    String price;
    String quantity;

    public Cart(String pname, String image, String pid, String price, String quantity, String category) {
        this.pname = pname;
        this.image = image;
        this.pid = pid;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
    }

    String category;

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Cart(){

    }

}
