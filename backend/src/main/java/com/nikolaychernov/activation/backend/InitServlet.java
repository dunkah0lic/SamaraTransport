package com.nikolaychernov.activation.backend;

import com.googlecode.objectify.ObjectifyService;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Nikolay on 10.04.2015.
 */
public class InitServlet extends HttpServlet {

    static {
        ObjectifyService.register(User.class);
    }

    private static String defaultPackageName = "air.nikolaychernov.samis.ChernovPryb";
    private static String productid = "license";

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String email = req.getParameter("name");
        String token = req.getParameter("token");


        //String accessToken = Utils.getAccessToken(refreshToken);
        int access = Utils.checkLicense(defaultPackageName, productid, token);

        User temp = new User(email, token,access);

        ObjectifyService.ofy().save().entity(temp);

        User temp2 = ObjectifyService.ofy().load().type(User.class).filter("email <>", "test").first().now();
        List<User> list = ObjectifyService.ofy().load().type(User.class).filter("email <>", "leigh").list();
        User temp3 = list.get(0);

        resp.setContentType("text/plain");


        resp.getWriter().println("" + temp2.getEmail() + "  " + temp2.getToken());
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
