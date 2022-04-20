;; Licensed under the Apache License, Version 2.0 (the "License");
;; you may not use this file except in compliance with the License.
;; You may obtain a copy of the License at
;;
;;     http://www.apache.org/licenses/LICENSE-2.0
;;
;; Unless required by applicable law or agreed to in writing, software
;; distributed under the License is distributed on an "AS IS" BASIS,
;; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
;; See the License for the specific language governing permissions and
;; limitations under the License.
;;
;; Copyright © 2013-2022, Kenneth Leung. All rights reserved.

(ns leiningen.new.bixby

  (:require [czlab.bixby.shared.new :as ws]
            [leiningen.core.main :as main]
            [clojure.java.io :as io]
            [stencil.core :as sc]
            [clojure.pprint :as pp]
            [clojure.string :as cs]
            [leiningen.new
             :refer [*use-snapshots?* *template-version*]]
            [leiningen.new.templates :refer [*dir* *force?*]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn bixby

  "A lein template for creating a czlab/bixby application."
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

