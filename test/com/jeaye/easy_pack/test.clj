(ns com.jeaye.easy-pack.test
  (:require [clojure.test :refer :all]
            [orchestra.spec.test :as st]
            [me.raynes.fs :as fs]
            [com.jeaye.easy-pack
             [cli :as cli]
             [layout :as layout]
             [input :as input]
             [output :as output]]))

(def cli-path "easy-pack-test")

(def resource-path "dev-resources/")
(def images {:1x1 (str resource-path "1x1.png")
             :100x10 (str resource-path "100x10.png")
             :10x100 (str resource-path "10x100.png")
             :100x100 (str resource-path "100x100.png")})
(def missing-image "this/will/likely/never/exist.png")

(defn enable-instrumentation! [f]
  (println "Instrumenting all fns")
  (st/instrument)
  (f))
(use-fixtures :once enable-instrumentation!)

(defn temp-file-for-output [output]
  (let [suffix (case output
                 :image ".png"
                 :css ".css")]
    ; TODO: reflection
    (.getPath (fs/temp-file "easy-pack" suffix))))

(defmacro with-layout [config & body]
  (let [outputs-str (->> (map name (:outputs config))
                         (clojure.string/join ","))
        ; TODO: cleanup
        output-files (mapcat (fn [[output file]]
                               (vector (str "--" (name output) "-file") file))
                             (:output-files config))
        args (-> (into ["-o" outputs-str] output-files)
                 (into (:inputs config)))]
    `(binding [~'cli/*options* (:options (cli/parse ~cli-path ~args))]
       (let [~'image-infos (mapv ~input/load-image! ~'(:inputs cli/*options*))
             ~'layout (~layout/generate ~'image-infos)]
         ~@body))))

(defmacro with-generated-output [config & body]
  `(with-layout ~config
     (let [~'output-fns (~output/outputs->fns ~'(:outputs cli/*options*))
           ~'output-state (~output/generate-outputs ~'layout
                                                    ~'(map :output-fn output-fns))]
       ~@body)))

(defmacro with-saved-output [config & body]
  (let [output-files (->> (:outputs config)
                          (map #(vector % (temp-file-for-output %)))
                          (into {}))]
    `(with-generated-output ~(assoc config :output-files output-files)
       (let [~'output-files ~output-files]
         (~output/generate-saves! ~'output-state ~'(map :save-fn output-fns))
         ~@body))))
