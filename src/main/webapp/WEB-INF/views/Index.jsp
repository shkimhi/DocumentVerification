<%--
  Created by IntelliJ IDEA.
  User: sh
  Date: 23. 9. 19.
  Time: 오후 1:17
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-kenU1KFdBIe4zVF0s0G1M5b4hcpxyD9F7jL+jjXkk+Q2h455rYXK/7HAuoJl+0I4"
        crossorigin="anonymous"></script>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet"
      integrity="sha384-rbsA2VBKQhggwzxH7pPCaAqO46MgnOM80zW1RWuH61DGLwZJEdK2Kadq2F9CUG65" crossorigin="anonymous">
<head>
    <title>Title</title>
</head>
<body>
<nav class="navbar navbar-expand-lg bg-light">
    <div class="container-fluid">
        <a class="navbar-brand" href="/">문서 검증 서비스</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent"
                aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
            </ul>
            <form class="d-flex me-2" action="/api/ca/enroll" method="GET">
                <button type="submit" class="btn btn-outline-success" id="submitButton">관리자 등록</button>
            </form>
            <form class="d-flex me-2" action="/api/ca/register" method="GET" class="g-col-2">
                <button type="submit" class="btn btn-outline-success" id="submitButton2">유저 가입</button>
            </form>

            <form class="d-flex me-2" action="/" method="GET" class="g-col-2">
                <button type="submit" class="btn btn-outline-success" id="ViddingButton">마이페이지</button>
            </form>
            <form class="d-flex me-2 " action="/logout" method="post">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <button type="submit" class="btn btn-outline-success">로그아웃</button>
            </form>

        </div>
    </div>
</nav>
index page
</body>
</html>
