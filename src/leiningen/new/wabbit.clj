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
;; Copyright (c) 2017, Kenneth Leung. All rights reserved.

(ns leiningen.new.wabbit

  (:import [java.rmi.server UID]
           [java.util UUID])

  (:require [leiningen.new.templates
             :refer [renderer
                     year
                     date
                     project-name
                     ->files
                     sanitize-ns
                     name-to-path
                     raw-resourcer
                     multi-segment]]
            [clojure.string :as cs]
            [leiningen.core.main :as main]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
(def ^:private template-name "wabbit")
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
(def ^:private template-files
  [{"conf" ["pod.conf"]}
   {"etc" ["log4j2c.xml" "log4j2d.xml" "shiro.ini"]}
   {"src/main"
    [{"resources" ["mime.properties" "Resources_en.properties"]}
     {"clojure" [{"_" ["core.clj"]}]}
     {"java" [{"_" ["HelloWorld.java"]}]}]}
   {"src/test"
    [{"clojure/test" ["test.clj"]}
     {"java/test" ["ClojureJUnit.java" "JUnit.java"]}]}
   {"src/web"
    [{"media" ["favicon.png"]}
     {"pages" ["index.html"]}
     {"scripts" ["main.js"]}
     {"styles" ["main.scss"]}]}
   "gitignore"
   "CHANGELOG.md"
   {"doc" ["intro.md"]}
   "LICENSE"
   "project.clj"
   "README.md"
   {"public" nil}])
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
(def ^:private xref-files
  {"gitignore" ".gitignore"})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
(defn- isWindows?
  "Is platform Windows?" []
  (>= (.indexOf (cs/lower-case (System/getProperty "os.name")) "windows") 0))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
(defn- stripLS "" [s] (cs/replace s #"^[/]+" ""))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
(defn- juid
  ""
  []
  (.replaceAll (str (UID.)) "[:\\-]+" ""))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
(defn- explodePath "" [s]
  (->>
    (cs/split s #"/")
    (remove #(or (nil? %)
                 (== 0 (.length %))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
(defn- sanitizePath "" [s]
  (cs/join "/" (explodePath s)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
(defn- sanitizeTarget
  ""
  [des]
  (let [tkns (explodePath des)
        n (last tkns)
        n (or (xref-files n) n)]
    (cs/join
      "/"
      (conj (vec (drop-last tkns)) n))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
(defn- render
  ""
  [path data]
  (let []
    (if (some #(.endsWith ^String path ^String %)
              [".png" ".ico" ".jpg" ".gif"])
      ((raw-resourcer template-name) path)
      ((renderer template-name) path data))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
(defn- traverse
  ""
  [par child data out]
  (cond
    (nil? child)
    (swap! out conj (sanitizePath par))
    (map? child)
    (doseq [[k v] child]
      (traverse (str par "/" k) v data out))
    (coll? child)
    (doseq [c child] (traverse par c data out))
    (string? child)
    (let [s (str par "/" child)
          s1 (cs/replace s "/_/" "/")
          s2 (cs/replace s "/_/" "/{{nested-dirs}}/")]
      (swap! out conj [(sanitizeTarget s2)
                       (render s1 data)]))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
(defn wabbit
  "FIXME: write documentation"
  [name]
  (let
    [main-ns (sanitize-ns name)
     pod (last (cs/split main-ns #"\."))
     h2dbUrl (->
               (cs/join "/"
                        [(if (isWindows?)
                           "/c:/temp" "/tmp")
                         (juid)
                         pod])
               (str ";MVCC=TRUE;AUTO_RECONNECT=TRUE"))
     data {:user (System/getProperty "user.name")
           :nested-dirs (name-to-path main-ns)
           :app-key (str (UUID/randomUUID))
           :h2dbpath h2dbUrl
           :domain main-ns
           :raw-name name
           :project pod
           :ver "0.1.0"
           :name name
           :year (year)
           :date (date)}
     out (atom [])]
    (main/info
      (format "Generating fresh 'lein new' %s project." template-name))
    (traverse "" template-files data out)
    (apply ->files data @out)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;EOF


