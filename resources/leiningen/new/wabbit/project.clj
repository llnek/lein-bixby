;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
(defproject {{raw-name}} "{{ver}}"

  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :license {:name "Apache License 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}

  :dependencies [[org.clojure/clojure "1.8.0"]]
  :main ^:skip-aot {{domain}}
  :target-path "target/%s"

  :profiles {:uberjar {:aot :all}})
