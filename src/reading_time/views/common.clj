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
        _gaq.push(['_setAccount', 'UA-15328874-7']);
        _gaq.push(['_trackPageview']);

        (function() {
        var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
        ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
        var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
        })();

  </script>")

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
    [:div.container.clearfix
     [:header.sixteen.columns ( link-to "/" [:h1 "Reading Time"] )
      [:h4 [:strong "Reading Time"] " estimates how long you'll take to read an online article. It has an " (link-to "/api?url=http://samrat.me/blog/2011/08/newsblur-an-awesome-alternative-to-google-reader" "API too") "(so you can put it on your blog!)"]]
     [:div.sixteen.columns content]
     [:footer.sixteen.columns
      [:p
       "Made by "
       (link-to "http://samrat.me" "Samrat Man Singh")
       " from Nepal."]
      ]]
    ga]))
