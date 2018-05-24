(ns com.jeaye.easy-pack.cli
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [clojure.tools.cli :refer [parse-opts]]
            [me.raynes.fs :as fs]))

; TODO: jpg, edn, json
(def valid-outputs #{:png :css})

(s/def ::output (s/and keyword? valid-outputs))
(s/def ::outputs (s/coll-of ::output))

(defn valid-output-file?
  "Return whether the output file is in a valid place."
  [path]
  (let [parent (fs/parent path)]
    (and (fs/exists? parent)
         (not (fs/directory? parent)))))

(def cli-options
  [["-o" "--outputs <OUTPUT1,OUTPUT2,...>" "Comma-separated list of output types"
    :parse-fn #(->> (string/split % #",")
                    (map keyword))
    :validate [#(s/valid? ::outputs %) "Invalid outputs list"]]
   [nil "--png-file FILE" "Output PNG file"
    :default "easy-pack.png"
    :validate [valid-output-file? "Invalid PNG output file"]]
   [nil "--css-file FILE" "Output CSS file"
    :default "easy-pack.css"
    :validate [valid-output-file? "Invalid CSS output file"]]
   ["-v" nil "Verbosity level; may be specified multiple times to increase value"
    :id :verbosity
    :default 0
    :assoc-fn (fn [m k _] (update-in m [k] inc))]
   ["-h" "--help"]])

(defn usage [summary]
  (->> ["Usage: easy-pack outputs [options]"
        ""
        "Options:"
        summary]
       (string/join \newline)))

(defn error-message [errors]
  (string/join \newline errors))

(defn parse [cli-args]
  (let [{:keys [options args errors summary]} (parse-opts cli-args cli-options)]
    (cond
      (:help options)
      {:exit-message (usage summary) :ok? true}

      errors
      {:exit-message (error-message errors) :ok? false}

      (-> options :outputs empty?)
      {:exit-message (usage summary) :ok? false}

      :else
      {:options options})))

(defn exit! [status msg]
  (println msg)
  (System/exit status))
