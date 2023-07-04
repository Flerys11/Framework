package etu2044.framework.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import etu2044.framework.Mapping;
import etu2044.framework.Outil;
import etu2044.framework.Url;

/**
 * FrontServler
 */
public class FrontServlet extends HttpServlet {
    HashMap<String, Mapping> mappingUrls = new HashMap<String, Mapping>();

    public void init() {
        String name_package = "Test";
        try {
            List<Class> all_Class = Outil.getClassFrom(name_package);
            for (int i = 0; i < all_Class.size(); i++) {
                Class class_Temp = all_Class.get(i);
                Method[] methods = class_Temp.getDeclaredMethods();
                for (int j = 0; j < methods.length; j++) {
                    if (methods[j].isAnnotationPresent(Url.class)) {
                        Mapping mapping = new Mapping(class_Temp.getName(), methods[j].getName());
                        this.mappingUrls.put(methods[j].getAnnotation(Url.class).url(), mapping);
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    public void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try (PrintWriter out = response.getWriter()) {
            out.println(" Bonjour ");
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        processRequest(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        processRequest(request, response);
    }
}