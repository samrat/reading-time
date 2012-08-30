(ns reading-time.views.app
  (:require [reading-time.views.common :as common]
            [reading-time.settings :as settings]
            [noir.response :as resp]
            )
  (:use [clojurewerkz.crawlista.extraction.content]
        [noir.core :only [defpage]]
        [clojure.string :only (split)]
        [ring.util.codec :only [url-encode url-decode]]
        [cheshire.core]
        hiccup.core hiccup.form))

(defn scrape-article [url]
  (let [api-url (str "http://www.diffbot.com/api/article?token=" settings/token "&url=" url)
        parsed-json (parse-string (slurp api-url))]
    (parsed-json "text")))

(defn scrape-title [url]
  (let [api-url (str "http://www.diffbot.com/api/article?token=" settings/token "&url=" url)
        parsed-json (parse-string (slurp api-url))]
    (parsed-json "title")))

(defn extract-article [url]
  (if (= (subs url 0 4) "http")
    (extract-text (slurp url))
    (extract-text (slurp (str "http://" url)))))

(defn get-title [url]
  (if (= (subs url 0 4) "http")
    (extract-title(slurp url))
    (extract-title (slurp (str "http://" url)))))

(defn count-words [text]
  (count (split text #"\s+")))

(def count-words-from-url (comp count-words scrape-article))

(defn prettify-minutes
  "Convert 1.5 into '1 minutes, 30 seconds"
  [minutes]
  (if (< minutes 1)
    (str (* 60 minutes) " seconds")
    (str (int minutes) " minutes, " (int (* 60 (rem minutes (int minutes)))) " seconds")) 
  )

(defpage [:get "/"] {:keys [url]}
  (if url (let [title (get-title url)
        minutes  (float (/ (count-words-from-url url) 250))
        time    (prettify-minutes minutes)]
    (common/template
     (if-not time [:h4 "Give it a try. Put in an article URL below."] [:h4 "Article URL:"] )
     (form-to [:get "/"]
              (text-field "url")
              (submit-button "Submit"))

     (if time [:h4 [:strong title] " will take approximately " [:u time] " to read."])
     ))

  (common/template
     [:h4 "Give it a try. Put in an article URL below."]
     (form-to [:get "/"]
              (text-field "url")
              (submit-button "Submit")))))

(defpage [:get "/api"] {:keys [url]}
  (let [minutes (float (/ (count-words-from-url url) 250))]
  (resp/json {:readable (prettify-minutes minutes) :minutes minutes})))
