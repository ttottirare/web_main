package kr.mjc.jacob.web.springmvc.v2;

import kr.mjc.jacob.web.dao.User;
import kr.mjc.jacob.web.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Servlet API를 사용하지 않는 UserController
 */
@Controller("userControllerV2")
@RequestMapping("/springmvc/v2/user")
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
  public void userList(
      @RequestParam(required = false, defaultValue = "1") int page,
      @RequestParam(required = false, defaultValue = "20") int count,
      Model model) {
    int offset = (page - 1) * count;
    List<User> userList = userDao.listUsers(offset, count);
    int totalCount = userDao.countUsers();
    model.addAttribute("userList", userList);
    model.addAttribute("totalCount", totalCount);
  }

  /**
   * 사용자 정보 화면
   */
  @GetMapping("/userInfo")
  public void userInfo(@RequestParam(required = false) Integer userId,
      Model model) {
    model.addAttribute("user", userDao.getUser(userId));
  }

  /**
   * 사용자 등록 액션
   * html form에서 submit한 데이터가 @ModelAttribute 객체(Command Object)에 바인딩됨.
   */
  @PostMapping("/join")
  public String addUser(@ModelAttribute User user,
      RedirectAttributes attributes) {
    try {
      userDao.addUser(user);
      return "redirect:/app/springmvc/v2/user/userList";
    } catch (DuplicateKeyException e) {
      // redirect할때 attribute를 저장
      attributes.addFlashAttribute("msg", "Duplicate email");
      return "redirect:/app/springmvc/v2/user/joinForm";
    }
  }

  /**
   * 로그인 액션
   */
  @PostMapping("/login")
  public String login(@RequestParam String email, @RequestParam String password,
      @RequestParam(required = false, defaultValue = "/") String returnUrl,
      HttpSession session, RedirectAttributes attributes) {
    try {
      User user = userDao.login(email, password);
      session.setAttribute("USER", user);
      return "redirect:" + returnUrl; // 로그인 성공할 경우 returnUrl로
    } catch (EmptyResultDataAccessException e) { // 로그인 실패할 경우
      attributes.addFlashAttribute("email", email);
      attributes.addFlashAttribute("msg", "Wrong email or password");
      return "redirect:/app/springmvc/v2/user/loginForm?returnUrl=" +
          URLEncoder.encode(returnUrl, Charset.defaultCharset());
    }
  }

  /**
   * 개인정보 수정 액션
   */
  @PostMapping("/s/updateUser")
  public String updateUser(User user,
      @SessionAttribute("USER") User sessionUser,
      RedirectAttributes attributes) {
    try {
      user.setUserId(sessionUser.getUserId());
      userDao.updateUser(user);
      // 개인정보 수정 후에 세션 업데이트
      sessionUser.setEmail(user.getEmail());
      sessionUser.setName(user.getName());
      return "springmvc/v2/user/s/myInfo";
    } catch (DuplicateKeyException e) {
      attributes.addFlashAttribute("user", user);
      attributes.addFlashAttribute("msg", "Duplicate email");
      return "redirect:/app/springmvc/v2/user/s/userEdit";
    }
  }

  /**
   * 비밀번호 수정 액션
   */
  @PostMapping("/s/updatePassword")
  public String updatePassword(String password, String newPassword,
      @SessionAttribute("USER") User user, RedirectAttributes attributes) {
    int result =
        userDao.updatePassword(user.getUserId(), password, newPassword);
    if (result > 0) {
      return "springmvc/v2/user/s/myInfo";
    } else {
      attributes.addFlashAttribute("msg", "Wrong password");
      return "redirect:/app/springmvc/v2/user/s/passwordForm";
    }
  }

  /**
   * 로그 아웃
   */
  @GetMapping("/s/logout")
  public String logout(HttpSession session) {
    session.invalidate();
    return "redirect:/";
  }
}
