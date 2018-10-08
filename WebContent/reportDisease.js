function getCookie(cname) {
	var name = cname + "=";
	var decodedCookie = decodeURIComponent(document.cookie);
	var ca = decodedCookie.split(';');
	for(var i = 0; i <ca.length; i++) {
		var c = ca[i];
		while (c.charAt(0) == ' ') {
			c = c.substring(1);
		}
		if (c.indexOf(name) == 0) {
			return c.substring(name.length, c.length);
		}
	}
	return "";
}
    	
/**
 * reportDisease
 * 
 * get current location and time, get user id and send to the server
 */
function getAndSendLocation( ) {
	var my_location_div = $('#myLocation');
	//const resultField = document.querySelector('#myLocation');
	const url = "DiseaseReport";
	var userid = getCookie('user_id');
	
	if (!userid || userid == "") {
		// user need to log in first
	    document.location = 'login.html';
		return true;
	}

	// check if geolocation is supported/enabled on current browser
	if ("geolocation" in navigator) { 
		// get location
		navigator.geolocation.getCurrentPosition(
				// when getting location is successful
				function success(position) {
					// note to show on the page
					my_location_div.html(`LAT=${position.coords.latitude}, LONG=${position.coords.longitude}`);
					//resultField.innerHTML = `You are located at latitude: ${position.coords.latitude},  longitude: ${position.coords.longitude} and the timestamp is ${position.timestamp}`;
					// create parameters string to add to the url
					parametersString = `?latitude=${position.coords.latitude}&longitude=${position.coords.longitude}&timestamp=${position.timestamp}`;
					parametersString += "&farmerId=" + userid;
					// send report
					fetch( url+parametersString ).then ( response => {
						if ( response.ok ) {
							my_location_div.html(`המיקום דווח בהצלחה. LAT=${position.coords.latitude}, LONG=${position.coords.longitude}`);
							my_location_div.addClass('alert');
							my_location_div.addClass('alert-success');
							$('#the-big-blue-button button').text('תודה');
							$('#the-big-blue-button button').prepend('<span class="glyphicon glyphicon-ok"></span>');
						} else {
							alert('Failed to send report: ' + response.ok);
						}	   
					}, networkError => alert('Network Error'));
				},

				function error(err) {
					if (err.code == err.PERMISSION_DENIED) {
						my_location_div.html(`יש לאפשר הרשאות מיקום לעמוד זה`);
						my_location_div.addClass('alert');
						my_location_div.addClass('alert-warning');
					} else {
						// when getting location results in an error
						console.error(`An error has occured while retrieving location(${err.code}): ${err.message}`);
						//console.error('An error has occured while retrieving location', error_message.messakjsfd);
						my_location_div.html(`An error has occured while retrieving location: ${err.message}`);
						my_location_div.addClass('alert');
						my_location_div.addClass('alert-danger');
					}
				}

		)
	} else {
		// geolocation is not supported
		// get your location some other way
		console.log('geolocation is not enabled on this browser')
		alert('Geolocation is not enabled on this browser. Please enable the browser to send your location in order to report.');
	}
}

(function () {
	var userid = getCookie('user_id');
	if (!userid || userid == "") {
		// user need to log in first
	    document.location = 'login.html';
		return true;
	}
})();
