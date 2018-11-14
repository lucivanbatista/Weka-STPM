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
	<hr>
	
	<div class="container">
		<div class="row">
			<div class="col-6">
				<center>
					<button type="button"><a href="/semantic">Enriquecimento Semântico</a></button>
				</center> 
			</div>
			
			<div class="col-6">
				<center>
					<button type="button"><a href="/data">Visualização dos Dados</a></button>
				</center> 
			</div>
		</div>
	</div>

	<c:import url="imports/rodape.jsp" />
	<script src="js/jquery.js"></script>
	<script src="js/bootstrap.js"></script>
</body>
</html>