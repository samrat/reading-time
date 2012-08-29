(ns reading-time.views.app
  (:require [reading-time.views.common :as common]
            [noir.response :as resp]
            )
  (:use [clojurewerkz.crawlista.extraction.content]
        [noir.core :only [defpage]]
        [clojure.string :only (split)]
        [ring.util.codec :only [url-encode url-decode]]
        [cheshire.core]
        hiccup.core hiccup.form))

(defn extract-article [url]
  (extract-text (slurp url)))

(defn get-title [url]
  (extract-title (slurp url)))

(defn count-words [text]
  (count (split text #"\s+")))

(def count-words-from-url (comp count-words extract-article))

(defn prettify-minutes
  "Convert 1.5 into '1 minutes, 30 seconds"
  [minutes]
  (if (< minutes 1)
    (str (* 60 minutes) " seconds")
    (str (int minutes) " minutes, " (int (* 60 (rem minutes (int minutes)))) " seconds")) 
  )

(defpage "/" {:keys [time title]}
  (common/template
   (if-not time [:h4 "Give it a try. Put in an article URL below."] [:h4 "Article URL:"] )
   (form-to [:post "/"]
            (text-field "url")
            (submit-button "Submit"))

   (if time [:h4 [:strong title] " will take approximately " [:u time] " to read."])
   ))

(defpage [:post "/"] {:keys [url]}
  (noir.core/render "/"
                    {:time (prettify-minutes (float (/ (count-words-from-url url) 250)) )
                     :title (get-title url)}))

(defpage [:get "/api"] {:keys [url]}
  (let [minutes (float (/ (count-words-from-url url) 250))]
  (resp/json {:readable (prettify-minutes minutes) :minutes minutes})))
