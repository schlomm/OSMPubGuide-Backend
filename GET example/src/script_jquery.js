// the actual request
function request_jquery(url) {
	$.ajax({
		type : "GET",
		url : "http://giv-openpubguide.uni-muenster.de:8080/de.ifgi.ohbpgiosm/rest/pubs/getpubswithinbbox?south=41.886288445510516&west=12.483901977539062&north=41.893700240146295&east=12.500102519989014",
		dataType : "xml",
		success : function (data) {
			console.log("Successfully queried API!");
			console.log(data);

		},

		error : function (data) {
			console.log("An error occurred while processing XML file.");
		}
	});
};

//Code Ends