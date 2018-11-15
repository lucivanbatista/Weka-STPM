<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Weka-STPM - Enriquecimento Semântico</title>
<meta name="viewport" content="width=device-width">
<link rel="stylesheet" href="css/bootstrap-litera.min.css">
<link rel="stylesheet" href="css/estilosemantic.css">
</head>
<body>
	<div class="container intro header">
		<h1>
			Enriquecimento Semântico</br> <small> Consiga enriquecer
				semanticamente sua base de dados</small>
		</h1>
	</div>
	
	<div class="container">
		<form method="post" action="getstops">
			<select id="tableName" name="tableName">
				<c:forEach items="${tabelas}" var="tabela">
					<option value="${tabela.nome}">${tabela.nome}</option>
				</c:forEach>
			</select> <select id="limit" name="limit">
				<option value="10">10</option>
				<option value="20">20</option>
				<option value="50">50</option>
				<option value="100">100</option>
				<option value="0">ALL</option>
			</select> <input type="submit" class="btn btn-primary btn-md" value="Filtrar">
		</form>
		
		<form method="post" action="/map" id="generate-map">
		    <c:if test="${empty tabelas}">
				<input type="submit" class="btn btn-primary btn-md" value="Gerar Mapa">
			</c:if>
		</form>
	</div>		

	<div class="container">
		<div class="panel-body">
			<h3>Pontos e seus Stops Identificados da tabela ${tableName}</h3>
		</div>
		<table class="table">
			<thead>
				<tr>
					<th>Stop ID</th>
					<th>Point ID</th>
					<th>ID do Objeto</th>
					<th>Lat</th>
					<th>Lon</th>
					<th>Time</th>
					<th>Edge</th>
					<th>POI ID</th>
					<th>Start Time Stop</th>
					<th>End Time Stop</th>
					<th>Amenity</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${stops}" var="stop">
					<tr>
						<td>${stop.gid_stop}</td>
						<td>${stop.gid}</td>
						<td>${stop.tid}</td>
						<td>${stop.latitude}</td>
						<td>${stop.longitude}</td>
						<td>${stop.time}</td>
						<td>${stop.edge_id}</td>
						<td>${stop.stop_gid}</td>
						<td>${stop.startTime}</td>
						<td>${stop.endTime}</td>
						<td>${stop.rf}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>

	<c:import url="imports/rodape.jsp" />
	<script src="js/jquery.js"></script>
    <script src="js/map.js"></script>
	<script src="js/bootstrap.js"></script>
</body>
</html>