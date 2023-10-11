<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.2/dist/js/bootstrap.bundle.min.js"></script>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-Zenh87qX5JnK2Jl0vWa8Ck2rdkQ2Bzep5IDxbcnCeuOxjzrPF/et3URy9Bv1WTRi" crossorigin="anonymous">
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Join</title>
    <style>
        .bd-placeholder-img {
            font-size: 1.125rem;
            text-anchor: middle;
            -webkit-user-select: none;
            -moz-user-select: none;
            user-select: none;
        }

        @media (min-width: 768px) {
            .bd-placeholder-img-lg {
                font-size: 3.5rem;
            }
        }

        .b-example-divider {
            height: 3rem;
            background-color: rgba(0, 0, 0, .1);
            border: solid rgba(0, 0, 0, .15);
            border-width: 1px 0;
            box-shadow: inset 0 .5em 1.5em rgba(0, 0, 0, .1), inset 0 .125em .5em rgba(0, 0, 0, .15);
        }

        .b-example-vr {
            flex-shrink: 0;
            width: 1.5rem;
            height: 100vh;
        }

        .bi {
            vertical-align: -.125em;
            fill: currentColor;
        }

        .nav-scroller {
            position: relative;
            z-index: 2;
            height: 2.75rem;
            overflow-y: hidden;
        }

        .nav-scroller .nav {
            display: flex;
            flex-wrap: nowrap;
            padding-bottom: 1rem;
            margin-top: -1px;
            overflow-x: auto;
            text-align: center;
            white-space: nowrap;
            -webkit-overflow-scrolling: touch;
        }
        html,
        body {
            height: 100%;
        }

        body {
            display: flex;
            align-items: center;
            padding-top: 40px;
            padding-bottom: 40px;
            background-color: #f5f5f5;
        }

        .form-signin {
            max-width: 330px;
            padding: 15px;
        }

        .form-signin .form-floating:focus-within {
            z-index: 2;
        }

        .form-signin input[type="id"] {
            margin-bottom: -1px;
            border-bottom-right-radius: 0;
            border-bottom-left-radius: 0;
        }

        .form-signin input[type="password"] {
            margin-bottom: 10px;
            border-top-left-radius: 0;
            border-top-right-radius: 0;
        }
        .error-message {
            color: red;
            font-size: 14px;
            margin-top: 5px;
        }
        .success-message{
            color: green;
            font-size: 14px;
            margin-top: 5px;
        }

    </style>

</head>
<body class="text-center">
<main class="form-signin w-100 m-auto">
    <h1 class="h3 mb-3 fw-normal">회원가입</h1>
    <div class="form-floating mb-2">
        <input type="text" class="form-control" id="UserId" placeholder="ID" required>
        <label for="UserId">ID</label>
        <div class="invalid-feedback"> ID를 입력해주세요 </div>
    </div>
    <div class="form-floating">
        <input type="password" class="form-control" id="UserPw" placeholder="PW" required>
        <label for="UserPw">PW</label>
        <div class="invalid-feedback"> PW를 입력해주세요 </div>
    </div>
    <!-- 에러 메시지를 여기에 추가 -->
    <div class="error-message" id="errorMessage"></div>
    <div class="success-message" id="successMessage"></div>
    <button class="w-100 btn btn-lg btn-primary mt-5" id="signupButton">회원가입</button>
</main>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script>
    $(document).ready(function () {
        // 회원가입 버튼 클릭 이벤트 처리
        $("#signupButton").click(function () {
            $("#errorMessage").empty();
            var userId = $("#UserId").val();
            var userPw = $("#UserPw").val();

            var jsonData = {
                "UserId": userId,
                "UserPw": userPw
            };

            $.ajax({
                type: "POST",
                url: "api/user/signup",
                data: JSON.stringify(jsonData),
                contentType: "application/json",
                success: function (response) {
                    $("#successMessage").text(response);
                    setTimeout(function () {
                        window.location.href = "/login";
                    }, 1000); // 1초(1000 밀리초) 후에 리디렉션
                },
                error: function (xhr, textStatus, errorThrown) {
                    if (xhr.status === 400) {
                        // 잘못된 요청 (BAD_REQUEST)
                        $("#errorMessage").text(xhr.responseText);
                    } else {
                        // 서버 내부 오류 (INTERNAL_SERVER_ERROR) 등
                        $("#errorMessage").text(xhr.responseText);
                    }
                }
            });
        });
    });
</script>
</body>
