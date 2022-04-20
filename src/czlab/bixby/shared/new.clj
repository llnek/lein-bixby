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

(ns czlab.bixby.shared.new

  (:require [czlab.bixby.shared.templates :as lein]
            [leiningen.core.main :as main]
            [clojure.pprint :as pp]
            [clojure.string :as cs]
            [clojure.java.io :as io])

  (:import [java.rmi.server UID]
           [java.util UUID]
           [java.util.concurrent.atomic AtomicInteger]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def
  ^:dynamic
  *template-name*
  (or "bixby"
      (last (cs/split (str *ns*) #"\."))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;left hand side is the target, right side is from the resource path -
;;from the resource files, generate a target dir structure.
(def
  ^:private
  template-files
  {"conf/app.conf" "app.conf"

   "etc" {"mime.properties" identity
          "log4j2c.xml" identity
          "log4j2d.xml" identity
          "Resources_en.properties" identity}

   "src/main/clojure/{{nested-dirs}}"
   {"core.clj" "src/{{app-type}}.clj"}

   "src/main/java/{{nested-dirs}}"
   {"Bonjour.java" "src/Bonjour.java"}

   "src/test/clojure/{{nested-dirs}}/test"
   {"test.clj" "src/test.clj"}

   ;;"src/test/java/{{nested-dirs}}/test"
   ;;{"ClojureJUnit.java" "src/ClojureJUnit.java"
   ;;"JUnit.java" "src/JUnit.java"}

   "public/res"
   {"favicon.png" "web/favicon.png"
    "favicon.ico" "web/favicon.ico"}

   "public/htm"
   {"index.html" "web/index.html"}

   "public/src"
   {"main.js" "web/main.js"}

   "public/css"
   {"main.css" "web/main.scss"}

   ".gitignore" "gitignore"

   "doc" {"intro.md" "intro.md"}

   "LICENSE" identity
   "project.clj" identity
   "README.md" identity

   "public" {}})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn- is-windows?
  []
  (cs/index-of
    (cs/lower-case (System/getProperty "os.name")) "windows"))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn- strip-ls
  "Remove leading slashes."
  [s]
  (cs/replace s #"^[/]+" ""))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn- juid
  "Generate a unique id."
  []
  (.replaceAll (str (UID.)) "[:\\-]+" ""))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defmacro
  ^:private explode-path

  "Break down a file path."
  [s] `(remove empty? (cs/split ~s #"/")))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defmacro
  ^:private sanitize-path

  "Clean up a file path."
  [s] `(cs/join "/" (explode-path ~s)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn- do-render

  "Render a file."
  [tfiles data]

  (letfn
    [(render [path data]
       (if (some #(cs/ends-with? path %)
                 [".png" ".ico" ".jpg" ".gif"])
         ((lein/raw-resourcer *template-name*) path)
         ((lein/renderer *template-name*) path data)))]
    (reduce #(if (string? %2)
               (conj %1 %2)
               (conj %1 [(first %2)
                         (render (last %2) data)])) [] tfiles)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn- traverse

  "Traverse the resource template."

  ([tfiles]
   (traverse "" tfiles))

  ([par tfiles]
   (reduce #(let [[k v] %2
                  kk (sanitize-path (str par "/" k))]
              (cond (map? v)
                    (if (empty? v)
                      (conj %1 kk)
                      (vec (concat %1 (traverse kk v))))
                    (fn? v)
                    (conj %1 [kk (v kk)])
                    (string? v)
                    (conj %1 [kk v])
                    :else
                    (throw (Exception.
                             (str "Unsupported resource: " k))))) [] tfiles)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn new<>

  "Leiningen template for creating a czlab/bixby application."
  [name options args]

  (let [{:keys [dir to-dir
                renderer-fn]} options
        proj (->> (-> name
                      (cs/includes? "/")
                      (if #"[/]+" #"[.]+"))
                  (cs/split name) last)
        _ (if-not (and proj
                       (> (count proj) 0))
            (throw (Exception. "Bad project name.")))
        main-ns (lein/sanitize-nsp name)
        uid (str (UUID/randomUUID))
        web? true
        data {:domain main-ns
              :project proj
              :encoding "UTF-8"
              :ver "0.0.1"
              :name name
              :year (lein/year)
              :date (lein/date)
              :title (format "Project %s." proj)
              :app-key (cs/replace uid #"-" "")
              :app-type (if web? "web" "server")
              :user (System/getProperty "user.name")
              :nested-dirs (lein/name->path main-ns)
              :description (format "Project %s: Home Page." proj)
              :h2dbpath (-> (cs/join "/"
                                     [(if-not (is-windows?)
                                        "/tmp"
                                        "/c:/windows/temp") (juid) proj])
                            (str ";AUTO_RECONNECT=TRUE"))}]
    ;(main/info (format "to-dir = %s" to-dir))
    ;(main/info (format "dir = %s" dir))
    (binding [lein/*renderer-fn* renderer-fn]
      (lein/x->files
        (assoc (dissoc options :renderer-fn)
               :dir
               (.getCanonicalPath (io/file dir proj)))
        data
        (-> (traverse "" template-files) (do-render data))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;EOF

