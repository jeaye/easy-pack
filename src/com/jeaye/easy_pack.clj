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
         y 0
         acc {:width 0
              :height 0
              :images []}]
    (if-let [info (first infos)]
      (let [{:keys [image path]} info
            img-width (img/width image)
            img-height (img/height image)]
        (recur (rest infos)
               (+ x img-width)
               y
               (-> acc
                   (update :width + img-width)
                   (update :height max img-height)
                   (update :images conj {:image image
                                         :path path
                                         :width img-width
                                         :height img-height
                                         :x x
                                         :y y}))))
      acc)))

(defn generate-outputs [layout output-fns]
  (reduce (fn [acc output-fn]
            (merge acc (output-fn acc)))
          layout
          output-fns))

; --outputs png,css,json,edn
(defn -main [& args]
  (let [image-infos (mapv load-image! args)
        output-fns [image/output css/output]
        layout (build-layout image-infos)]
    layout
    #_(img/save (:image output) "output.png" :quality 1.0 :progressive nil)))
