;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; The group name is the template name, so in this case the template-name
;; is 'wabbit'
(defproject wabbit/lein-template "1.0.0"

  :description "A leiningen template for creating a czlab/wabbit application"

  :license {:url "http://www.eclipse.org/legal/epl-v10.html"
            :name "Eclipse Public License"}
  :url "https://github.com/llnek/lein-wabbit"

  :dependencies [;;[leiningen-core "2.7.1"]
                 ;;[leiningen "2.7.1"]
                 [io.czlab/wabbit-shared "1.0.0"]]
  :target-path "target/%s"
  ;;:aot [leiningen.wabbit]

  :eval-in-leiningen true
  :global-vars {*warn-on-reflection* true})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;EOF
