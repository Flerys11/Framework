package Test;

import etu1987.framework.Url;

import java.sql.Date;

import etu2044.framework.Modelview;

public class Emp {
    String nom;
    Integer t;
    java.sql.Date daty;
    java.util.Date da;

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
        System.out.println(this.getDaty());
        System.out.println(this.getDa());
        return m;
    }

}
