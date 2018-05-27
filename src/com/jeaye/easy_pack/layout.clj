(ns com.jeaye.easy-pack.layout
  (:require [mikera.image.core :as img]
            [com.jeaye.easy-pack
             [cli :as cli]]
            [com.jeaye.easy-pack.output
             [image :as image]
             [css :as css]]))

; TODO: Smarter layout
(defn generate [image-infos]
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
