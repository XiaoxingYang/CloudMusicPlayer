<%@ page contentType="text/html; charset=gb2312" language="java" import="java.sql.*" errorPage="" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<script language="javascript">
<!--
	function checkParameter1(){
		if((form1.select.value=="createplaylist")&&(form1.textfield.value=="")){
			window.alert("please input playlist name");
			return false;
		}else if((form1.select.value=="deleteplaylist")&&(form1.textfield.value=="")){
			window.alert("please input playlist name");
			return false;
		}else if((form1.select.value=="deletemusic")&&(form1.textfield.value=="")){
			window.alert("please input music name");
			return false;
		}else if((form1.select.value=="fetchmusiclists")&&(form1.textfield.value=="")){
			window.alert("please input playlist name");
			return false;
		}
		else{
			return true;
		}
	} 
	function checkParameter2(){
		if((form2.bucketname.value=="")||(form2.musicname.value=="")){
			window.alert("not enough parameters");
			return false;
		}
		else{
			return true;
		}
	}
 -->
</script>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />

</script>
<title>CloudMusicPlayer</title>
<style type="text/css">
<!--
body,td,th {
	color: #FFF;
	font-family: Times New Roman, Times, serif;
	font-size: 16px;
}
body {
	background-color: #666666;
	margin-left: 15px;
}
-->
</style></head>

<body style="background:url(bg.jpg);">
<form id="form1" name="form1" method="post" action="MusicManagementService">
  <div align="left">
    <p align="right"> Xiaoxing Yang</p>
    <h1><strong>CloudMusicPlayer 
    </strong></h1>
    <p>
      <select name="select">
        <option value="fetchplaylists">Show Playlist </option>
        <option value="createplaylist">Create Playlist</option>
        <option value="deleteplaylist">Delete Playlist</option>
        <option value="deletemusic">Delete Music</option>
        <option value="fetchmusiclists">Show MusicList</option>
      </select>
      <input type="submit" name="Submit" value="submit" onclick="return checkParameter1();" />
    </p>
  </div><p align="left"> 
    <input type="text" name="textfield" value="" /> 
  </p>
<div align="left">
  <p>result: <%=request.getAttribute("result") %><br />
  </p>
</div><div align="left"></div>
</form>
<form id="form2" name="form2" method="get" action="MusicStreamServer">
  <input type="text" name="bucketname" value="playlist" />
<p align="left">
  <input type="text" name="musicname" value="music" />
</p>
<p align="left">
  <input type="submit"  value="playmusic" onclick="return checkParameter2();"/>
  <p>result: <%=request.getAttribute("result2") %><br />
</form>




</body>
</html>