(ns reading-time.views.common
  (:use [noir.core :only [defpartial]]
        [hiccup.page :only [include-css html5 include-js]]
        [hiccup.element :only [link-to]]))

; Utils ; -----------------------------------------------------------------------
(defn include-less [href]
  [:link {:rel "stylesheet/less" :type "text/css" :href href}])

; Google Analytics -------------------------------------------------------------
(def ga "
  <script type=\"text/javascript\">

        var _gaq = _gaq || [];
        _gaq.push(['_setAccount', 'UA-34425817-1']);
        _gaq.push(['_setDomainName', 'reading-time.samrat.me']);
        _gaq.push(['_trackPageview']);

        (function() {
        var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
        ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
        var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
        })();

  </script>")

(def tweet "<a href=\"https://twitter.com/share\" class=\"twitter-share-button\" data-url=\"http://reading-time.samrat.me\" data-text=\"Find out how long you'll take it to read an article or add &quot;Reading time&quot; to your site!\" data-related=\"samratmansingh\">Tweet</a><script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0];if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=\"//platform.twitter.com/widgets.js\";fjs.parentNode.insertBefore(js,fjs);}}(document,\"script\",\"twitter-wjs\");</script>")

(def fb-like "<div id=\"fb-root\"></div>
<script>(function(d, s, id) {
  var js, fjs = d.getElementsByTagName(s)[0];
  if (d.getElementById(id)) return;
  js = d.createElement(s); js.id = id;
  js.src = \"//connect.facebook.net/en_US/all.js#xfbml=1\";
  fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));</script>")

(def fb-button "<div class=\"fb-like\" data-href=\"http://reading-time.herokuapp.com/\" data-send=\"false\" data-layout=\"button_count\" data-width=\"450\" data-show-faces=\"false\"></div>")

(defpartial template [& content]
  (html5
   [:head
    (map include-css ["/css/base.css"
                      "/css/skeleton.css"
                      "/css/layout.css"])
    (include-less "/css/style.less")
    (include-js "/js/less.js")
    [:title "Reading Time"]]
   [:body
    fb-like
    [:div.container.clearfix
     [:header.sixteen.columns ( link-to "/" [:h1 "Reading Time"] )
      [:h4 [:strong "Reading Time"] " estimates how long you'll take to read an online article. It has an " (link-to "/api?url=http://samrat.me/blog/2011/08/newsblur-an-awesome-alternative-to-google-reader" "API too") "(so you can put it on your blog!)"]
      tweet fb-button]
     [:div.sixteen.columns content]
     [:footer.sixteen.columns
      [:p
       "Made by "
       (link-to "http://samrat.me" "Samrat Man Singh")
       " from Nepal. It's "
       (link-to "https://github.com/samrat/reading-time" "open source")
       "."]
      ]]
    ga]))
