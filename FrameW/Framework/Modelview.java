package etu2044.framework;
import java.util.HashMap;


public class Modelview {
    String view;
    HashMap<String, Object> data = new HashMap<String, Object>();


    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }

    public  HashMap<String, Object> getData(){
        return this.data;
    }
    public String getView() {
        return view;
    }
    public void setView(String view) {
        this.view = view;
    }
    public void addItem(String key,Object object) {
        this.getData().put(key,object);
    }
}
