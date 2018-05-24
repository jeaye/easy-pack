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

(defn TODO [args]
  (let [image-infos (->> (mapv fs/absolute args)
                         distinct
                         (mapv load-image!))
        output-fns [image/output css/output]
        save-fns [image/save! css/save!]
        layout (-> (build-layout image-infos)
                   (generate-outputs output-fns))]
    (doseq [save-fn! save-fns]
      (save-fn! layout))
    layout))

(defn -main [path & args]
  (let [{:keys [options exit-message ok?]}  (cli/parse path args)]
    (if (some? exit-message)
      (cli/exit! (if ok? 0 1) exit-message)
      ; TODO: bind options to dynamic var and run primary logic
      options)))
