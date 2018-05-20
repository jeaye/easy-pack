(ns com.jeaye.easy-pack
  (:gen-class)
  (:require [mikera.image.core :as img]
            [com.jeaye.easy-pack.output
             [image :as image]
             [css :as css]]))

(defn load-image! [path]
  {:image (img/load-image path)
   :path path})

(defn build-layout [image-infos]
  (loop [infos image-infos
         x 0
         acc {:width 0
              :height 0
              :layout {}}]
    (if-let [info (first infos)]
      (let [{:keys [image path]} info
            img-width (img/width image)
            img-height (img/height image)]
        (recur (rest infos)
               (+ x img-width)
               (-> acc
                   (update :width + img-width)
                   (update :height max img-height)
                   (assoc-in [:layout path] {:image image
                                             :width img-width
                                             :height img-height
                                             :x x
                                             :y 0}))))
      acc)))

; --outputs png,css,json,edn
(defn -main [& args]
  (let [image-infos (mapv load-image! args)
        output-fns [image/output css/output]
        layout (build-layout image-infos)]
    (img/save (:image output) "output.png" :quality 1.0 :progressive nil)))
