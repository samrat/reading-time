(ns reading-time.views.app
  (:require [reading-time.views.common :as common]
            [noir.response :as resp]
            )
  (:use [clojurewerkz.crawlista.extraction.content]
        [noir.core :only [defpage]]
        [clojure.string :only (split)]
        [ring.util.codec :only [url-encode url-decode]]
        [cheshire.core]
        [hiccup.element :only [link-to]]
        hiccup.core hiccup.form))

(defn extract-article [url]
  (extract-text (slurp url)))
  
(defn get-title [url]
  (extract-title (slurp url)))
  
(defn count-words [text]
  (count (split text #"\s+")))

(defn rdd-url
  "Leverage Readability view in order to ease the article extraction."
  [url]
  (str "http://www.readability.com/m?url=" url))

(defn httpify-url
  "Add http:// to url if not present"
  [url]
  (if (= (subs url 0 4) "http")
    url
    (str "http://" url)))

(def count-words-from-url (comp count-words extract-article))

(defn prettify-minutes
  "Convert 1.5 into '1 minutes, 30 seconds"
  [minutes]
  (if (< minutes 1)
    (str (* 60 minutes) " seconds")
    (str (int minutes) " minutes, " (int (* 60 (rem minutes (int minutes)))) " seconds")) 
  )

(defpage [:get "/"] {:keys [url]}
  (if url (let [title   (get-title (httpify-url url))
                minutes (float (/ (count-words-from-url (rdd-url (httpify-url url))) 250))
                time    (prettify-minutes minutes)]
    (common/template
     (if-not time [:h4 "Give it a try. Put in an article URL below."] [:h4 "Article URL:"] )
     (form-to [:get "/"]
              (text-field "url")
              (submit-button "Submit"))

     (if time [:h4
               (link-to url title)
               " will take approximately "
               [:strong [:u time]] " to read."
               " Read it on " (link-to (rdd-url url) "Readability")
               "."
               ])
     ))

  (common/template
     [:h4 "Give it a try. Put in an article URL below."]
     (form-to [:get "/"]
              (text-field "url")
              (submit-button "Submit")))))

(defpage [:get "/api"] {:keys [url callback]}
  (let [minutes (float (/ (count-words-from-url (rdd-url (httpify-url url))) 250))]
    (if callback
      (resp/content-type "text/javascript" (str callback "(" (generate-string {:minutes minutes :readable (prettify-minutes minutes)} ) ")"))
      (resp/json { :minutes minutes :readable (prettify-minutes minutes)}))))
