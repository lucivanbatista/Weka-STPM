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
	
	<div class="container intro">
		<h1>
			Enriquecimento Semântico</br>
			<small> Consiga enriquecer semanticamente sua base de dados</small>
		</h1>
	</div>
	
	<div class="container">
		<fieldset class="field_semantic">
			<form action="semanticstart" method="post" id="formsemantic">
				<h3>Configurações das Tabelas e do Banco: </h3>
				<div class="form-group row">
					<label class="col-form-label col-form-label-sm">Schema </label>
					<input type="text" class="form-control" id="schema" name="schema" placeholder="Digite o schema das tabelas">
				</div>
				<div class="form-group row">
					<label class="col-form-label col-form-label-sm">Trajectory Table </label>
					<input type="text" class="form-control" id="trajectoryTable" name="trajectoryTable" placeholder="Digite sua tabela com os pontos do GPS" autofocus>
				</div>
				<div class="form-group row">
					<label class="col-form-label col-form-label-sm">TrajectoryId (Por padrão é tid)</label>
					<input type="text" class="form-control" id="trajectoryId" name="trajectoryId" value="tid" placeholder="Digite o identificador do objeto" required>
				</div>
				<div class="form-group row">
					<label class="col-form-label col-form-label-sm">DetectionTime (Por padrão é time)</label>
					<input type="text" class="form-control" id="detectionTime" name="detectionTime" value="time" placeholder="Digite o identificador do timestamp">
				</div>
				<div class="form-group row">
					<label class="col-form-label col-form-label-sm">Points Of Interest</label>
					<input type="text" class="form-control" id="pointsInterest" name="pointsInterest" placeholder="Digite a tabela dos pontos de interesse">
				</div>
								
				
				<h3>Configuração dos Algoritmos</h3>
				<div class="form-group row">
					<label class="col-form-label col-form-label-sm">User Buff (metros)</label>
					<input type="text" class="form-control" id="userBuff" name="userBuff" placeholder="Digite o raio do ponto de interesse">
				</div>
				<div class="form-group row">
					<label class="col-form-label col-form-label-sm">RF Min Time (s)</label>
					<input type="text" class="form-control" id="rfMinTime" name="rfMinTime" placeholder="Digite o tempo mínimo de permanência">
				</div>
				<div class="form-group row">
					<label class="col-form-label col-form-label-sm">Selecione o algoritmo de enriquecimento semântico</label>
					<select id="method" name="method">
						<option value="1">IB-SMoT</option>
						<option value="2">CB-SMoT</option>
					</select>
				</div>
				<div class="form-group row">
					<label class="col-form-label col-form-label-sm">MaxAvgSpeed (para o CB-SMoT)</label>
					<input type="text" class="form-control" id="maxAvgSpeed" name="maxAvgSpeed" value="0.9" placeholder="Digite o MaxAvgSpeed">
				</div>
				<div class="form-group row">
					<label class="col-form-label col-form-label-sm">MinTime (para o CB-SMoT)</label>
					<input type="text" class="form-control" id="minTime" name="minTime" value="60" placeholder="Digite o MinTime">
				</div>
				<div class="form-group row">
					<label class="col-form-label col-form-label-sm">MaxSpeed (para o CB-SMoT)</label>
					<input type="text" class="form-control" id="maxSpeed" name="maxSpeed" value="1.1" placeholder="Digite o maxSpeed">
				</div>
						
				<div class="form-group row">
					<label class="col-form-label col-form-label-sm">Nome da tabela a ser gerada (Número do experimento ou semelhante)</label>
					<input type="text" class="form-control" id="tableName" name="tableName" placeholder="Digite o nome da tabela a ser gerada">
				</div>
				
				<input type="submit" class="btn btn-primary btn-md" value="Executar o Enriquecimento Semântico"></button>
			</form>
		</fieldset>
	</div>	
	
	<script src="js/jquery.js"></script>
	<script src="js/bootstrap.js"></script>
</body>
</html>