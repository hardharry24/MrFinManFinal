package Singleton;

public class ViewTypeSingleton {

    private static ViewTypeSingleton instance;

    private String typeview ="Day";

    public String getTypeview() {
        return typeview;
    }

    public void setTypeview(String typeview) {
        this.typeview = typeview;
    }

    public static ViewTypeSingleton getInstance()
    {
        if (instance == null)
            instance = new ViewTypeSingleton();
        return instance;
    }



    public static ViewTypeSingleton resetInstance()
    {
        return instance = null;
    }








}
