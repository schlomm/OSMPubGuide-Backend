// source: http://www.javascriptkit.com/dhtmltutors/ajaxgetpost.shtml

function ajaxRequest() {
	var activexmodes = ["Msxml2.XMLHTTP", "Microsoft.XMLHTTP"]//activeX versions to check for in IE
	if (window.ActiveXObject) { //Test for support for ActiveXObject in IE first (as XMLHttpRequest in IE7 is broken)
		for (var i = 0; i < activexmodes.length; i++) {
			try {
				return new ActiveXObject(activexmodes[i])
			} catch (e) {
				//suppress error
			}
		}
	} else if (window.XMLHttpRequest) // if Mozilla, Safari etc
		return new XMLHttpRequest()else
			return false
}

// the actual request
function request_ajax() {
	var mygetrequest = new ajaxRequest()

		if (mygetrequest.overrideMimeType)
			mygetrequest.overrideMimeType('text/xml')

			mygetrequest.onreadystatechange = function () {
				if (mygetrequest.readyState == 4) {
					if (mygetrequest.status == 200 || window.location.href.indexOf("http") == -1) {
						var xmldata = mygetrequest.responseXML //retrieve result as an XML object
							var stuff = xmldata.xml
							console.log(xmldata.xml)
							console.log("Success")
							alert("Works with ajax :)");
					} else {
						alert("Did not work with ajax :(");
					}
				}
			}

		mygetrequest.open("GET", "http://giv-openpubguide.uni-muenster.de:8080/de.ifgi.ohbpgiosm/rest/pubs/getpubswithinbbox?south=41.886288445510516&west=12.483901977539062&north=41.893700240146295&east=12.500102519989014", true)
		//mygetrequest.setRequestHeader('X-Requested-With', 'XMLHttpRequest')
		//mygetrequest.setRequestHeader('Access-Control-Allow-Origin', '*')
		//mygetrequest.setRequestHeader('Access-Control-Allow-Headers', 'Origin, X-Requested-With, Content-Type, Accept')
		mygetrequest.send(null)
}