(ns com.jeaye.easy-pack.cli
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [clojure.tools.cli :refer [parse-opts]]))

; TODO: jpg, edn, json
(def valid-outputs #{:png :css})

(s/def ::output (s/and keyword? valid-outputs))
(s/def ::outputs (s/coll-of ::output))

(def cli-options
  [["-o" "--outputs <OUTPUT1,OUTPUT2,...>" "Comma-separated list of output types"
    :parse-fn #(->> (string/split % #",")
                    (map keyword))
    :validate [#(s/valid? ::outputs %) "Invalid outputs list"]]
   ["-v" nil "Verbosity level; may be specified multiple times to increase value"
    :id :verbosity
    :default 0
    :assoc-fn (fn [m k _] (update-in m [k] inc))]
   ["-h" "--help"]])

(defn usage [options-summary]
  (->> ["Usage: easy-pack [options] outputs"
        ""
        "Options:"
        options-summary]
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
