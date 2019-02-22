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
		<c:if test="${not empty msg}">
			<div class="alert alert-success alert-dismissible">
   				  <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
				  <strong><p>${msg}</p></strong> Enriquecimento Semântico Realizado com ${msg}!
				</div>
		</c:if>
		<form action="semanticstart" method="post" id="formsemantic">
			<div class="row">
				<div class="col-6">
					<center><h3>Configurações das Tabelas e do Banco</h3></center>
					<div class="form-group">
						<label class="col-form-label col-form-label-sm">Schema </label> <input
							type="text" class="form-control" id="schema" name="schema"
							aria-describedby="schemaHelpBlock" value="public" required autofocus>
						<small id="schemaHelpBlock" class="form-text text-muted">
  							Digite o schema do banco de dados
						</small>	
					</div>
					<div class="form-group">
						<label class="col-form-label col-form-label-sm">Trajectory
							Table </label> <input type="text" class="form-control"
							id="trajectoryTable" name="trajectoryTable"
							aria-describedby="trajectoryHelpBlock" required>
						<small id="trajectoryHelpBlock" class="form-text text-muted">
  							Digite a tabela que possui os pontos a serem utilizados
						</small>
					</div>
					<div class="row">
						<div class="col-6">
							<div class="form-group">
								<label class="col-form-label col-form-label-sm">TrajectoryId</label> 
								<input type="text" class="form-control"
									id="trajectoryId" name="trajectoryId" value="tid"
									placeholder="Digite o identificador do objeto" aria-describedby="trajectoryIDHelpBlock" 
									data-toggle="tooltip" title="Digite o identificador único para os objetos dos seus dados (Padrão é tid)" required>
							</div>
						</div>
						<div class="col-6">
							<div class="form-group">
								<label class="col-form-label col-form-label-sm">DetectionTime</label>
								<input type="text" class="form-control"
									id="detectionTime" name="detectionTime" value="time"
									placeholder="Digite o identificador do timestamp" aria-describedby="timeHelpBlock" 
									data-toggle="tooltip" title="Digite a coluna do tempo dos seus dados (Padrão é time)" required>
							</div>
						</div>
					</div>
					<div class="form-group">
						<label class="col-form-label col-form-label-sm">Points Of Interest</label>
						<input type="text" class="form-control"
							id="pointsInterest" name="pointsInterest" aria-describedby="poiHelpBlock" required>
						<small id="poiHelpBlock" class="form-text text-muted">
		  					Digite a tabela que possui os pontos de interesse
						</small>
					</div>
				</div>


				<div class="col-6">

					<center><h3>Configuração dos Algoritmos</h3></center>
					<div class="row">
						<div class="col-6">
							<div class="form-group">
								<label class="col-form-label col-form-label-sm">User
									Buff (metros)</label> <input type="number" class="form-control"
									id="userBuff" name="userBuff" aria-describedby="ubHelpBlock" value="150" 
									data-toggle="tooltip" title="Digite o valor em metros que representa o raio dos pontos de interesse" required>
							</div>
						</div>
						<div class="col-6">
							<div class="form-group">
								<label class="col-form-label col-form-label-sm">RF Min
									Time (s)</label> <input type="number" class="form-control"
									id="rfMinTime" name="rfMinTime" aria-describedby="rfHelpBlock" value="90"
									data-toggle="tooltip" title="Digite o valor em segundos que representa o tempo mínimo de permanência nos pontos de interesse" required>
							</div>
						</div>
					</div>
					<div class="form-group">
						<label class="col-form-label col-form-label-sm">Selecione
							o algoritmo de enriquecimento semântico</label> <select id="method"
							name="method">
							<option value="1">IB-SMoT</option>
							<option value="2">CB-SMoT</option>
						</select>
					</div>
					<div class="row align-items-end">
						<div class="col-4">
							<div class="form-group">
								<label class="col-form-label col-form-label-sm">MaxAvgSpeed
									</label> <input type="number" class="form-control"
									id="maxAvgSpeed" name="maxAvgSpeed" value="0.6"
									placeholder="Digite o MaxAvgSpeed">
							</div>
						</div>
						<div class="col-4">
							<div class="form-group">
								<label class="col-form-label col-form-label-sm">MinTime
									</label> <input type="number" class="form-control"
									id="minTime" name="minTime" value="40"
									placeholder="Digite o MinTime">
							</div>
						</div>
						<div class="col-4">
							<div class="form-group">
								<label class="col-form-label col-form-label-sm">MaxSpeed
									</label> <input type="number" class="form-control"
									id="maxSpeed" name="maxSpeed" value="1.5"
									placeholder="Digite o maxSpeed">
							</div>
						</div>
						
					</div>
					<div class="form-group">
						<label class="col-form-label col-form-label-sm">Nome da
							tabela a ser gerada</label> <input
							type="text" class="form-control" id="tableName"
							name="tableName" aria-describedby="tableHelpBlock" required>
						<small id="tableHelpBlock" class="form-text text-muted">
		  					Digite o nome desejado para a nova tabela, formato final: METODO_stops_NOME
						</small>
					</div>					
				</div>
			</div>			
			<center>
				<input type="submit" class="btn btn-primary btn-md"
					value="Executar o Enriquecimento Semântico">
			</center>
		</form>
		<hr>

	</div>

	<c:import url="imports/rodape.jsp" />
	<script src="js/jquery.js"></script>
	<script src="js/bootstrap.js"></script>
</body>
</html>