<%--
  Created by IntelliJ IDEA.
  User: sh
  Date: 23. 10. 11.
  Time: 오후 4:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.8.2/css/all.min.css"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-kenU1KFdBIe4zVF0s0G1M5b4hcpxyD9F7jL+jjXkk+Q2h455rYXK/7HAuoJl+0I4"
        crossorigin="anonymous"></script>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet"
      integrity="sha384-rbsA2VBKQhggwzxH7pPCaAqO46MgnOM80zW1RWuH61DGLwZJEdK2Kadq2F9CUG65" crossorigin="anonymous">
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<head>
    <title>MyPage</title>
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
                <form class="d-flex me-2" action="/mypage" method="GET" class="g-col-2">
                <button type="submit" class="btn btn-outline-success" id="ViddingButton">마이페이지</button>
            </form>
            <form class="d-flex me-2 " action="/logout" method="post">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <button type="submit" class="btn btn-outline-success">로그아웃</button>
            </form>
        </div>
    </div>
</nav>
<div class="container mt-5">
    <button id="loadDataButton" class="btn btn-primary m-5">데이터 불러오기</button>
    <div class="row row-cols-1 row-cols-md-2 g-4" style="border: 1px solid #ccc; min-height: 220px;">
<%--
        <div class="col">
            <div class="card text-bg-secondary mb-3" style="max-width: 18rem;">
                <div class="card-header">Header</div>
                <div class="card-body">
                    <h5 class="card-title">Secondary card title</h5>
                    <p class="card-text">Some quick example text to build on the card title and make up the bulk of the card's content.</p>
                </div>
            </div>
        </div>
--%>

    </div>
</div>

<script>
    $(document).ready(function() {
        // JSON 데이터를 가져오는 AJAX 요청
        $('#loadDataButton').click(function() {
            $.ajax({
            url: '/api/ledger/queryuser', // API 엔드포인트 URL을 여기에 입력
            method: 'POST', // 또는 필요한 HTTP 메서드 사용
            success: function(data) {
                console.log(data);
                // JSON 데이터를 파싱하고 각 데이터 항목을 처리
                $.each(data, function(index, item) {
                    console.log(index);
                    var cardHtml = ' <div class="col">'
                        + '<div class="card text-bg-secondary mb-3" style="max-width: 30rem;">'
                        + '<div class="card-header">' + item.record.filename +'</div>'
                        + '<div class="card-body">'
                        + '<h5 class="card-title">' + item.key + '</h5>'
                        + '<p class="card-text">'
                        + '<strong>Username:</strong>' + item.record.username + '<br>'
                        + '<strong>File Hash:</strong>' + item.record.filehash + '<br>'
                        + '<strong>File Date:</strong>' + item.record.filedate + '</p>'
                    +'</div>'
                +'</div>'
            +'</div>';
                    // 카드를 동적으로 생성하고 추가
                    $('.row.row-cols-1.row-cols-md-2.g-4').append(cardHtml);
                });
            },
            error: function(xhr) {
                $(".row.row-cols-1.row-cols-md-2.g-4").text(xhr.responseText);
                console.log('데이터를 가져오는 중 오류가 발생했습니다.'+ xhr.responseText);
            }
        });
        });
    });
</script>
</body>
</html>
