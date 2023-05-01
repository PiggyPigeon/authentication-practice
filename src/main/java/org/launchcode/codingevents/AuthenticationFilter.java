package org.launchcode.codingevents;

import org.launchcode.codingevents.controllers.AuthenticationController;
import org.launchcode.codingevents.data.UserRepository;
import org.launchcode.codingevents.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.handler.WebRequestHandlerInterceptorAdapter;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;


//in our text book it uses 'HandlerInterceptorAdapter' instead of 'WebRequestHandlerInterceptorAdapter'
//the webrequest one is a subclass of the former that adds methods specifically for handling
//web requests in a Spring web application.
//"If you need to intercept and modify the behavior of web requests in your Spring web application, you would typically use WebRequestHandlerInterceptorAdapter"
//I dont know what this means but we may want to play with this when it comes to errors on the group project...
public class AuthenticationFilter extends HandlerInterceptorAdapter {
    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationController authenticationController;

    //below is the 'whitelist' which declares which pages can be seen by users who are not logged in
    //ntote: many web apps have a home page that does not require being logged in to view.
    private static final List<String> whitelist = Arrays.asList("/login", "/register", "/logout", "/css");

    //below boolean checks whether a given request is whitelisted or not
    //If you wanted to be more restrictive, you could use .equals() instead of .startsWith().
    private static boolean isWhitelisted(String path) {
        for (String pathRoot : whitelist) {
            if (path.startsWith(pathRoot)) {
                return true;
            }
        }
        return false;
    }

    //below prehandle code happens before a request is handled by a controller.
    //this one is making sure that a user is logged in
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws IOException {

        // Don't require sign-in for whitelisted pages
        if (isWhitelisted(request.getRequestURI())) {
            // returning true indicates that the request may proceed
            return true;
        }

        HttpSession session = request.getSession();
        User user = authenticationController.getUserFromSession(session);

        // The user is logged in
        if (user != null) {
            return true;
        }

        // The user is NOT logged in
        response.sendRedirect("/login");
        return false;
    }






}
