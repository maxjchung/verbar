<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ page trimDirectiveWhitespaces="true" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=5,8,9" >
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" href="css/stylesheet.css" type="text/css"/>
<title>Guided Legal Code Search</title>
</head>
<body>
<jsp:useBean id="model" type="com.example.model.SearchModel" scope="request" />
<!-- full page table -->
<form method="post">
<input type="hidden" name="currentstate" value="<% out.print( model.exchange.state ); %>" />
<input type="hidden" name="currentpath" value="<% out.print( model.exchange.path ); %>" />
<input type="hidden" name="currentterm" value="<% out.print( model.exchange.term ); %>" />
<input type="hidden" name="highlights" value="<% out.print( model.exchange.highlights?"true":"false" ); %>" />
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
			<input type="text" name="newterm" id="myInput" style="width: 145px;" class="style_4" value="<% out.print( model.exchange.term ); %>" />
			<br><br style="line-height: 8px" >
			<input type="submit" value="Search" style="width: 150px;" class="style_4" /><br>
			<hr>
			<input type="submit" name="browse" value="Browse" style="width: 150px;" class="style_4"/>
			<hr>
			<input type="submit" name="toggle" value="Toggle Highlights" style="width: 150px;" class="style_4"/>
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
			<td valign="top" align="left" width="11px" height="100%">&nbsp;</td>
			<td valign="top" align="left" width="100%">
			<!-- START CENTER DISPLAY -->
<% if ( model.exchange.state == 1 ) { %>
<table class="codestable" width="100%">
<%   for (int i=0, l=model.exchange.codesAvailable.size(); i<l; ++i) { 
       if (i % 3 == 0) { %>
<tr>
<%     } %>
<td><input type="checkbox" name="<%out.print(model.exchange.codesAvailable.get(i).fullFacet);%>" <%if (model.exchange.codesAvailable.get(i).selected) out.print("checked=\"checked\" ");%>/><span class="style_5"><% out.print(model.exchange.codesAvailable.get(i).title); %></span></td>
<%     if (i % 3 == 2) { %>
</tr>
<%     } %> 
<%  }%>
<%   if ( model.allSelected ) { %>
<td><input type="checkbox" name="unselectall" onchange="submit();"><span class="style_5"><b>UNSELECT ALL</b></span><input type="hidden" name="allselected" /></td>
<%   } else {%>
<td><input type="checkbox" name="selectall" onchange="submit();"/><span class="style_5"><b>SELECT ALL</b></span></td>
<%   }%>
</tr>
</table>
<% } else { // if note state == 1 then preserve the selectedCodes in hidden inputs
    if ( model.allSelected ) { // if select all, then simply save that %>
<input type="hidden" name="allselected" />
<%  } else { // else save each code-state individually
      for (int i=0, l=model.exchange.codesAvailable.size(); i<l; ++i) { 
        if (model.exchange.codesAvailable.get(i).selected) { %>
<input type="hidden" name="<%out.print(model.exchange.codesAvailable.get(i).fullFacet);%>" />
<%      } 
      } 
    } 
  } %>
<!-- Start view of states 2, 3, and 4 -->
<% if ( model.exchange.state == 2 || model.exchange.state == 3 || model.exchange.state == 4 || model.exchange.state == 5 ) { %>
<table class="titlelisttable" width="100%">
<% for (int i=0, l=model.exchange.selectedCodesList.size(); i<l; ++i) { %>
<tr align="left">
<td style="white-space: nowrap; text-align: right;" class="style_5">
<% if ( !model.exchange.term.isEmpty() ) { 
	out.print( "" + model.exchange.selectedCodesList.get(i).count + " IN ");
} else {
	out.print( "&nbsp;");
} %>
</td>
<td style="white-space: nowrap" >
<button name="newpath" value="<%out.print(model.exchange.selectedCodesList.get(i).fullFacet);%>" class="style_5">
<% out.print( model.exchange.selectedCodesList.get(i).title ); %>
</button>
</td>
<td align="left" style="white-space: nowrap" class="style_5"><% out.print( "[" + model.exchange.selectedCodesList.get(i).codeRange + "]" ); %></td>
<td align="left" width="100%" class="style_5"><% out.print( model.exchange.selectedCodesList.get(i).title ); %></td>
</tr>
<% } %>
<% for (int i=0, l=model.exchange.pathList.size(); i<l; ++i) { %>
<tr>
<td>&nbsp;</td>
<td style="white-space: nowrap" >
<% if ( i == 0 ) { %>
<button name="newpath" value="<%out.print(model.exchange.pathList.get(i).fullFacet);%>"  class="style_5">
<% out.print( model.exchange.pathList.get(i).title ); %>
</button>
<% } else if ( model.exchange.pathList.get(i).part == null ) { %>
<button name="newpath" value="<%out.print(model.exchange.pathList.get(i).fullFacet);%>"  class="style_5">
<% out.print( "[EMPTY]"); %>
</button>
<% } else {  %>
<button name="newpath" value="<%out.print(model.exchange.pathList.get(i).fullFacet);%>"  class="style_5">
<% out.print(model.exchange.pathList.get(i).part + "-" + model.exchange.pathList.get(i).partNumber ); %>
</button>
<% }  %>
</td>
<td align="left" style="white-space: nowrap" class="style_5">
<% out.print( "["  + model.exchange.pathList.get(i).codeRange + "]" ); %></td>
<td align="left" width="100%" class="style_5">
<% out.print( model.exchange.pathList.get(i).title ); %></td>
</tr>
<% } %>
</table>
<% } %>
<hr>
<table class="codelisttable">
<% for (int i=0, l=model.exchange.subcodeList.size(); i < l; ++i) { %>
<tr>
<td style="white-space: nowrap; text-align: right;" class="style_5">
<% if ( !model.exchange.term.isEmpty() ) { 
		out.print( "" + model.exchange.subcodeList.get(i).count + " IN ");
	} else {
		out.print( "&nbsp;");
	} %>
</td>
<td style="white-space: nowrap" >
<% if ( model.exchange.subcodeList.get(i).part == null ) { %>
<button name="newpath" value="<%out.print(model.exchange.subcodeList.get(i).fullFacet);%>" class="style_5">
<% out.print( "[EMPTY]" ); %>
</button>
<% } else {  %>
<button name="newpath" value="<%out.print(model.exchange.subcodeList.get(i).fullFacet);%>" class="style_5">
<% out.print( model.exchange.subcodeList.get(i).part + "&nbsp;" + model.exchange.subcodeList.get(i).partNumber); %>
</button>
<% } %>
</td>
<td align="left" style="white-space: nowrap" class="style_5"><% out.print( "["  + model.exchange.subcodeList.get(i).codeRange + "]" ); %></td>
<td align="left" width="100%" class="style_5"><% out.print( model.exchange.subcodeList.get(i).title ); %></td>
</tr>
<% } // end of state 2, 3, or 4 %>
</table>
<% if ( model.exchange.state == 5 || model.exchange.highlights )  { // start of state == 5 %>
<pre ><span class="sectiontext"><% 	for (int i=0,l=model.exchange.sectionTextList.size(); i<l; ++i) {
	out.print( model.highlightText(model.exchange.sectionTextList.get(i).getText(), model.exchange.term, "<span class=\"termtext\">", "</span>") + "<br><br>" );
	} %></span>
</pre>
<% } //  end of state == 5 %>
			<!-- END CENTER DISPLAY -->
			</td>
			<td valign="top" align="right" width="11px" height="100%">&nbsp;</td>
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