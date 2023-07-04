package FrameW.etu1987.framework;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;

public class Outil {

    // Get class in the package
    public static List<Class> getClassFrom(String  packages) throws Exception{
        String path = packages.replaceAll("[.]", "\\\\");
        URL packageUrl = Thread.currentThread().getContextClassLoader().getResource(path);
        File packDir =new File(packageUrl.toURI());
        File[] inside = packDir.listFiles(file->file.getName().endsWith(".class"));
        List<Class> lists = new ArrayList<>();
        for(File f : inside){
            String c = packages+"."+f.getName().substring(0,f.getName().lastIndexOf("."));
            lists.add(Class.forName(c));
        }
        return lists;
    }

}
