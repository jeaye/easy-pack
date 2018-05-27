(ns com.jeaye.easy-pack.test
  (:require [clojure.test :refer :all]))

(def cli-path "easy-pack-test")

(def resource-path "dev-resources/")
(def images {:1x1 (str resource-path "1x1.png")
             :100x10 (str resource-path "100x10.png")
             :10x100 (str resource-path "10x100.png")
             :100x100 (str resource-path "100x100.png")})
(def missing-image "this/will/likely/never/exist.png")
