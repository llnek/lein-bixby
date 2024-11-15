;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; The group name is the template name, so in this case the template-name
;; is 'bixby'
(defproject bixby/lein-template "2.2.0"

  :description "A leiningen template for creating a czlab/bixby application"

  :license {:url "https://www.apache.org/licenses/LICENSE-2.0.txt"
            :name "Apache License"}

  :url "https://github.com/llnek/lein-bixby"

  :dependencies []
  :target-path "target/%s"

  :source-paths ["src"]
  :resource-paths ["resources"]

  :eval-in-leiningen true
  :global-vars {*warn-on-reflection* true})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;EOF
