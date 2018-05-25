(ns com.jeaye.easy-pack.output.image
  (:require [mikera.image.core :as img]
            [com.jeaye.easy-pack
             [cli :as cli]]))

(defn output [layout]
  (let [output-image (img/new-image (:width layout) (:height layout))]
    (doseq [{:keys [image x y width height]} (:images layout)]
      (let [sub-image (img/sub-image output-image x y width height)]
        (img/set-pixels sub-image (img/get-pixels image))))
    (assoc-in layout [:output :image] output-image)))

(defn save! [layout]
  (img/save (get-in layout [:output :image])
            (:image-file cli/*options*)
            :quality (:image-quality cli/*options*)
            :progressive nil))
