/**
 * reportDisease
 * 
 * get current location and time, get user id and send to the server
 */
function getAndSendLocation( ) {
	const resultField = document.querySelector('#myLocation');
	const url = "DiseaseReport";

	// check if geolocation is supported/enabled on current browser
	if ("geolocation" in navigator) { 
		// get location
		navigator.geolocation.getCurrentPosition(
				// when getting location is successful
				function success(position) {
					// note to show on the page
					resultField.innerHTML = `You are located at latitude: ${position.coords.latitude},  longitude: ${position.coords.longitude} and the timestamp is ${position.timestamp}`;
					// create parameters string to add to the url
					parametersString = `?latitude=${position.coords.latitude}&longitude=${position.coords.longitude}&timestamp=${position.timestamp}`;
					// send report
					fetch( url+parametersString ).then ( response => {
						if ( response.ok ) {
							alert('Your report was successfully sent');
						} else {
							alert('Failed to send report');
						}	   
					}, networkError => alert('Network Error'));
				},

				function error(error_message) {
					// when getting location results in an error
					console.error('An error has occured while retrieving location', error_message);
					resultField.innerHTML = `An error has occured while retrieving location: ${error_message}`;
				}

		)
	} else {
		// geolocation is not supported
		// get your location some other way
		console.log('geolocation is not enabled on this browser')
		alert('Geolocation is not enabled on this browser. Please enable the browser to send your location in order to report.');
	}
}
