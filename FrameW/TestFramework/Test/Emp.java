package Test;

import etu2044.framework.Url;
import etu2044.framework.Scope;
import etu2044.framework.Annotation;
import etu2044.framework.FileUploader;
import et2044.framework.Authentication;
import etu2044.framework.Session;
import etu2044.framework.RestAPI;

import java.sql.Date;
import java.util.HashMap;

import etu1987.framework.Modelview;
@Scope(type="Emp")
public class Emp {
    HashMap<String, Object> session; //tsy maintsy session nenatarany
    String nom;
    Integer t;
    java.sql.Date daty;
    java.util.Date da;
    String[] table;
    FileUploader file;


    public void setSession(HashMap<String, Object> session) {
        this.session = session;
    }
    public HashMap<String, Object> getSession() {
        return session;
    }
    public void setFile(FileUploader file) {
        this.file = file;
    }
    public FileUploader getFile() {
        return file;
    }

    public String[] getTable() {
        return table;
    }
    public void setTable(String[] table) {
        this.table = table;
    }

    public void setDa(java.util.Date da) {
        this.da = da;
    }

    public java.util.Date getDa() {
        return da;
    }
    public void setDaty(java.sql.Date daty) {
        this.daty = daty;
    }
    public java.sql.Date getDaty() {
        return daty;
    }
    public void setT(Integer t) {
        this.t = t;
    }
    public Integer getT() {
        return t;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getNom() {
        return nom;
    }
    
    @Authentication(profile = "admin")
    @Url(url="find-All")
    public Modelview FindAll() {
        Modelview m = new Modelview();
        m.setView("Ay.jsp");
        System.out.println(" Admin rery ");
        return m;
    }
    
    @Url(url="get-form")
    public Modelview getForm() {
        Modelview m = new Modelview();
        System.out.println(this.getFile().getName());
        m.setView("Ay.jsp");
        return m;
    }
    @Authentication()
    @Url(url = "parameter-type")
    public Modelview params(@Annotation(parametre = "test") Integer test) {
        Modelview m = new Modelview();
        m.addItem("data", "data");
        m.setGson(true);
        m.setView("Ay.jsp");
        return m;
    }
    
    @Url(url = "login")
    public Modelview login() {
        Modelview m = new Modelview();
        m.addSession("isConnected", true);
        m.addSession("profile", "admin");
        m.setView("index.jsp");
        return m;
    }
    
    @Session
    @Url(url = "session")
    public Modelview testSession() {
        Modelview m = new Modelview();
        this.getSession().put("profile", "test");
        m.setView("index.jsp");
        return m;
    }
    @RestAPI()
    @Url(url = "list")
    public Emp ListEmp() {
        Emp m = new Emp();
        m.setNom("RAKPO");
        m.setT(12);
        return m;
    }

}
