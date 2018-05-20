(ns com.jeaye.easy-pack
  (:gen-class)
  (:require [mikera.image.core :as img]
            [com.jeaye.easy-pack.output
             [image :as image]
             [css :as css]]))

(defn load-image! [path]
  {:image (img/load-image path)
   :path path})

(defn combine! [output-fns image-infos]
  (let [full-width (reduce #(+ %1 (-> %2 :image img/width)) 0 image-infos)
        full-height (apply max 0 (map #(-> % :image img/height) image-infos))
        images (loop [infos image-infos
                      x 0
                      acc {}]
                 (if-let [info (first infos)]
                   (let [{:keys [image path]} info
                         img-width (img/width image)
                         img-height (img/height image)]
                     (recur (rest infos)
                            (+ x img-width)
                            (assoc acc path {:image image
                                             :width img-width
                                             :height img-height
                                             :x x
                                             :y 0})))
                   acc))]
    {:width full-width
     :height full-height
     :images images}))

; --outputs png,css,json,edn
(defn -main [& args]
  (let [image-infos (mapv load-image! args)
        output-fns [image/output css/output]
        output (combine! output-fns image-infos)]
    (img/save (:image output) "output.png" :quality 1.0 :progressive nil)))
