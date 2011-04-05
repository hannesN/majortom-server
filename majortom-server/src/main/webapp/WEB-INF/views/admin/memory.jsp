<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<h1>Memory usage</h1>

<%@ page import="java.lang.management.*, java.util.*" %><%  
Iterator<?> iter = ManagementFactory.getMemoryPoolMXBeans().iterator();  
while(iter.hasNext()){  
    MemoryPoolMXBean item = (MemoryPoolMXBean) iter.next();  
      MemoryUsage mu = item.getUsage();  
      long used      = mu.getUsed();  
      long committed = mu.getCommitted();  
      long max       = mu.getMax();  
      %>
<p>  
MEMORY TYPE: <%=item.getName()%><br/>  
Used:        <%=used%>  <br/>
Committed:   <%=used%>  <br/>
Max:         <%=max%>  <br/>

<%}%>
</p>

<form action="garbagecollect" method="post">
	<input type="submit" value="Collect Garbage" />
</form>
