(ns com.jeaye.easy-pack.cli
  (:require [clojure.string :as string]
            [clojure.tools.cli :refer [parse-opts]]))

(def cli-options
  [["-p" "--port PORT" "Port number"
    :default 80
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 0x10000) "Must be a number between 0 and 65536"]]
   ["-v" nil "Verbosity level"
    :id :verbosity
    :default 0
    :assoc-fn (fn [m k _] (update-in m [k] inc))]
   ["-h" "--help"]])

(defn usage [options-summary]
  (->> ["Usage: easy-pack [options]"
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

      (empty? args)
      {:exit-message (usage summary) :ok? false}

      :else
      {:options options})))

(defn exit! [status msg]
  (println msg)
  (System/exit status))
