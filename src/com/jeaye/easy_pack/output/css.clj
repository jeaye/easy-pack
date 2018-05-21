(ns com.jeaye.easy-pack.output.css
  (:require [me.raynes.fs :as fs]))

(def base-class ".icon")
(def base-output (str base-class " {}\n"))

(defn build-class-name [image]
  (let [base-path (fs/base-name (:path image) true)]
    (str base-class "-" base-path)))

(defn image->css [image]
  (str (:css-class image) " {\n"
       "  background-position: " (:x image) "px " (:y image) "px;\n"
       "  width: " (:width image) "px;\n"
       "  height: " (:height image) "px;\n"
       "}\n"))

(defn build [images]
  (apply str base-output (map image->css images)))

(defn output [layout]
  ; TODO: Files in different dirs with the same name will have the same class
  (let [images (mapv #(assoc % :css-class (build-class-name %))
                     (:images layout))]
    (-> (assoc layout :images images)
        (assoc-in [:output :css] (build images)))))

(defn save! [layout])
