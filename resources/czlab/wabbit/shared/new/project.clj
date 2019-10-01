;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defproject {{raw-name}} "{{ver}}"

  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies
  [[org.clojure/clojurescript "1.10.520"]
   [org.clojure/clojure "1.10.1"]
   [commons-logging "1.2"]
   [io.czlab/wabbit "1.1.0"]]

  :plugins [[wabbit/lein-template "1.1.0"]
            [cider/cider-nrepl "0.22.2"]
            [lein-codox "0.10.7"]
            [lein-cprint "1.3.1"]]

  :kill-port "localhost:4444"
  :profiles
  {:podify
   {:agentlib "-agentlib:jdwp=transport=dt_socket,server=y,address=8787,suspend=n"
    :jvm-opts ^:replace
    ["-XX:+CMSClassUnloadingEnabled"
     "-XX:+UseConcMarkSweepGC"
     "-Xms1g"
     "-Xmx8g"
     "-Dwabbit.kill.port=@@kill-port@@"
     "-Dlog4j.configurationFile=file:etc/log4j2d.xml"]}
   :uberjar {:aot :all}}

  :global-vars {*warn-on-reflection* true}
  :target-path "target/%s"
  :aot :all

  :aliases {"wabbit-deploy" ["with-profile" "podify" "wabbit"]
            "wabbit-run" ["trampoline" "run" "-m" "czlab.wabbit.exec"]
            "wabbit-console" ["trampoline" "run" "-m" "czlab.wabbit.cons.con7"]}

  :java-source-paths ["src/main/java" "src/test/java"]
  :source-paths ["src/main/clojure"]
  :test-paths ["src/test/clojure"]
  :resource-paths ["src/main/resources"]

  :jvm-opts ["-Dlog4j.configurationFile=file:etc/log4j2c.xml"]
  :javac-options [;"-source" "8"
                  "-Xlint:unchecked" "-Xlint:-options" "-Xlint:deprecation"])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;EOF


