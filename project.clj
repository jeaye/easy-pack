(defproject com.jeaye/easy-pack "0.1.0-SNAPSHOT"
  :description ""
  :url ""
  :license {:name "jank license"
            :url "https://upload.jeaye.com/jank-license"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [net.mikera/imagez "0.12.0"]]
  :main ^:skip-aot com.jeaye.easy-pack
  :target-path "target/%s"
  :profiles {:dev {:global-vars {*warn-on-reflection* true
                                 *assert* true}}
             :uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
