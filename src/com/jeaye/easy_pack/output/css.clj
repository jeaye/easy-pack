(ns com.jeaye.easy-pack.output.css
  (:require [clojure.spec.alpha :as s]
            [me.raynes.fs :as fs]
            [com.jeaye.easy-pack
             [cli :as cli]
             [util :as util]]))

(s/def ::output ::util/non-empty-string)

; TODO: Configure class
(def base-class ".icon")
; TODO: Base image url
; background-image: url({% asset sprite-sheet.png @path %}) !important;
(def base-output (str base-class " {}\n"))

(defn build-class-name [image]
  (let [base-path (->> (fs/base-name (:path image) true)
                       ; https://stackoverflow.com/questions/448981/which-characters-are-valid-in-css-class-names-selectors#449000
                       (filter #(re-matches #"[_a-zA-Z0-9-]" (str %)))
                       (apply str))]
    (str base-class "-" base-path)))

(defn image->css [image]
  (str (:css-class image) " {\n"
       "  background-position: " (-> image :x -) "px " (-> image :y -) "px;\n"
       "  width: " (:width image) "px;\n"
       "  height: " (:height image) "px;\n"
       "}\n"))

(defn build [images]
  (apply str base-output (map image->css images)))

(defn output [output-state]
  ; TODO: Files in different dirs with the same name will have the same class
  (let [images (mapv #(assoc % :css-class (build-class-name %))
                     (-> output-state :layout :images))]
    (-> (assoc-in output-state [:layout :images] images)
        (assoc-in [:output ::output] (build images)))))

(defn save! [output-state]
  (spit (:css-file cli/*options*) (get-in output-state [:output :css])))
