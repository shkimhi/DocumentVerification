<%--
  Created by IntelliJ IDEA.
  User: sh
  Date: 23. 9. 19.
  Time: 오후 1:17
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
    <title>Title</title>
    <style>
        /* 드래그 앤 드롭 영역의 스타일 */
        #drop-area {
            border: 2px dashed #ccc;
            border-radius: 8px;
            padding: 20px;
            text-align: center;
            cursor: pointer;
        }

        /* 드래그 앤 드롭 영역 위로 파일을 드래그했을 때 스타일 */
        #drop-area.dragover {
            background-color: #f0f0f0;
        }

        /* 업로드 아이콘 스타일 */
        #upload-icon {
            font-size: 48px;
            color: #ccc;
        }
    </style>
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
            <form class="d-flex me-2" action="/api/ca/enroll" method="POST">
                <button type="submit" class="btn btn-outline-success" id="submitButton">관리자 등록</button>
            </form>
            <form class="d-flex me-2" action="/api/ca/register" method="POST" class="g-col-2">
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
<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <div id="drop-area" class="text-center">
                <i id="upload-icon" class="fas fa-cloud-upload-alt"></i>
                <p>파일을 드래그 앤 드롭하세요<br>또는 클릭하여 파일 선택</p>
            </div>
            <input type="file" id="file-input" style="display: none;">
        </div>
    </div>
    <!-- 대기 화면 -->
    <div id="loading" class="text-center mt-5" style="display: none;">
        <div class="progress">
        <div class="progress-bar" id="progress-bar" role="progressbar" style="width: 0%;" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100">0%</div>
        </div>
        <p id="loading-text"></p>
    </div>

<!-- 실행 완료 화면 -->
    <div id="completed" class="text-center mt-5" style="display: none; color: green;">
        <p id="completed-text">성공적으로 업로드 되었습니다.</p>
    </div>
    <div id="error" class="text-center mt-5" style="display: none; color: red;">
        <p id="error-text">업로드에 실패하였습니다.</p>
    </div>

</div>
<script>
    $(document).ready(function () {


        $("#drop-area").click(function (e){
            e.preventDefault();
            $("#file-input").click();
        });

        // 파일 선택 또는 드래그 앤 드롭 이벤트 처리
        $("#file-input").change(function () {
            processFile($("#file-input")[0].files[0]);
        });

        $("#drop-area").on("dragover", function (e) {
            e.preventDefault();
            e.stopPropagation();
            $(this).addClass("dragover");
        });

        $("#drop-area").on("dragleave", function (e) {
            e.preventDefault();
            e.stopPropagation();
            $(this).removeClass("dragover");
        });

        $("#drop-area").on("drop", function (e) {
            e.preventDefault();
            e.stopPropagation();
            $(this).removeClass("dragover");
            var file = e.originalEvent.dataTransfer.files[0];
            processFile(file);
        });

        function uploadFile(file) {
            setTimeout(function () {
                updateLoadingText("파일 업로드 중...")
            }, 500);
            return new Promise((resolve, reject) => {
                var formData = new FormData();
                formData.append("file", file);

                $.ajax({
                    type: "POST",
                    url: "/api/sftp/upload",
                    data: formData,
                    processData: false,
                    contentType: false,
                    success: function (response) {
                        resolve(response);
                    },
                    error: function (xhr, textStatus, errorThrown) {
                        reject(xhr.responseText);
                    }
                });
            });
        }

        function convertToPdf(file) {
            // PDF 변환 로직
            updateLoadingText("pdf로 변환 중...")
            return new Promise((resolve, reject) => {
                var formData = new FormData();
                formData.append("file", file);

                $.ajax({
                    type: "POST",
                    url: "/api/sftp/convert",
                    data: formData,
                    processData: false,
                    contentType: false,
                    success: function (response) {
                        resolve(response);
                    },
                    error: function (xhr, textStatus, errorThrown) {
                        reject(xhr.responseText);
                    }
                });
            });

        }

        function createLedger(file){
            //트랜잭션 로직
            setTimeout(function () {
                updateLoadingText("원장에 기록 중...")
            }, 500);
            return new Promise((resolve, reject) => {
                var formData = new FormData();
                formData.append("file", file);

                $.ajax({
                    type: "POST",
                    url: "/api/ledger/create",
                    data: formData,
                    processData: false,
                    contentType: false,
                    success: function (response) {
                        resolve(response);
                    },
                    error: function (xhr, textStatus, errorThrown) {
                        reject(xhr.responseText);
                    }
                });
            });

        }

        async function processFile(file) {
            try {
                $("#loading").show();
                $("#completed").hide();
                $("#error").hide();
                updateProgressBar(1);

                const pdfConversionResponse = await convertToPdf(file);
                console.log("PDF 변환 성공:", pdfConversionResponse);
                updateLoadingText("PDF 변환 완료");
                updateProgressBar(50);

                const uploadResponse = await uploadFile(file);
                console.log("파일 업로드 성공:", uploadResponse);
                updateLoadingText("파일 업로드 완료");
                updateProgressBar(80);

                const createLedgerResponse = await createLedger(file);
                console.log("원장에 기록 성공:", createLedgerResponse);
                updateLoadingText("원장에 기록 완료");
                updateProgressBar(100);

                setTimeout(function (){
                    $("#loading").hide();
                    $("#completed").show();
                },1500)
            } catch (error) {
                console.error("작업 실패:", error);
                $("#loading").hide();
                $("#error").show();
            }
        }
        function updateLoadingText(text) {
            $("#loading-text").text(text);
        }
        async function updateProgressBar(targetPercentage) {
            const progressBar = document.getElementById("progress-bar");
            let currentPercentage = progressBar.getAttribute("aria-valuenow");
            currentPercentage = parseInt(currentPercentage);

            if (currentPercentage < targetPercentage) {
                for (let i = currentPercentage + 1; i <= targetPercentage; i++) {
                    progressBar.innerText=i+"%";
                    $("#progress-bar").css("width", i + "%");
                    $("#progress-bar").attr("aria-valuenow", i);
                    await sleep(50);
                }
            }
        }
        function sleep(ms) {
            return new Promise(resolve => setTimeout(resolve, ms));
        }

    });
</script>
</body>
</html>
