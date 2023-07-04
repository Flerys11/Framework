package etu2044.framework.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.management.modelmbean.RequiredModelMBean;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;


import etu2044.framework.Annotation;
import etu2044.framework.Authentication;
import etu2044.framework.FileUploader;
import etu2044.framework.Mapping;
import etu2044.framework.Modelview;
import etu2044.framework.Outil;
import etu2044.framework.Url;
import etu2044.framework.Scope;
import etu2044.framework.Session;
import etu2044.framework.RestAPI;

/**
 * FrontServler
 */
@MultipartConfig
public class FrontServlet extends HttpServlet {
    HashMap<String, Mapping> mappingUrls = new HashMap<String, Mapping>();
    HashMap<String, Object> singleton = new HashMap<String, Object>();
    String sessionName;
    String sessionProfile;

    public void init() {
        String name_package = "Test";
        try {
            sessionName = getInitParameter("sessionName");
            sessionProfile = getInitParameter("sessionProfile");
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
                for (int j = 0; j < all_Class.size(); j++) {
                    if (all_Class.get(j).isAnnotationPresent(Scope.class)) {
                        this.singleton.put(all_Class.get(j).getName(), null);
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    // File traitement part
    private FileUploader fileTraitement(Collection<Part> files, Field field) {
        FileUploader file = new FileUploader();
        String name = field.getName();
        boolean exists = false;
        String filename = null;
        Part filepart = null;
        for (Part part : files) {
            if (part.getName().equals(name)) {
                filepart = part;
                exists = true;
                break;
            }
        }
        try (InputStream io = filepart.getInputStream()) {
            ByteArrayOutputStream buffers = new ByteArrayOutputStream();
            byte[] buffer = new byte[(int) filepart.getSize()];
            int read;
            while ((read = io.read(buffer, 0, buffer.length)) != -1) {
                buffers.write(buffer, 0, read);
            }
            file.setName(this.getFileName(filepart));
            file.setBytes(buffers.toByteArray());
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    //
    //
     private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        String[] parts = contentDisposition.split(";");
        for (String partStr : parts) {
            if (partStr.trim().startsWith("filename"))
                return partStr.substring(partStr.indexOf('=') + 1).trim().replace("\"", "");
        }
        return null;
    }
    //
    public void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try (PrintWriter out = response.getWriter()) {
            String url = request.getRequestURI().substring(request.getContextPath().length()+1);
            if (this.mappingUrls.containsKey(url))
            {
                Mapping mapping = this.mappingUrls.get(url);
                Class clazz = Class.forName(mapping.getClassName());
                Field[] fields = clazz.getDeclaredFields();
                Object object = null;
                if (this.singleton.containsKey(clazz.getName())) {
                    if (this.singleton.get(clazz.getName()) != null) {
                        object = this.singleton.get(clazz.getName());
                    } else {
                        object = clazz.getConstructor().newInstance();
                        this.singleton.replace(clazz.getName(), null, object);
                    }
                } else {
                    object = clazz.getConstructor().newInstance();
                }
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
                                        params[w] = obj;
                                    } else if (parameters[w].getType() == java.sql.Date.class) {
                                        java.sql.Date obj = java.sql.Date.valueOf(object2);
                                        params[w] = obj;
                                    } else {
                                        Object obj = parameters[w].getType().getConstructor(String.class)
                                                .newInstance(object2);
                                        params[w] = obj;
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
                try {
                    Collection<Part> files = request.getParts();
                    for (Field f : fields) {
                        if (f.getType() == etu1987.framework.FileUploader.class) {
                            String s1 = f.getName().substring(0, 1).toUpperCase();
                            String seter = s1 + f.getName().substring(1);
                            Method m = clazz.getMethod("set" + seter, f.getType());
                            Object o = this.fileTraitement(files, f);
                            m.invoke(object, o);
                        }
                    }
                } catch (Exception e) {

                }

                // 
                Object returnObject = null;
                if (equalMethod.isAnnotationPresent(Authentication.class)) {
                    Authentication auth = equalMethod.getAnnotation(Authentication.class);
                    if (request.getSession().getAttribute(sessionName) != null) {
                        if ((auth.profile().trim().isEmpty() == false
                                && !auth.profile().trim().equals(
                                        String.valueOf(request.getSession().getAttribute(sessionProfile)).trim()))) {
                            throw new Exception(" privilege non accorder ");
                        }
                    } else {
                        throw new Exception(" null  tsy misy session ");
                    }
                }
                if (equalMethod.isAnnotationPresent(Session.class)) {
                    Method method = clazz.getDeclaredMethod("setSession", HashMap.class);
                    HashMap<String, Object> ses = new HashMap<String, Object>();
                    Enumeration<String> noms = request.getSession().getAttributeNames();
                    List<String> listeStrings = Collections.list(noms);
                    for (String string : listeStrings) {
                        Object temp = request.getSession().getAttribute(string);
                        ses.put(string, temp);
                    }
                    method.invoke(object, ses);
                }
                returnObject = equalMethod.invoke(object, (Object[]) params);

                if (equalMethod.isAnnotationPresent(Session.class)) {
                    Method method = clazz.getDeclaredMethod("getSession");
                    HashMap<String, Object> ses = new HashMap<String, Object>();
                    ses = (HashMap<String, Object>) method.invoke(object);
                    for (Map.Entry<String, Object> o : ses.entrySet()) {
                        request.getSession().setAttribute(o.getKey(), o.getValue());
                    }
                }

                if (returnObject instanceof Modelview) {
                    Modelview modelview = (Modelview) returnObject;
                    HashMap<String, Object> data = modelview.getData();
                    HashMap<String, Object> session = modelview.getSession();
                    for (Map.Entry<String, Object> o : data.entrySet()) {
                        request.setAttribute(o.getKey(), o.getValue());
                    }
                    for (Map.Entry<String, Object> o : session.entrySet()) {
                        request.getSession().setAttribute(o.getKey(), o.getValue());
                    }
                    if (modelview.isGson()) {
                        response.setContentType("application/json");
                        out.println(new com.google.gson.Gson().toJson(data));
                    } else {
                        RequestDispatcher requestDispatcher = request.getRequestDispatcher(modelview.getView());
                        requestDispatcher.forward(request, response);
                    }
                }
                else if (equalMethod.isAnnotationPresent(RestAPI.class)) {
                    response.setContentType("application/json");
                    out.println(new com.google.gson.Gson().toJson(returnObject));
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