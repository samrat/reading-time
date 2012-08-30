var script = document.createElement('script');
script.src = 'http://code.jquery.com/jquery-1.8.0.min.js';
script.type = 'text/javascript';

$.getJSON("http://www.reading-time.samrat.me/api?url=" + location.href + "&callback=?",
	{},
	function(data) {
	  $("div.rt_readable").append(data.readable);
	  alert(data["readable"]);
	});
