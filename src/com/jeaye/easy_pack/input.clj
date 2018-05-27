(ns com.jeaye.easy-pack.input
  (:require [mikera.image.core :as img]))

(defn load-image! [path]
  {:image (img/load-image path)
   :path path})
