package kr.mjc.jacob.web.model2.user;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/model2/user/joinForm")
public class JoinFormServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    request.getRequestDispatcher("/WEB-INF/jsp/model2/user/joinForm.jsp")
        .forward(request, response);
  }
}
