$.getJSON("http://www.reading-time.samrat.me/api?url=" + location.href + "&callback=?",
	{},
	function(data) {
	  $("div.rt_readable").append(data.readable);
	  alert(data["readable"]);
	});
