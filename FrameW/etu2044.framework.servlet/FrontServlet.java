package FrameW.etu1987.framework;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * FrontServler
 */
public class FrontServlet extends HttpServlet {
    HashMap<String,Mapping> mappingUrls;


    public void processRequest( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        try( PrintWriter out = response.getWriter() ){
            out.println(" Bonjour ");
        }
    }

    @Override
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws IOException, ServletException{
        processRequest(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request,
                      HttpServletResponse response)
        throws IOException, ServletException
    {
        processRequest(request, response);
    }
}