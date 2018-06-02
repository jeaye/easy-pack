(ns com.jeaye.easy-pack.input
  (:require [clojure.spec.alpha :as s]
            [mikera.image.core :as img]
            [orchestra.core :refer [defn-spec]]
            [com.jeaye.easy-pack.util :as util]))

(s/def ::image #(instance? java.awt.image.BufferedImage %))
(s/def ::loaded-image (s/keys :req-un [::image ::util/path]))

(defn-spec load-image! ::loaded-image
  [path ::util/path]
  {:image (img/load-image path)
   :path path})
