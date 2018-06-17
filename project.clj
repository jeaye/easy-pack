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
                 [imagez "1.6.1" :exclusions [;net.mikera/mathz
                                              ;net.mikera/randomz
                                              ;net.mikera/mikera-gui
                                              ;org.imgscalr/imgscalr-lib
                                              ]]]
  :plugins [[io.taylorwood/lein-native-image "0.2.0"]]
  :main ^:skip-aot com.jeaye.easy-pack
  :target-path "target/%s"
  :native-image {:name "easy-pack"
                 :opts ["--verbose"
                        "--no-server"
                        "-H:+ReportUnsupportedElementsAtRuntime"]
                 :graal-bin "/usr/lib/jvm/java-8-graal/bin"}
  :profiles {:dev {:source-paths ["src/" "test/"]
                   :plugins [[com.jakemccrary/lein-test-refresh "0.22.0"]
                             [lein-cloverage "1.0.10"]]
                   :global-vars {*warn-on-reflection* true
                                 *assert* true}}
             :uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
