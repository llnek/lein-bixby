;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defproject lein-libjars "2.2.0"

  :description "A leiningen plugin to download all required jars into lib dir."

  :license {:url "https://www.apache.org/licenses/LICENSE-2.0.txt"
            :name "Apache License"}

  :url "https://github.com/llnek/lein-libjars"

  :dependencies []

  :target-path "target/%s"
  :source-paths ["src"]

  :eval-in-leiningen true
  :global-vars {*warn-on-reflection* true})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;EOF
