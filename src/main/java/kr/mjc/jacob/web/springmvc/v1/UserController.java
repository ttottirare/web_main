package kr.mjc.jacob.web.springmvc.v1;

import kr.mjc.jacob.web.dao.User;
import kr.mjc.jacob.web.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Servlet API를 사용해서 구현한 UserController
 */
@Controller("userControllerV1")
@RequestMapping("/springmvc/v1/user")
public class UserController {

  private final UserDao userDao;

  @Autowired
  public UserController(UserDao userDao) {
    this.userDao = userDao;
  }

  /**
   * 사용자 목록 화면
   */
  @GetMapping("/userList")
  public void userList(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String pageStr = Optional.ofNullable(request.getParameter("page")).orElse(
        "1");
    int page = Integer.parseInt(pageStr);
    int count = 25;
    int offset = (page - 1) * count;

    List<User> userList = userDao.listUsers(offset, count);
    request.setAttribute("userList", userList);

    request.getRequestDispatcher("/WEB-INF/jsp/springmvc/v1/user/userList.jsp")
        .forward(request, response);
  }

  /**
   * 사용자 등록 화면
   */
  @GetMapping("/joinForm")
  public void userForm(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    request.getRequestDispatcher("/WEB-INF/jsp/springmvc/v1/user/joinForm.jsp")
        .forward(request, response);
  }

  /**
   * 로그인 화면
   */
  @GetMapping("/loginForm")
  public void loginForm(HttpServletRequest request,
      HttpServletResponse response)
      throws ServletException, IOException {

    request.getRequestDispatcher("/WEB-INF/jsp/springmvc/v1/user/loginForm.jsp")
        .forward(request, response);
  }

  /**
   * 개인정보 화면
   */
  @GetMapping("/userInfo")
  public void userInfo(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    request.getRequestDispatcher("/WEB-INF/jsp/springmvc/v1/user/userInfo.jsp")
        .forward(request, response);
  }

  /**
   * 사용자 등록 액션
   */
  @PostMapping("/join")
  public void addUser(HttpServletRequest request, HttpServletResponse response)
      throws IOException {

    User user = new User();
    user.setEmail(request.getParameter("email"));
    user.setPassword(request.getParameter("password"));
    user.setName(request.getParameter("name"));

    try {
      userDao.addUser(user);
      response.sendRedirect(
          request.getContextPath() + "/app/springmvc/v1/user/userList");
    } catch (DuplicateKeyException e) {
      response.sendRedirect(
          request.getContextPath() +
              "/app/springmvc/v1/user/userForm?msg=Duplicate email");
    }
  }

  /**
   * 로그인 액션
   */
  @PostMapping("/login")
  public void login(HttpServletRequest request, HttpServletResponse response)
      throws IOException {

    String email = request.getParameter("email");
    String password = request.getParameter("password");

    try {
      User user = userDao.login(email, password);
      HttpSession session = request.getSession();
      session.setAttribute("USER", user);
      response.sendRedirect(
          request.getContextPath() + "/app/springmvc/v1/user/userInfo");
    } catch (EmptyResultDataAccessException e) {
      response.sendRedirect(request.getContextPath() +
          "/app/springmvc/v1/user/loginForm?msg=Wrong email or password");
    }
  }


  /**
   * 로그 아웃
   */
  @GetMapping("/logout")
  public void logout(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    HttpSession session = request.getSession();
    session.invalidate();
    response.sendRedirect(request.getContextPath() + "/");
  }
}
