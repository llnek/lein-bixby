(ns leiningen.new.wabbit
  (:require [leiningen.new.templates :refer [renderer year date project-name
                                             ->files sanitize-ns name-to-path
                                             raw-resourcer multi-segment]]
            [clojure.string :as cs]
            [leiningen.core.main :as main]))

(def ^:private template-name "wabbit")

(defn- render
  ""
  [path data]
  (if (some #(.endsWith ^String path ^String %)
            [".png" ".ico" ".jpg" ".gif"])
    ((raw-resourcer template-name) path)
    ((renderer template-name) path data)))

(def ^:private tfiles
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
   ".gitignore"
   "CHANGELOG.md"
   {"doc" ["intro.md"]}
   "LICENSE"
   "project.clj"
   "README.md"
   {"public" nil}])

(defn- stripLS "" [s] (cs/replace s #"^[/]+" ""))

(defn- yyy
  ""
  [par child data out]
  (cond
    (nil? child)
    (swap! out conj (stripLS par))
    (map? child)
    (doseq [[k v] child]
      (yyy (str par "/" k) v data out))
    (coll? child)
    (doseq [c child] (yyy par c data out))
    (string? child)
    (let [s (str par "/" child)
          s (stripLS s)
          s1 (cs/replace s "/_/" "/")
          s2 (cs/replace s "/_/" "/{{nested-dirs}}/")
          s1 (render s1 data)]
      (swap! out conj [s2 s1]))))


(defn wabbit
  "FIXME: write documentation"
  [name]
  (let [my-render (renderer "wabbit")
        main-ns (sanitize-ns name)
        data {:project (last (cs/split main-ns #"\."))
              :nested-dirs (name-to-path main-ns)
              :raw-name name
              :domain main-ns
              :app-key "111"
              :ver "0.1.0"
              :user "xxx"
              :auth-plugin ""
              :h2dbpath ""
              :sample-emitter ""
              :name name
              :year (year)
              :date (date)}
        out (atom [])]
    (main/info "Generating fresh 'lein new' wabbit project.")
    (yyy "" tfiles data out)
    (doseq [k @out
            :when false]
      (if (string? k)
        (println "yyyy = " k)
        (println "yyyy = " (first k))))
    (apply ->files data @out)))
    ;;(println "map =\n" (str data))))


