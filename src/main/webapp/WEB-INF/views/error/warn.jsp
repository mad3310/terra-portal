<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="java.util.*"%>
<%
	String errorMsg = (String)request.getAttribute("errorMsg");
	Integer state = (Integer)request.getAttribute("state");
	response.setContentType("application/x-javascript;charset=UTF-8");
	response.setStatus(state);
	out.clear();
	out.print(errorMsg);
	out.flush();
%>
