(ns com.jeaye.easy-pack.test.cli
  (:require [clojure.test :refer :all]
            [com.jeaye.easy-pack.test :as easy-pack.test]
            [com.jeaye.easy-pack.cli :as cli]))

(deftest help
  (testing "empty args"
    (let [res (cli/parse easy-pack.test/cli-path [])]
      (is (contains? res :exit-message))
      (is (not (:ok? res)))))

  (testing "empty outputs"
    (let [res (cli/parse easy-pack.test/cli-path ["-o,"])]
      (is (contains? res :exit-message))
      (is (not (:ok? res)))))

  (testing "only -h provided"
    (let [res (cli/parse easy-pack.test/cli-path ["-h"])]
      (is (contains? res :exit-message))
      (is (:ok? res))))

  (testing "-h provided with others"
    (let [res (cli/parse easy-pack.test/cli-path ["-h" "-ocss"])]
      (is (contains? res :exit-message))
      (is (:ok? res)))))
