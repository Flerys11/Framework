package etu2044.framework;
import java.util.HashMap;


public class Modelview {
    String view;
    HashMap<String, Object> data = new HashMap<String, Object>();
    HashMap<String, Object> session = new HashMap<String, Object>();


    public void setSession(HashMap<String, Object> session) {
        this.session = session;
    }
    public HashMap<String, Object> getSession() {
        return session;
    }
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

    public void addItem(String key, Object object) {
        this.getData().put(key, object);
    }
    public void addSession(String key, Object object) {
        this.getSession().put(key, object);
    }
}
