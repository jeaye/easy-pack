(ns com.jeaye.easy-pack.test.output.image
  (:require [clojure.test :refer :all]
            [mikera.image.core :as img]
            [com.jeaye.easy-pack
             [cli :as cli]
             [input :as input]
             [layout :as layout]
             [output :as output]
             [test :as easy-pack.test
              :refer [with-generated-output with-saved-output]]]))

(deftest integration|generate
  (testing "generating from single image"
    (with-generated-output {:inputs [(:1x1 easy-pack.test/images)]
                            :outputs [:image]}
      (let [image (get-in layout-with-outputs [:output :image])
            width (img/width image)
            height (img/height image)]
        (is (some? image))
        (is (= 1 width))
        (is (= 1 height))))))

(deftest integration|save
  (testing "saving from single image"
    (with-saved-output {:inputs [(:1x1 easy-pack.test/images)]
                        :outputs [:image]}
      (let [image (get-in layout-with-outputs [:output :image])
            saved (input/load-image! (:image output-files))
            saved-image (:image saved)
            saved-width (img/width saved-image)
            saved-height (img/height saved-image)]
        (is (= (into [] (img/get-pixels saved-image))
               (into [] (img/get-pixels image))))
        (is (= 1 saved-width))
        (is (= 1 saved-height))))))
