(ns com.jeaye.easy-pack
  (:gen-class)
  (:require [mikera.image.core :as img]))

(defn load-image! [path]
  {:image (img/load-image path)
   :path path})

(defn combine! [image-infos]
  (let [full-width (reduce #(+ %1 (-> %2 :image img/width)) 0 image-infos)
        full-height (apply max 0 (map #(-> % :image img/height) image-infos))
        output-image (img/new-image full-width full-height)
        positions (loop [infos image-infos
                         x 0
                         positions {}]
                    (if-let [info (first infos)]
                      (let [{:keys [image path]} info
                            img-width (img/width image)
                            img-height (img/height image)
                            sub-output (img/sub-image output-image x 0 img-width img-height)]
                        (img/set-pixels sub-output (img/get-pixels image))
                        (recur (rest infos)
                               (+ x img-width)
                               (assoc positions path {:width img-width
                                                      :height img-height
                                                      :x x
                                                      :y 0})))
                      positions))]
    {:image output-image
     :positions positions}))

; --outputs png,css,json,edn
(defn -main [& args]
  (let [image-infos (mapv load-image! args)
        output (combine! image-infos)]
    (img/save (:image output) "output.png" :quality 1.0 :progressive nil)))
