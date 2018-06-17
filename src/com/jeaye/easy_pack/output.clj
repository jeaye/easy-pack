(ns com.jeaye.easy-pack.output
  (:require [clojure.spec.alpha :as s]
            [orchestra.core :refer [defn-spec]]
            [me.raynes.fs :as fs]
            [com.jeaye.easy-pack
             [cli :as cli]
             [layout :as layout]]
            [com.jeaye.easy-pack.output
             [image :as image]
             [css :as css]]))

(def output->fns {:image {:output-fn image/output
                          :save-fn image/save!}
                  :css {:output-fn css/output
                        :save-fn css/save!}})

(s/def ::type (s/and keyword? (into #{} (keys output->fns))))
(s/def ::output-fn fn?)
(s/def ::save-fn fn?)
(s/def ::fn (s/keys :req-un [::output-fn
                             ::save-fn]))
(s/def ::fns (s/coll-of ::fn))

(s/def ::output (s/keys :opt [::image/output
                              ::css/output]))
(s/def ::state (s/keys :req-un [::layout
                                ::output]))

(defn-spec outputs->fns ::fns
  [outputs (s/coll-of ::type)]
  (map output->fns outputs))

(defn-spec generate-outputs ::state
  [layout ::layout/info, output-fns (s/coll-of ::output-fn)]
  (reduce (fn [acc output-fn]
            (output-fn acc))
          {:layout layout
           :output {}}
          output-fns))

(defn-spec generate-saves! nil?
  [output-state ::state, output-fns (s/coll-of ::save)]
  (doseq [save-fn! output-fns]
    (save-fn! output-state)))

(defn-spec generate! nil?
  [layout ::layout/info]
  (let [output-fns (outputs->fns (:outputs cli/*options*))
        output-state (generate-outputs layout
                                       (map :output-fn output-fns))]
    (generate-saves! output-state (map :save-fn output-fns))
    nil))
