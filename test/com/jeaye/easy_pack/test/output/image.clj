(ns com.jeaye.easy-pack.test.output.image
  (:require [clojure.test :refer :all]
            [mikera.image.core :as img]
            [com.jeaye.easy-pack
             [cli :as cli]
             [input :as input]
             [layout :as layout]
             [output :as output]
             [test :as easy-pack.test :refer [with-generated-output]]]))

(deftest generate
  (testing "single image"
    (with-generated-output {:inputs [(:1x1 easy-pack.test/images)]
                            :outputs [:image]}
      (let [image (get-in layout-with-outputs [:output :image])
            width (img/width image)
            height (img/height image)]
        (is (some? image))
        (is (= 1 width))
        (is (= 1 height))))))
