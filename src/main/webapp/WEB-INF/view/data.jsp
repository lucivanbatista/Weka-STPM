<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Weka-STPM - Enqieucimento Semântico</title>
	<meta name="viewport" content="width=device-width">
	<link rel="stylesheet" href="css/bootstrap-litera.min.css">
	<link rel="stylesheet" href="css/estilosemantic.css">
</head>
<body>
	<div class="container intro header">
		<h1>
			Enriquecimento Semântico</br>
			<small> Consiga enriquecer semanticamente sua base de dados</small>
		</h1>
	</div>
	
	<div class="container">
		<div class="panel panel-default">
			<div class="panel-body">
				<h3>Stops Identificados</h3>
			</div>
			
			<table class="table">
				<thead>
					<tr>
						<th>TID (Identificador do Objeto)</th>
						<th>Start Time (Início do Stop)</th>
						<th>End Time (Fim do Stop)</th>
						<th>GID (Identificador do Stop)</th>
						<th>Amenity (Tipo do Stop)</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${stops}" var="stop">
						<tr>
							<td>${stop.tid}</td>
							<td>${stop.enterTime}</td>
							<td>${stop.leaveTime}</td>
							<td>${stop.gid}</td>
							<td>${stop.amenity}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
	
	
	<script src="js/jquery.js"></script>
	<script src="js/bootstrap.js"></script>
</body>
</html>