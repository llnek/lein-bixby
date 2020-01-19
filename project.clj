;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; The group name is the template name, so in this case the template-name
;; is 'blutbad'
(defproject blutbad/lein-template "2.0.0"

  :description "A leiningen template for creating a czlab/blutbad application"

  :license {:url "http://www.eclipse.org/legal/epl-v10.html"
            :name "Eclipse Public License"}
  :url "https://github.com/llnek/lein-blutbad"

  :dependencies []
  :target-path "target/%s"

  :source-paths ["src"]
  :resource-paths ["resources"]

  :eval-in-leiningen true
  :global-vars {*warn-on-reflection* true})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;EOF
