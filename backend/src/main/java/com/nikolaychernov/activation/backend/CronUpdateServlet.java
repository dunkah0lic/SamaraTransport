package com.nikolaychernov.activation.backend;

import com.googlecode.objectify.ObjectifyService;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Nikolay on 12.04.2015.
 */
public class CronUpdateServlet extends HttpServlet{

    static {
        ObjectifyService.register(User.class);
    }

    private static String defaultPackageName = "air.nikolaychernov.samis.ChernovPryb";
    private static String productid = "license";

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String accessToken = Utils.getAccessToken();
        List<User> list = ObjectifyService.ofy().load().type(User.class).filter("access", 0).list();
        for (User user : list){
            int access = 1;
            access = Utils.checkLicense(defaultPackageName, productid, user.getToken(), accessToken);
            user.setAccess(access);

        }
        ObjectifyService.ofy().save().entities(list);

        resp.setContentType("text/plain");
        resp.getWriter().println("");
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String name = req.getParameter("name");
        resp.setContentType("text/plain");
        if (name == null) {
            resp.getWriter().println("Please enter a name");
        }
        resp.getWriter().println("Hello " + name);
    }
}
