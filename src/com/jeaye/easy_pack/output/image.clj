(ns com.jeaye.easy-pack.output.image
  (:require [clojure.spec.alpha :as s]
            [mikera.image.core :as img]
            [com.jeaye.easy-pack
             [cli :as cli]
             [util :as util]]))

(s/def ::output (s/keys :req-un [::util/image]))

(defn output [output-state]
  (let [layout (:layout output-state)
        output-image (img/new-image (:width layout) (:height layout))]
    (doseq [{:keys [image x y width height]} (:images layout)]
      (let [sub-image (img/sub-image output-image x y width height)]
        (img/set-pixels sub-image (img/get-pixels image))))
    (assoc-in output-state [:output ::output] output-image)))

(defn save! [output-state]
  (img/save (get-in output-state [:output ::output])
            (:image-file cli/*options*)
            :quality (:image-quality cli/*options*)
            :progressive nil))
