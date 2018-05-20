(ns com.jeaye.easy-pack.output.image
  (:require [mikera.image.core :as img]))

(defn output [acc image-infos]
  (let [output-image (img/new-image (:width acc) (:height acc))]
    (doseq [{:keys [image x y width height]} (:images acc)]
      (let [sub-image (img/sub-image output-image x y width height)]
        (img/set-pixels sub-image (img/get-pixels image))))
    (assoc-in acc [:output :image] output-image)))
