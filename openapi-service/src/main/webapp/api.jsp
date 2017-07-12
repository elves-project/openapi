<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<p style="text-indent: 2em; margin-top: 30px;">
系统将在 <span id="time">5</span> 秒钟后自动跳转至Elves Docs，如果未能跳转，<a href="http://gy-games.gitbooks.io/elves" title="点击访问">请点击</a>。</p>
<script type="text/javascript">
    delayURL();
    function delayURL() {
        var delay = document.getElementById("time").innerHTML;
        var t = setTimeout("delayURL()", 1000);
        if (delay > 0) {
            delay--;
            document.getElementById("time").innerHTML = delay;
        } else {
            clearTimeout(t);
            window.location.href = "http://gy-games.gitbooks.io/elves";
        }
    }
</script>
</head>
</html>