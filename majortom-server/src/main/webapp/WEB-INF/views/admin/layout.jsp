<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<?xml version="1.0" encoding="utf-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xml:lang="en-US" xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <meta content="text/html; charset=UTF-8" http-equiv="Content-Type" />
    <title>MaJorToM - Server</title>
    <link href="/favicon.ico" rel="shortcut icon" />
    <link href="<%= getServletContext().getContextPath()%>/resources/css/screen.css" media="screen, projection" rel="stylesheet" type="text/css" />
    <link href="<%= getServletContext().getContextPath()%>/resources/css/print.css" media="print" rel="stylesheet" type="text/css" />
    <link href="<%= getServletContext().getContextPath()%>/resources/css/maiana.css" media="screen, projection" rel="stylesheet" type="text/css" />
    <!--[if lt IE 8]>
    <link href="stylesheets/blueprint/ie.css" media="screen, projection" rel="stylesheet" type="text/css" />
    <![endif]-->
  </head>
  <body class="maintenance">
    <div id="wrapper">
      <div id="header">
        <div class="container">
          <div class="span-20 prepend-2 append-2 last">
            <div id="logo">
              <h1>MaJorToM-Server</h1>
            </div>
          </div>
        </div>
      </div>
	    <div id="content">&nbsp;
			<jsp:include page="/WEB-INF/views/admin/${view}.jsp" />	        
	    </div>
	</div>
    <div id="footer">
      <div class="container">
        <div class="span-20 prepend-2">
          <ul class="horizontal">
            <li class="noBullet"><a href="<%= getServletContext().getContextPath()%>/about">About</a></li>
            <li class="noBullet"><a href="http://topicmapslab.de/impressum">Imprint</a></li>
            <li>MaJorToM-Server is a service by the <a href="http://www.topicmapslab.de">Topic Maps Lab</a> &copy; 2011</li>
          </ul>
        </div>
      </div>
    </div>
    <div>
    <a id="sideLabelLink" href="http://www.topicmapslab.de/"><span id="sideLabel">
     &nbsp;</span></a>
     </div> 
  </body>
</html>
