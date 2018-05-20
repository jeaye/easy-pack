(ns com.jeaye.easy-pack.output.image
  (:require [mikera.image.core :as img]))

(defn output [layout]
  (println "Generating image output")
  (let [output-image (img/new-image (:width layout) (:height layout))]
    (doseq [{:keys [image x y width height]} (:images layout)]
      (let [sub-image (img/sub-image output-image x y width height)]
        (img/set-pixels sub-image (img/get-pixels image))))
    (assoc-in layout [:output :image] output-image)))
