(ns com.jeaye.easy-pack.test
  (:require [clojure.test :refer :all]
            [com.jeaye.easy-pack
             [cli :as cli]
             [layout :as layout]
             [output :as output]]))

(def cli-path "easy-pack-test")

(def resource-path "dev-resources/")
(def images {:1x1 (str resource-path "1x1.png")
             :100x10 (str resource-path "100x10.png")
             :10x100 (str resource-path "10x100.png")
             :100x100 (str resource-path "100x100.png")})
(def missing-image "this/will/likely/never/exist.png")

(defmacro with-layout [config & body]
  (let [outputs-str (->> (map name (:outputs config))
                         (clojure.string/join ","))
        args (into ["-o" outputs-str] (:inputs config))]
    `(binding [~'cli/*options* (:options (cli/parse ~cli-path ~args))]
       (let [~'image-infos ~'(mapv input/load-image! (:inputs cli/*options*))
             ~'layout ~'(layout/generate image-infos)]
         ~@body))))

(defmacro with-generated-output [config & body]
  `(with-layout ~config
     (let [~'output-fns ~'(->> (output/outputs->fns (:outputs cli/*options*))
                               (map :output))
           ~'layout-with-outputs ~'(output/generate-outputs layout output-fns)]
       ~@body)))
