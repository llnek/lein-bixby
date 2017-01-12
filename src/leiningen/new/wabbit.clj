;; Copyright (c) 2013-2017, Kenneth Leung. All rights reserved.
;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file epl-v10.html at the root of this distribution.
;; By using this software in any fashion, you are agreeing to be bound by
;; the terms of this license.
;; You must not remove this notice, or any other, from this software.

(ns leiningen.new.wabbit

  (:import [java.util.concurrent.atomic AtomicInteger]
           [java.rmi.server UID]
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
            [clojure.pprint :as pp]
            [clojure.string :as cs]
            [czlab.wabbit.svcs.core :as sc]
            [leiningen.core.main :as main]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
(def ^:private template-name (last (cs/split (str *ns*) #"\.")))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
(def ^:private template-files
  {"conf/pod.conf" "pod.conf"
   "etc"
   {"log4j2c.xml" identity
    "log4j2d.xml" identity
    "shiro.ini" identity}
   "src/main/resources/{{nested-dirs}}/etc"
   {"mime.properties" "etc/mime.properties"
    "Resources_en.properties" "etc/Resources_en.properties"}
   "src/main/clojure/{{nested-dirs}}"
   {"core.clj" "src/soa.clj"}
   "src/main/java/{{nested-dirs}}"
   {"HelloWorld.java" "src/HelloWorld.java"}
   "src/test/clojure/{{nested-dirs}}/test"
   {"test.clj" "src/test.clj"}
   "src/test/java/{{nested-dirs}}/test"
   {"ClojureJUnit.java" "src/ClojureJUnit.java"
    "JUnit.java" "src/JUnit.java"}
   "src/web/media"
   {"favicon.png" "web/favicon.png"
    "favicon.ico" "web/favicon.ico"}
   "src/web/pages" {"index.html" "web/index.html"}
   "src/web/scripts" {"main.js" "web/main.js"}
   "src/web/styles" {"main.scss" "web/main.scss"}
   ".gitignore" "gitignore"
   "CHANGELOG.md" identity
   "doc" {"intro.md" "intro.md"}
   "LICENSE" identity
   "project.clj" identity
   "README.md" identity
   "public" {}})

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
(defn- doRender
  ""
  [tfiles data out]
  (doseq [t tfiles]
    (if (string? t)
      (swap! out conj t)
      (let [[k v] t]
        (swap! out conj [k (render v data)])))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
(defn- traverse
  ""
  [par tfiles out]
  (doseq [[k v] tfiles
          :let [kk (sanitizePath (str par "/" k))]]
    (cond
      (map? v)
      (if (empty? v)
        (swap! out conj kk)
        (traverse kk v out))
      (fn? v)
      (swap! out conj [kk (v kk)])
      (string? v)
      (swap! out conj [kk v]))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
(defn- checkSvcArgs
  ""
  [args]
  (let [types (sc/emittersByType)
        out (atom {})
        cnt (AtomicInteger.)]
    (doseq [a args
            :let [a' (cs/replace a #"^[\-]+" "")
                  k (keyword a')
                  v (types k)]
            :when (some? v)]
      (let [n (str (name k) (.incrementAndGet cnt))
            n' (keyword n)]
        (swap! out assoc n' (:conf v))))
    (let [s (with-out-str (pp/pprint @out))]
      ;;(println s)
      s)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
(defn wabbit
  "A lein template for creating a czlab/wabbit application"
  [name & args]
  (let
    [args (if (empty? args) ["-web"] args)
     main-ns (sanitize-ns name)
     pod (last (cs/split main-ns #"\."))
     svcstr (checkSvcArgs args)
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
           :io-services svcstr
           :h2dbpath h2dbUrl
           :domain main-ns
           :raw-name name
           :project pod
           :ver "0.1.0"
           :name name
           :year (year)
           :date (date)}
     out2 (atom [])
     out (atom [])]
    (main/info
      (format "Generating fresh 'lein new' %s project." template-name))
    (traverse "" template-files out)
    (doRender @out data out2)
    (apply ->files data @out2)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;EOF


