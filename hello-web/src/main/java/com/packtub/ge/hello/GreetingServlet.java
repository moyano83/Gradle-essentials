package com.packtub.ge.hello;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by jm186111 on 05/04/2016.
 */
@WebServlet("/greet")
public class GreetingServlet extends HttpServlet{

    private static GreetingService service = new GreetingService();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        req.setAttribute("name", service.greet(name));
        RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/greet.jsp");
        dispatcher.forward(req, resp);
    }
}
