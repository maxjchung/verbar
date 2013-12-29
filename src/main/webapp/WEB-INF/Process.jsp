<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ page trimDirectiveWhitespaces="true" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=5,8,9" >
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" href="css/stylesheet.css" type="text/css"/>
<title>Statutory Code Search and Browse</title>
</head>
<body>
<jsp:useBean id="model" type="java.lang.Long" scope="request" />
<jsp:useBean id="message" type="java.lang.String" scope="request" />
<!-- full page table -->
<form id="formsId" method="post">
<table width="100%" bgcolor="black" cellpadding="0" cellspacing="0" border="0" height="100%" >
<tr height="40px">
	<td valign="bottom" align="center" height="100%" width="150px" bgcolor="#EFE4B0">
		<span style="padding-bottom: 0pt; padding-top: 0pt;" class="paragraph_style_2">Created through<br>Association with</span>
	</td>
	<td valign="bottom" align="center" height="100%" width="100%" bgcolor="#EFE4B0">
		<div id="header">
			<span style="line-height: 27.9775px; font-size: 24.7px;" class="paragraph_style_1">Statutory Code Search and Browse</span>
		</div>
	</td>
	<td valign="bottom" align="center" height="100%" width="200px" bgcolor="#EFE4B0">
		<span style="padding-bottom: 0pt; padding-top: 0pt;" class="paragraph_style_1">&nbsp;</span>
	</td>
</tr>
<!-- new row .. three columns menu, page, ads -->
<tr height="100%">
	<td valign="top" align="left" height="100%" width="150px" bgcolor="white">
		<table style="background: #ffffff url(img/bg2.jpg) repeat-x; width: 150px; height: 500px;" cellpadding="0" cellspacing="0" border="0" >
		<tr valign="top" >
		<td valign="top" align="center" >
			<hr>
			<span class="paragraph_style_1">&nbsp;</span><br>
			<span style="padding-bottom: 0pt; padding-top: 0pt;" class="paragraph_style_2">&nbsp;</span>
			<hr>
			<span class="paragraph_style_1">&nbsp;</span><br>
			<span style="padding-bottom: 0pt; padding-top: 0pt;" class="paragraph_style_2">&nbsp;</span>
			<br>
			<hr>
		</td>
		</tr>
		</table>
	</td>
	<td valign="top" align="center" height="100%" width="100%" bgcolor="white">
		<!-- center display table -->
		<table width="100%" bgcolor="white" cellpadding="0" cellspacing="0" border="0" height="100%" >
		<tr>
		<td valign="top" height="11px">
			<table width="100%" bgcolor="white" cellpadding="0" cellspacing="0" border="0" >
			<tr valign="top"><td valign="top" align="left" width="11px"><img align="left" src="img/upleft.jpg"></td><td width="100%">&nbsp;</td><td valign="top" align="right" width="11px"><img align="right" src="img/upright.jpg"></td>
			</tr>
			</table>
		</td>
		</tr>
		<tr>
		<td valign="top" height="100%" width="100%" >
			<table width="100%" bgcolor="white" cellpadding="0" cellspacing="0" border="0" >
			<tr valign="top">
			<td valign="top" align="left" width="11px"/>
			<td valign="top" align="left" width="100%">
			<!-- START CENTER DISPLAY -->
			<table><tr><td width="150px">&nbsp;</td><td>
			<br><br><br>The program is 
<% if ( model.intValue() == 0 ) {
		out.print("NOT processing"); 
	} else if ( model.intValue() == 100 ) {
		out.print("DONE PROCESSING");
	} else  {
		out.print("PROCESSING and is " + model + "% done"); 
	}
%>
<br>
<% out.print( message ); %>
<br>
			<button>Refresh</button><br><br>
			Input Codes Directory: <input type="text" name="codesdir" />&nbsp;<button name="start">START PROCESSING CODES DIRECTORY</button><br><br>
			<button name="stop">STOP PROCESSING CODES (Not Implemented)</button><br><br>
			<button name="search">BACK TO SEARCH</button>
</td></tr></table>
			<!-- END CENTER DISPLAY -->
			</td>
			<td valign="top" align="right" width="11px"/>
			</tr>
			</table>
		</td>
		</tr>
		</table>
	</td>
	<td valign="top" align="right" height="100%" width="200px" bgcolor="white">
		<!-- right ad display table -->
		<!-- right ad display table -->
		<table style="background: #ffffff url(img/bg2.jpg) repeat-x; width: 200px; height: 500px;" cellpadding="0" cellspacing="0" border="0" >
		<tr height="100%">
		<td valign="top" height="100%" align="center">&nbsp;
		</td>
		</tr>
		</table>
	</td>
</tr>
</table>
</form>
</body>
</html>