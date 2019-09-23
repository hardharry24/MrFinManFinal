package Models;

public class Category_bp_list {

    int catID;
    String name;
    String icon;


    public Category_bp_list(int catID,String name, String img_id) {
        this.name = name;
        this.icon = img_id;
        this.catID = catID;
    }
    public int getCatID() {
        return catID;
    }

    public void setCatID(int catID) {
        this.catID = catID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg_id() {
        return icon;
    }

    public void setImg_id(String img_id) {
        this.icon = img_id;
    }





}
