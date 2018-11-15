<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" href="css/bootstrap-litera.min.css">
<link rel="stylesheet" href="css/estilosemantic.css">
<title>Weka-STPM</title>
<style>
      /* Always set the map height explicitly to define the size of the div
       * element that contains the map. */
      #map {
        height: 100%;
      }
      /* Optional: Makes the sample page fill the window. */
      html, body {
        height: 80%;
        margin: 10px;
        padding: 0;
      }
    </style>
</head>
<body>
    <div id="map"></div>
    
    <c:import url="imports/rodape.jsp" />
    <script src="js/jquery.js"></script>
    <script src="js/map.js"></script>
    <script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyBaQ4ELIUByX3CICs58xkieMYK5q3_wMuI&callback=initMap" async defer></script>
  </body>
</html>