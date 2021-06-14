package kr.mjc.jacob.web.springmvc.v2;

import kr.mjc.jacob.web.HttpUtils;
import kr.mjc.jacob.web.dao.Article;
import kr.mjc.jacob.web.dao.ArticleDao;
import kr.mjc.jacob.web.dao.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller("articleControllerV2")
@RequestMapping("/springmvc/v2/article")
@Slf4j
public class ArticleController {

  private final ArticleDao articleDao;

  @Autowired
  public ArticleController(ArticleDao articleDao) {
    this.articleDao = articleDao;
  }

  /**
   * 게시글 목록 화면
   */
  @GetMapping("/articleList")
  public void articleList(
      @RequestParam(required = false, defaultValue = "1") int page,
      @RequestParam(required = false, defaultValue = "20") int count,
      HttpServletRequest request, Model model) {

    int offset = (page - 1) * count;  // 목록의 시작 위치
    List<Article> articleList = articleDao.listArticles(offset, count);
    int totalCount = articleDao.countArticles();
    model.addAttribute("articleList", articleList);
    model.addAttribute("totalCount", totalCount);
    log.debug(HttpUtils.getRequestURLWithQueryString(request));
    // 현재 리스트 페이지를 세션에 넣는다.
    request.getSession().setAttribute("listPage",
        HttpUtils.getRequestURLWithQueryString(request));
  }

  /**
   * 특정 사용자가 쓴 게시글
   */
  @GetMapping("/userArticles")
  public String userArticles(
      @RequestParam(required = false, defaultValue = "1") int page,
      @RequestParam(required = false, defaultValue = "20") int count,
      @RequestParam int userId, HttpServletRequest request, Model model) {

    int offset = (page - 1) * count;  // 목록의 시작 위치
    List<Article> articleList =
        articleDao.userArticles(userId, offset, count);
    int totalCount = articleDao.countUserArticles(userId);
    model.addAttribute("articleList", articleList);
    model.addAttribute("totalCount", totalCount);
    log.debug(HttpUtils.getRequestURLWithQueryString(request));
    // 현재 리스트 페이지를 세션에 넣는다.
    request.getSession().setAttribute("listPage",
        HttpUtils.getRequestURLWithQueryString(request));
    return "springmvc/v2/article/articleList";
  }

  /**
   * 게시글 보기 화면
   */
  @GetMapping("/articleView")
  public void articleView(int articleId, @RequestHeader String referer,
      HttpSession session, Model model) {
    session.setAttribute("referer", referer);
    Article article = articleDao.getArticle(articleId);
    model.addAttribute("article", article);
    // forward /WEB-INF/springmvc/v2/article/articleView.jsp
  }

  /**
   * 게시글 수정 화면
   */
  @GetMapping("/s/articleEdit")
  public String articleEdit(@RequestParam int articleId,
      @SessionAttribute("USER") User user, Model model) {
    // 자기글 체크
    Article article = articleDao.getArticle(articleId);
    if (user.getUserId() != article.getUserId()) // 사용자가 다르면 401 Unauthorized
      return "error/401";

    model.addAttribute("article", article);
    return "springmvc/v2/article/s/articleEdit";
  }

  /**
   * 게시글 등록 액션
   */
  @PostMapping("/s/addArticle")
  public String addArticle(@ModelAttribute Article article,
      @SessionAttribute("USER") User user) {
    // 세션의 user로 사용자 정보를 설정한다.
    article.setUserId(user.getUserId());
    article.setName(user.getName());

    articleDao.addArticle(article);

    // 등록 후에 목록의 1페이지로 간다.
    return "redirect:/app/springmvc/v2/article/articleList?page=1";
  }

  /**
   * 게시글 수정 액션
   */
  @PostMapping("/s/updateArticle")
  public String updateArticle(@ModelAttribute Article article,
      @SessionAttribute("USER") User user) {
    article.setUserId(user.getUserId());

    int result = articleDao.updateArticle(article);
    if (result == 0)  // 사용자가 다르면 수정이 안됨. 400 Bad Request
      return "error/400";

    return "redirect:/app/springmvc/v2/article/articleView?articleId=" +
        article.getArticleId();
  }

  /**
   * 게시글 삭제 액션
   */
  @GetMapping("/s/deleteArticle")
  public String deleteArticle(int articleId,
      @SessionAttribute("USER") User user,
      @SessionAttribute("listPage") String listPage) {
    int result = articleDao.deleteArticle(articleId, user.getUserId());
    if (result == 0) {// 사용자가 다르면 삭제가 안됨. 401 Unauthorized
      return "error/401";
    }

    // 삭제 후에 목록의 원래 페이지로 간다.
    return "redirect:" + listPage;
  }
}
