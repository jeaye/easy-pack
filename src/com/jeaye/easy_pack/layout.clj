(ns com.jeaye.easy-pack.layout
  (:require [clojure.spec.alpha :as s]
            [orchestra.core :refer [defn-spec]]
            [mikera.image.core :as img]
            [com.jeaye.easy-pack
             [cli :as cli]
             [input :as input]
             [util :as util]]
            [com.jeaye.easy-pack.output
             [image :as image]
             [css :as css]]))

(s/def ::width nat-int?)
(s/def ::height nat-int?)
(s/def ::x nat-int?)
(s/def ::y nat-int?)
(s/def ::image (s/keys :req-un [::util/image
                                ::util/path
                                ::width
                                ::height
                                ::x
                                ::y]))
(s/def ::images (s/coll-of ::image))
(s/def ::info (s/keys :req-un [::width
                               ::height
                               ::images]))

; TODO: Smarter layout
(defn-spec generate ::info
  [loaded-images (s/coll-of ::input/loaded-image)]
  (loop [imgs loaded-images
         x (long 0)
         y (long 0)
         acc {:width 0
              :height 0
              :images []}]
    (if-let [loaded-image (first imgs)]
      (let [{:keys [image path]} loaded-image
            img-width (img/width image)
            img-height (img/height image)]
        (recur (rest imgs)
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
