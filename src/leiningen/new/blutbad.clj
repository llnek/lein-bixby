;; Copyright © 2013-2019, Kenneth Leung. All rights reserved.
;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file epl-v10.html at the root of this distribution.
;; By using this software in any fashion, you are agreeing to be bound by
;; the terms of this license.
;; You must not remove this notice, or any other, from this software.

(ns leiningen.new.blutbad

  (:require [czlab.blutbad.shared.new :as ws]
            [leiningen.core.main :as main]
            [clojure.java.io :as io]
            [stencil.core :as sc]
            [clojure.pprint :as pp]
            [clojure.string :as cs]
            [leiningen.new
             :refer [*use-snapshots?* *template-version*]]
            [leiningen.new.templates :refer [*dir* *force?*]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn blutbad

  "A lein template for creating a czlab/blutbad application."
  [name & args]

  ;(main/info (format "name=%s, args= %s" name args))
  (try
    (main/info
      (format "Generating new %s project..." ws/*template-name*))
    (ws/new<> name
              {:template-version *template-version*
               :use-snapshots? *use-snapshots?*
               :renderer-fn sc/render-string
               :force? *force?*
               :to-dir *dir*
               :dir (System/getProperty
                      "leiningen.original.pwd")} args)
    (catch Throwable t
      (.printStackTrace t)
      (main/abort (.getMessage t)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;EOF

