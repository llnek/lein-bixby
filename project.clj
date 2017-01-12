;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
(defproject wabbit/lein-template "0.2.0"

  :description "A leiningen template for creating a czlab/wabbit application"

  :scm "https://github.com/llnek/lein-wabbit.git"
  :url "https://github.com/llnek/lein-wabbit"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  ;;{:deploy-repositories [["releases" :clojars]]}
  :dependencies [[io.czlab/wabbit-svcs "0.1.0"]]

  :global-vars {*warn-on-reflection* true}
  :eval-in-leiningen true)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;EOF
