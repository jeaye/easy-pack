(defproject com.jeaye/easy-pack "production"
  :description ""
  :url ""
  :license {:name "jank license"
            :url "https://upload.jeaye.com/jank-license"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/tools.cli "0.3.7"]
                 [orchestra "2017.11.12-1"]
                 [medley "1.0.0"]
                 [me.raynes/fs "1.4.6"]
                 [net.mikera/imagez "0.12.0"]]
  :main ^:skip-aot com.jeaye.easy-pack
  :target-path "target/%s"
  :profiles {:dev {:plugins [[com.jakemccrary/lein-test-refresh "0.22.0"]
                             [lein-cloverage "1.0.10"]]
                   :global-vars {*warn-on-reflection* true
                                 *assert* true}}
             :uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
