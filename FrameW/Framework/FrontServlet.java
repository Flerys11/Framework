package etu2044.framework.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.management.modelmbean.RequiredModelMBean;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import etu2044.framework.Annotation;
import etu2044.framework.Mapping;
import etu2044.framework.Modelview;
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
            String url = request.getRequestURI().substring(request.getContextPath().length()+1);
            if (this.mappingUrls.containsKey(url))
                {
                    Mapping mapping = this.mappingUrls.get(url);
                    Class clazz = Class.forName(mapping.getClassName());
                    Field[] fields = clazz.getDeclaredFields();
                    Object object = clazz.getConstructor().newInstance();
                    Enumeration<String> nom = request.getParameterNames();
                    List<String> list = Collections.list(nom);
                    for (int w = 0; w < fields.length; w++) {
                        String table = fields[w].getName() + ((fields[w].getType().isArray()) ? "[]" : "");
                        for (int g = 0; g < list.size(); g++) {
                            if (table.trim().equals(list.get(g).trim())) {
                                String s1 = fields[w].getName().substring(0, 1).toUpperCase();
                                String seter = s1 + fields[w].getName().substring(1);
                                Method me = clazz.getMethod("set" + seter, fields[w].getType());
                                if (fields[w].getType().isArray() == false) {
                                    String object2 = request.getParameter(fields[w].getName());
                                    if (fields[w].getType() == java.util.Date.class) {
                                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                                        Date obj = format.parse(object2);
                                        me.invoke(object, obj);
                                    } else if (fields[w].getType() == java.sql.Date.class) {
                                        java.sql.Date obj = java.sql.Date.valueOf(object2);
                                        me.invoke(object, obj);
                                    } else {
                                        Object obj = fields[w].getType().getConstructor(String.class)
                                                .newInstance(object2);
                                        me.invoke(object, obj);
                                    }
                                } else {
                                    String[] strings = request.getParameterValues(table);
                                    me.invoke(object, (Object) strings);
                                }
                            }
                        }
                    }
                    Method[] methods = object.getClass().getDeclaredMethods();
                    Method equalMethod = null;
                    for (int i = 0; i < methods.length; i++) {
                        if (methods[i].getName().trim().compareTo(mapping.getMethod()) == 0) {
                            equalMethod = methods[i];
                            break;
                        }
                    }
                    
                    Parameter[] parameters = equalMethod.getParameters();
                    System.out.println(parameters);
                    Object[] params = new Object[parameters.length];
                    // 
                    for (int w = 0; w < parameters.length; w++) {
                        if (parameters[w].isAnnotationPresent(Annotation.class)) {
                            Annotation pAnnotation = parameters[w].getAnnotation(Annotation.class);
                            String table = pAnnotation.parametre() + ((parameters[w].getType().isArray()) ? "[]" : "");
                            for (int g = 0; g < list.size(); g++) {
                                if (table.trim().equals(list.get(g).trim())) {
                                    if (parameters[w].getType().isArray() == false) {
                                        String object2 = request.getParameter(pAnnotation.parametre());
                                        if (parameters[w].getType() == java.util.Date.class) {
                                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                                            Date obj = format.parse(object2);
                                            params[w]=obj;
                                        } else if (parameters[w].getType() == java.sql.Date.class) {
                                            java.sql.Date obj = java.sql.Date.valueOf(object2);
                                            params[w]=obj;
                                        } else {
                                            Object obj = parameters[w].getType().getConstructor(String.class).newInstance(object2);
                                            params[w]=obj;
                                        }
                                    } else {
                                        String[] strings = request.getParameterValues(table);
                                        params[w] = strings;
                                    }
                                }
                            }
                        }
                    }
                    // 
                    Object returnObject = equalMethod.invoke(object, (Object[]) params);
                    if (returnObject instanceof Modelview) {
                        Modelview modelview = (Modelview) returnObject;
                        HashMap<String, Object> data = modelview.getData();
                        for (Map.Entry<String,Object> o : data.entrySet()) {
                            request.setAttribute( o.getKey() , o.getValue() );
                        }
                        RequestDispatcher requestDispatcher = request.getRequestDispatcher(modelview.getView());
                        requestDispatcher.forward(request, response);
                    }
                }
        } catch (Exception e) {
            e.printStackTrace();
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