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

	<div class="d-flex w-100 h-100 p-3 mx-auto flex-column">      
		<main role="main" class="inner">
			<h1>Sobre</h1>
			<p class="lead">Esta aplicação é um dos resultados provenientes do Trabalho de Conclusão de Curso do aluno José Lucivan Batista Freires 
			realizado na Universidade Federal do Ceará no Campus Quixadá durante o ano de 2018.</p>
			<p class="lead">Ela executa algoritmos que detectam paradas e enriquece semanticamente trajetórias de objetos em movimento.</p>
		</main>
	</div>

	<c:import url="imports/rodape.jsp" />
	<script src="js/jquery.js"></script>
	<script src="js/bootstrap.js"></script>
</body>
</html>