;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
(defproject wabbit/lein-template "0.2.0"

  :description "A leiningen template for creating a czlab/wabbit application"

  :license {:url "http://www.eclipse.org/legal/epl-v10.html"
            :name "Eclipse Public License"}
  :url "https://github.com/llnek/lein-wabbit"

  :dependencies [[io.czlab/wabbit-svcs "0.1.0"]]
  :target-path "out/%s"
  ;;cannot use aot!!!
  ;;:aot :all

  :global-vars {*warn-on-reflection* true}
  :eval-in-leiningen true)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;EOF
