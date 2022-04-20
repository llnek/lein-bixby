;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defproject {{name}} "{{ver}}"

  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :license {:url "https://www.apache.org/licenses/LICENSE-2.0.txt"
            :name "Apache License"}

  :dependencies
  [[org.clojure/clojurescript "1.11.4"]
   [org.clojure/clojure "1.11.1"]
   [commons-logging "1.2"]
   [io.czlab/bixby "2.1.0"]]

  :plugins [[cider/cider-nrepl "0.28.3"]
            [lein-codox "0.10.8"]
            [lein-cljsbuild "1.1.8"]]

  :kill-port 4444
  :profiles
  {:podify
   {:agentlib "-agentlib:jdwp=transport=dt_socket,server=y,address=8787,suspend=n"
    :jvm-opts ^:replace
    ["-XX:+CMSClassUnloadingEnabled"
     "-XX:+UseConcMarkSweepGC"
     "-Xms1g"
     "-Xmx8g"
     "-Dbixby.kill.port=@@kill-port@@"
     "-Dlog4j.configurationFile=file:etc/log4j2d.xml"]}
   :uberjar {:aot :all}}

  :global-vars {*warn-on-reflection* true}
  :target-path "target/%s"
  :aot :all

  :aliases {"bixby-deploy" ["with-profile" "podify" "bixby"]
            "bixby-stop" ["trampoline" "run" "-m" "czlab.bixby.cons.con4"]
            "bixby-run" ["trampoline" "run" "-m" "czlab.bixby.exec"]
            "bixby-console" ["trampoline" "run" "-m" "czlab.bixby.cons.con7"]}

  :java-source-paths ["src/main/java" "src/test/java"]
  :source-paths ["src/main/clojure"]
  :test-paths ["src/test/clojure"]
  :resource-paths ["src/main/resources"]

  :jvm-opts ["-Dlog4j.configurationFile=file:etc/log4j2c.xml"]
  :javac-options ["-source" "16"
                  "-target" "16"
                  "-Xlint:unchecked" "-Xlint:-options" "-Xlint:deprecation"])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;EOF


