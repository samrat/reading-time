(ns reading-time.extract
  (:require [clojure.string :as cs])
  (:import (org.jsoup Jsoup Connection HttpStatusException)))

(defn extract-article [url]
  (.text (.get (org.jsoup.Jsoup/connect url))))

(defn get-title [url]
  (.title (.get (org.jsoup.Jsoup/connect url))))

(defn count-words [text]
  (count (cs/split text #"\s+")))

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
    (str (int (* 60 minutes)) " seconds")
    (str (int minutes) " minutes, " (int (* 60 (rem minutes (int minutes)))) " seconds")))


(defn get-info
  [url]
  (try (let [doc (.get (org.jsoup.Jsoup/connect (httpify-url url)))
             text (.text doc)
             title (.title doc)
             num-words (count-words text)
             mins-to-read (float (/ num-words 250))]
         {:title title
          :reading-time (prettify-minutes mins-to-read)
          :mins-to-read mins-to-read
          :num-words (count-words text)})
       (catch Exception e
         {:error :url-fetch-failed})))
