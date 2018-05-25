(ns com.jeaye.easy-pack
  (:gen-class)
  (:require [mikera.image.core :as img]
            [me.raynes.fs :as fs]
            [com.jeaye.easy-pack
             [cli :as cli]]
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
            (output-fn acc))
          layout
          output-fns))

(def output->fns {:image {:output image/output
                          :save image/save!}
                  :css {:output css/output
                        :save css/save!}})

(defn pack! []
  (let [image-infos (->> (:inputs cli/*options*)
                         (mapv fs/absolute)
                         distinct
                         (mapv load-image!))
        output-fns (map output->fns (:outputs cli/*options*))
        layout (-> (build-layout image-infos)
                   (generate-outputs (map :output output-fns)))]
    (doseq [save-fn! (map :save output-fns)]
      (save-fn! layout))
    layout))

(defn -main [path & args]
  (let [{:keys [options exit-message ok?]} (cli/parse path args)]
    (if (some? exit-message)
      (cli/exit! (if ok? 0 1) exit-message)
      (binding [cli/*options* options]
        (clojure.pprint/pprint options)
        (pack!)))))
