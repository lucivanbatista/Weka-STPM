function initMap() {
  var myLatLng = {lat: 39.9226134253132, lng: 116.293713255831};

  var map = new google.maps.Map(document.getElementById('map'), {
    zoom: 9,
    center: {lat: 39.9, lng: 116.40}
  });
  
  setMarkers(map);

  var marker = new google.maps.Marker({
    position: myLatLng,
    map: map,
    title: 'Hello World!'
  });
}

$("#generate-map").submit(function (event) {
});

var beaches = [
['Bondi Beach', -33.890542, 151.274856, 4],
['Coogee Beach', -33.923036, 151.259052, 5],
['Cronulla Beach', -34.028249, 151.157507, 3],
['Manly Beach', -33.80010128657071, 151.28747820854187, 2],
['Maroubra Beach', -33.950198, 151.259302, 1]
];

function setMarkers(map) {

for (var i = 0; i < beaches.length; i++) {
 var beach = beaches[i];
 var marker = new google.maps.Marker({
   position: {lat: beach[1], lng: beach[2]},
   map: map,
   title: beach[0],
   zIndex: beach[3]
 });
}
}