(ns com.jeaye.easy-pack.test.cli
  (:require [clojure.test :refer :all]
            [com.jeaye.easy-pack
             [cli :as cli]
             [test :as easy-pack.test]]))

(deftest help
  (testing "empty args"
    (let [res (cli/parse easy-pack.test/cli-path [])]
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

(deftest outputs
  (testing "empty outputs"
    (let [res (cli/parse easy-pack.test/cli-path ["-o,"])]
      (is (contains? res :exit-message))
      (is (not (:ok? res)))))

  (testing "invalid output"
    (let [res (cli/parse easy-pack.test/cli-path ["-obad"])]
      (is (contains? res :exit-message))
      (is (not (:ok? res)))))

  (testing "valid output"
    (let [{:keys [options]} (cli/parse easy-pack.test/cli-path
                         ["-oimage" (:1x1 easy-pack.test/images)])]
      (is (= {:outputs [:image]}
             (select-keys options [:outputs])))))

  (testing "multiple valid outputs"
    (let [{:keys [options]} (cli/parse easy-pack.test/cli-path
                                       ["-oimage,css" (:1x1 easy-pack.test/images)])]
      (is (= {:outputs [:image :css]}
             (select-keys options [:outputs])))))

  (testing "duplicate valid outputs"
    (let [{:keys [options]} (cli/parse easy-pack.test/cli-path
                                       ["-oimage,css,image"
                                        (:1x1 easy-pack.test/images)])]
      (is (= {:outputs [:image :css]}
             (select-keys options [:outputs]))))))

(deftest inputs
  (testing "empty inputs"
    (let [res (cli/parse easy-pack.test/cli-path ["-oimage"])]
      (is (contains? res :exit-message))
      (is (not (:ok? res)))))

  (testing "valid input"
    (let [{:keys [options]} (cli/parse easy-pack.test/cli-path
                                       ["-oimage" (:1x1 easy-pack.test/images)])]
      (is (= {:inputs [(:1x1 easy-pack.test/images)]}
             (select-keys options [:inputs])))))

  (testing "multiple valid inputs"
    (let [{:keys [options]} (cli/parse easy-pack.test/cli-path
                                       ["-oimage"
                                        (:1x1 easy-pack.test/images)
                                        (:100x10 easy-pack.test/images)])]
      (is (= {:inputs [(:1x1 easy-pack.test/images)
                       (:100x10 easy-pack.test/images)]}
             (select-keys options [:inputs])))))

  (testing "duplicate valid inputs"
    (let [{:keys [options]} (cli/parse easy-pack.test/cli-path
                                       ["-oimage"
                                        (:1x1 easy-pack.test/images)
                                        (:1x1 easy-pack.test/images)])]
      (is (= {:inputs [(:1x1 easy-pack.test/images)]}
             (select-keys options [:inputs])))))

  (testing "invalid input"
    (let [res (cli/parse easy-pack.test/cli-path
                         ["-oimage" easy-pack.test/missing-image])]
      (is (contains? res :exit-message))
      (is (not (:ok? res))))))

(deftest unit|valid-output-file?
  (testing "non-existent"
    (is (cli/valid-output-file? "this-does-not-exist")))
  (testing "existent"
    (is (cli/valid-output-file? "project.clj")))

  (testing "nested with existent parent"
    (is (cli/valid-output-file? "dev-resources/output")))
  (testing "nested with non-existent parent"
    (is (not (cli/valid-output-file? "bad-parent/output"))))

  (testing "current directory"
    (is (not (cli/valid-output-file? "."))))
  (testing "other directory"
    (is (not (cli/valid-output-file? "src")))))
