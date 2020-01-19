;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defproject {{name}} "{{ver}}"

  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies
  [[org.clojure/clojurescript "1.10.597"]
   [org.clojure/clojure "1.10.1"]
   [commons-logging "1.2"]
   [io.czlab/blutbad "2.0.0"]]

  :plugins [[blutbad/lein-template "2.0.0"]
            [cider/cider-nrepl "0.22.4"]
            [lein-codox "0.10.7"]]

  :kill-port 4444
  :profiles
  {:podify
   {:agentlib "-agentlib:jdwp=transport=dt_socket,server=y,address=8787,suspend=n"
    :jvm-opts ^:replace
    ["-XX:+CMSClassUnloadingEnabled"
     "-XX:+UseConcMarkSweepGC"
     "-Xms1g"
     "-Xmx8g"
     "-Dblutbad.kill.port=@@kill-port@@"
     "-Dlog4j.configurationFile=file:etc/log4j2d.xml"]}
   :uberjar {:aot :all}}

  :global-vars {*warn-on-reflection* true}
  :target-path "target/%s"
  :aot :all

  :aliases {"blutbad-deploy" ["with-profile" "podify" "blutbad"]
            "blutbad-stop" ["trampoline" "run" "-m" "czlab.blutbad.cons.con4"]
            "blutbad-run" ["trampoline" "run" "-m" "czlab.blutbad.exec"]
            "blutbad-console" ["trampoline" "run" "-m" "czlab.blutbad.cons.con7"]}

  :java-source-paths ["src/main/java" "src/test/java"]
  :source-paths ["src/main/clojure"]
  :test-paths ["src/test/clojure"]
  :resource-paths ["src/main/resources"]

  :jvm-opts ["-Dlog4j.configurationFile=file:etc/log4j2c.xml"]
  :javac-options [;"-source" "8"
                  "-Xlint:unchecked" "-Xlint:-options" "-Xlint:deprecation"])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;EOF


