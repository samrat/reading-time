(ns reading-time.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [clojure.data.json :as json]
            [ring.middleware.params :refer [wrap-params]]
            [org.httpkit.server :refer [run-server]]
            [reading-time.templates :as rtt]
            [reading-time.extract :as rte]

            [hiccup.core :refer :all]
            [hiccup.form :refer :all]))


(defroutes app
  (GET "/" {query-params :query-params}
       (let [url (get query-params "url")]
         (when url
           (let [{:keys [title reading-time error]} (rte/get-info url)]
             (rtt/template
              (when (= error :url-fetch-failed)
                [:div [:h4 "Error fetching URL: " [:a {:href url} url]]
                 [:br]])
              (if-not reading-time
                [:h4 "Give it a try. Put in an article URL below."]
                [:h4 "Article URL:"] )
              (form-to [:get "/"]
                       (text-field "url")
                       (submit-button "Submit"))
              (when reading-time
                [:h4
                 [:a {:href url} title]
                 " will take approximately "
                 [:strong [:u reading-time]] " to read."]))))))
  (GET "/" [] (rtt/template
               [:h4 "Give it a try. Put in an article URL below."]
               (form-to [:get "/"]
                        (text-field "url")
                        (submit-button "Submit"))))

  (GET "/api" {query-params :query-params}
       (let [{:strs [url callback]} query-params]
         (if url
           (let [{:keys [title reading-time mins-to-read]} (rte/get-info url)]
             (if callback
               {:status 200
                :headers {"Content-Type" "text/javascript"}
                :body (str callback "(" (json/write-str {:title title
                                                         :url url
                                                         :minutes mins-to-read
                                                         :readable reading-time}) ")")}
               {:status 200
                :headers {"Content-Type" "application/json"}
                :body (json/write-str {:title title
                                       :url url
                                       :minutes mins-to-read
                                       :readable reading-time})}))

           (rtt/template
            [:h2 "API docs"]
            [:p
             "To use the API send a " [:code "GET request"] " to " [:code "/api"] " with a `url` parameter."
             ]

            [:p [:strong "Example:"] [:br]
             [:code "$ curl http://reading-time.smingh.org/api?url=http://swizec.com/blog/services-i-want-to-pay-for/swizec/5158"]
             [:br]
             [:code "{'title':'A geek with a hat » Services I want to pay for','url':'http://swizec.com/blog/services-i-want-to-pay-for/swizec/5158','minutes':1.7799999713897705,'readable':'1 minutes, 46 seconds'}"]
             [:br] [:br]
             "You can also send a callback function as a parameter:" [:br]
             [:code "$ curl http://reading-time.smingh.org/api?url=http://swizec.com/blog/services-i-want-to-pay-for/swizec/5158&callback=?"]
             [:br]
             [:code "?({'title':'A geek with a hat » Services I want to pay for','url':'http://swizec.com/blog/services-i-want-to-pay-for/swizec/5158','minutes':1.7799999713897705,'readable':'1 minutes, 46 seconds'})"]]))))

  (route/resources "/"))

(defn -main [& m]
  (let [port (Integer. (get (System/getenv) "PORT" "9000"))]
    (run-server (wrap-params #'app) {:port port})))
