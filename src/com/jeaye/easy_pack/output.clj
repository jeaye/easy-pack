(ns com.jeaye.easy-pack.output
  (:require [mikera.image.core :as img]
            [me.raynes.fs :as fs]
            [com.jeaye.easy-pack
             [cli :as cli]
             [layout :as layout]]
            [com.jeaye.easy-pack.output
             [image :as image]
             [css :as css]]))

(def output->fns {:image {:output image/output
                          :save image/save!}
                  :css {:output css/output
                        :save css/save!}})

(defn outputs->fns [outputs]
  (map output->fns outputs))

(defn generate-outputs [layout output-fns]
  (reduce (fn [acc output-fn]
            (output-fn acc))
          layout
          output-fns))

(defn generate-saves! [layout output-fns]
  (doseq [save-fn! output-fns]
    (save-fn! layout)))

(defn generate! [layout]
  (let [output-fns (outputs->fns (:outputs cli/*options*))
        layout-with-outputs (generate-outputs layout (map :output output-fns))]
    (generate-saves! layout-with-outputs (map :save output-fns))
    nil))
