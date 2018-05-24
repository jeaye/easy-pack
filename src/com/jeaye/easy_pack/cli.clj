(ns com.jeaye.easy-pack.cli
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [clojure.tools.cli :refer [parse-opts]]
            [me.raynes.fs :as fs]))

; TODO: edn, json
(def valid-outputs #{:image :css})
(def valid-outputs-str (->> (map name valid-outputs)
                            (string/join ",")))

(s/def ::output (s/and keyword? valid-outputs))
(s/def ::outputs (s/coll-of ::output))

(def ^:dynamic *options* {})

(defn valid-output-file?
  "Return whether the output file is in a valid place."
  [path]
  (let [parent (fs/parent path)]
    (and (fs/exists? parent)
         (not (fs/directory? parent)))))

(def cli-options
  [["-o" "--outputs <OUTPUT1,OUTPUT2,...>" (str "Comma-separated list of output types"
                                                " (" valid-outputs-str ")")
    :default "image"
    :parse-fn #(->> (string/split % #",")
                    (map keyword))
    :validate [#(s/valid? ::outputs %) (str "Invalid outputs. Valid outputs are: "
                                            valid-outputs-str)]]
   [nil "--image-file FILE" "Output image file"
    :default "easy-pack.png"
    :validate [valid-output-file? "Invalid image output file"]]
   [nil "--css-file FILE" "Output CSS file"
    :default "easy-pack.css"
    :validate [valid-output-file? "Invalid CSS output file"]]
   ["-v" nil "Verbosity level; may be specified multiple times to increase value"
    :id :verbosity
    :default 0
    :assoc-fn (fn [m k _] (update-in m [k] inc))]
   ["-h" "--help"]])

(defn usage [path summary]
  (->> [(str "Usage: " path " [options] inputs")
        ""
        "Options:"
        summary]
       (string/join \newline)))

(defn error-message [errors]
  (string/join \newline errors))

(defn parse [path cli-args]
  (let [{:keys [options arguments errors summary]} (parse-opts cli-args cli-options)]
    (cond
      (:help options)
      {:exit-message (usage path summary) :ok? true}

      errors
      {:exit-message (error-message errors) :ok? false}

      (or (-> options :outputs empty?)
          (empty? arguments))
      {:exit-message (usage path summary) :ok? false}

      :else
      {:options (assoc options :inputs arguments)})))

(defn exit! [status msg]
  (println msg)
  (System/exit status))
