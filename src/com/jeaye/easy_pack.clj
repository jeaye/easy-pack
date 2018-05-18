(ns com.jeaye.easy-pack
  (:gen-class)
  (:require [mikera.image.core :as img]))

(defn copy-pixels! [output-image images]
  (loop [imgs images
         x 0]
    (when-let [img (first imgs)]
      (let [img-width (img/width img)
            img-height (img/height img)
            sub-output (img/sub-image output-image x 0 img-width img-height)]
        (img/set-pixels sub-output (img/get-pixels img))
        (recur (rest imgs) (+ x img-width))))))

(defn -main [& args]
  (let [images (mapv img/load-image args)
        full-width (reduce #(+ %1 (img/width %2)) 0 images)
        full-height (apply max 0 (map img/height images))
        output-image (img/new-image full-width full-height)]
    (copy-pixels! output-image images)
    output-image))
