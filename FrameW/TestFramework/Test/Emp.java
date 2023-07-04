package Test;

import etu2044.framework.Url;
import etu2044.framework.Modelview;

public class Emp {
    String nom;
    Integer t;

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
    @Url(url="find-All")
    public Modelview FindAll() {
        Modelview m = new Modelview();
        m.setView("Ay.jsp");
        int u = 23;
        m.addItem("data", u);
        System.out.println(" find_all ");
        return m;
    }
    
    @Url(url="get-form")
    public Modelview getForm() {
        Modelview m = new Modelview();
        m.setView("Ay.jsp");
        System.out.println(this.getNom());
        return m;
    }

}
