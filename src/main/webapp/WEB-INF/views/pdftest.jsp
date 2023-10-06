<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Insert title here</title>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>

<script>
    $(function(){
        $pdfOpen = $('#pdfOpen'),
            $pdfOpen.click(function(){
                window.open('pdf');
            });
    })
</script>
<button id="pdfOpen">pdf열기</button>
</body>
</html>
