package kr.mjc.jacob.web.springmvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class ExampleController {

  /**
   * Servlet API를 사용한 메서드
   */
  @GetMapping("/springmvc/hi")
  public void hi(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    request.getRequestDispatcher("/WEB-INF/jsp/springmvc/hi.jsp")
        .forward(request, response);
  }

  /**
   * Servlet API를 사용하지 않음. return type이 String
   *
   * @return
   */
  @GetMapping("/springmvc/hello")
  public String hello() {
    // prefix + view 이름 + suffix로 forward
    // forward /WEB-INF/jsp/springmvc/hello.jsp
    return "springmvc/hello"; // view 이름
  }

  /**
   * Servlet API를 사용하지 않음. return type이 void
   *
   * @return
   */
  @GetMapping("/springmvc/welcome")
  public void welcome() {
    // return type이 void일 때 prefix + 기본 view 이름 + suffix로 forward
    // 기본 view 이름은 요청 URL에서 contextPath/ 뒷 부분
    // 요청 URL - http://localhost:8080/springmvc/welcome
    // 기본 view 이름 - springmvc/welcome
    // forward - /WEB-INF/jsp/springmvc/welcome.jsp
  }
}
