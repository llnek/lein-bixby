;; Copyright (c) 2013-2017, Kenneth Leung. All rights reserved.
;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file epl-v10.html at the root of this distribution.
;; By using this software in any fashion, you are agreeing to be bound by
;; the terms of this license.
;; You must not remove this notice, or any other, from this software.

(ns leiningen.new.wabbit

  (:require [leiningen.new.templates :refer [*dir* *force?*]]
            [leiningen.new
             :refer [*use-snapshots?*
                     *template-version*]]
            [czlab.wabbit.shared.new :as wa]
            [clojure.java.io :as io]
            [stencil.core :as sc]
            [clojure.pprint :as pp]
            [clojure.string :as cs]
            [leiningen.core.main :as main]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
(defn wabbit

  "A lein template for creating a czlab/wabbit application"
  [name & args]

  ;;(main/debug (str "args= " args))
  (try
    (main/info (format "Generating fresh 'lein new' %s project." wa/*template-name*))
    (let
      [options
       {:renderer-fn sc/render-string
        :dir (or *dir*
                 (-> (System/getProperty "leiningen.original.pwd")
                     (io/file name) .getPath))
        :force? *force?*
        :use-snapshots? *use-snapshots?*
        :template-version *template-version*}]
      (apply wa/new<> name options args))
    (catch Throwable t
      (.printStackTrace t)
      (main/abort (.getMessage t)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;EOF

