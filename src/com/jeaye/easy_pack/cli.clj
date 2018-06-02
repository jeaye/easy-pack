(ns com.jeaye.easy-pack.cli
  (:require [clojure.spec.alpha :as s]
            [clojure.edn :as edn]
            [clojure.string :as string]
            [clojure.tools.cli :refer [parse-opts]]
            [orchestra.core :refer [defn-spec]]
            [medley.core :refer [distinct-by]]
            [me.raynes.fs :as fs]
            [com.jeaye.easy-pack.util :as util]))

; TODO: edn, json
(def valid-outputs #{:image :css})
(def valid-outputs-str (->> (map name valid-outputs)
                            (string/join ",")))

(s/def ::output (s/and keyword? valid-outputs))
(s/def ::outputs (s/coll-of ::output))
(s/def ::image-quality (s/and number? #(< 0 % 1)))

(s/def ::summary ::util/non-empty-string)
(s/def ::args (s/coll-of ::util/non-empty-string))

(s/def ::exit-status integer?)
(s/def ::exit-message ::util/non-empty-string)

(s/def ::ok? boolean?)
(s/def ::options (s/keys :req-un []))
(s/def ::parse-state (s/or :error (s/keys :req-un [::exit-message
                                                   ::ok?])
                           :success (s/keys :req-un [::options])))

(def ^:dynamic *options* {})

(defn-spec valid-output-file? boolean?
  "Returns whether the output file is in a valid place."
  [path ::util/path]
  (let [parent (fs/parent path)]
    (and (fs/exists? parent)
         (not (fs/directory? path)))))

(defn-spec valid-input-file? boolean?
  "Returns whether the input file is readable."
  [path ::util/path]
  (and (fs/exists? path) (fs/file? path)))

(def cli-options
  [["-o" "--outputs <OUTPUT1,OUTPUT2,...>" (str "Comma-separated list of output types"
                                                " (" valid-outputs-str ")")
    :default "image"
    :parse-fn #(->> (string/split % #",")
                    (map keyword)
                    distinct)
    :validate [#(s/valid? ::outputs %) (str "Invalid outputs. Valid outputs are: "
                                            valid-outputs-str)]]
   [nil "--image-file FILE" "Output image file"
    :default "easy-pack.png"
    :validate [valid-output-file? "Invalid image output file"]]
   [nil "--image-quality NUM" "Quality of output image file (from 0.0 to 1.0)"
    :default 1.0
    :parse-fn edn/read-string
    :validate [#(s/valid? ::image-quality %) "Invalid image quality. Valid range is from 0.0 to 1.0 inclusive."]]
   [nil "--css-file FILE" "Output CSS file"
    :default "easy-pack.css"
    :validate [valid-output-file? "Invalid CSS output file"]]
   ; TODO: Use this
   ["-v" nil "Verbosity level; may be specified multiple times to increase value"
    :id :verbosity
    :default 0
    :assoc-fn (fn [m k _] (update-in m [k] inc))]
   ["-h" "--help"]])

(defn-spec usage ::util/non-empty-string
  [path ::util/path, summary ::summary]
  (->> [(str "Usage: " path " [options] inputs")
        ""
        "Options:"
        summary]
       (string/join \newline)))

(defn-spec error-message string?
  [errors (s/coll-of ::util/non-empty-string)]
  (string/join \newline errors))

(defn-spec parse ::parse-state
  [path ::util/path, cli-args ::args]
  (let [{:keys [options arguments errors summary]} (parse-opts cli-args cli-options)
        ; Rule out duplicates by dictinction of absolute paths.
        inputs (->> (mapv #(vector % (fs/absolute %)) arguments)
                    (distinct-by second)
                    (map first))
        invalid-inputs (filter (comp not valid-input-file?) inputs)]
    (cond
      (:help options)
      {:exit-message (usage path summary)
       :ok? true}

      errors
      {:exit-message (error-message errors)
       :ok? false}

      (or (-> options :outputs empty?)
          (empty? inputs))
      {:exit-message (usage path summary)
       :ok? false}

      (not-empty invalid-inputs)
      {:exit-message (str "Invalid input files: "
                          (->> (map #(str "'" % "'") invalid-inputs)
                               (string/join ", ")))
       :ok? false}

      :else
      {:options (assoc options :inputs inputs)})))

(defn-spec exit! nil?
  [status ::exit-status msg ::exit-message]
  (println msg)
  (System/exit status))
