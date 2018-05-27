(ns com.jeaye.easy-pack
  (:gen-class)
  (:require [com.jeaye.easy-pack
             [cli :as cli]
             [layout :as layout]
             [input :as input]
             [output :as output]]))

(defn -main [path & args]
  (let [{:keys [options exit-message ok?]} (cli/parse path args)]
    (if (some? exit-message)
      (cli/exit! (if ok? 0 1) exit-message)
      (binding [cli/*options* options]
        (let [image-infos (mapv input/load-image! (:inputs cli/*options*))
              layout (layout/generate image-infos)]
          (output/generate! layout)
          nil)))))
