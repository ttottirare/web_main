<%@ page import="kr.mjc.jacob.web.dao.User" %>
<nav><span style="font-weight: bold;">[MVC]</span> <a href="./">홈</a> <a
    href="./mvc/user/userList">사용자</a>
  <a href="./mvc/article/articleList">게시글</a>
  <% User sessionUser = (User) session.getAttribute("USER");
    if (sessionUser != null) { // 로그인을 했으면 %>
  <a href="./mvc/user/userInfo"><%= sessionUser.getName()%>
  </a>님 <a href="./mvc/user/logout">로그아웃</a>
  <% } else { // 로그인 안했으면 %>
  <a href="./mvc/user/loginForm">로그인</a> <a
      href="./mvc/user/joinForm">회원가입</a>
  <%}%>
</nav>