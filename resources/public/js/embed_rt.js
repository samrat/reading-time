$.getJSON("http://www.reading-time.samrat.me/api?url=" + location.href + "&callback=?",
	{},
	function(data) {
	  $("div.rt_readable").append("Reading Time: ");
	  $("div.rt_readable").append(data.readable);
	});
