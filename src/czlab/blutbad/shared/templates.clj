;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Original: https://github.com/technomancy/leiningen/src/leiningen/new/templates.clj
;;
(ns czlab.blutbad.shared.templates

  (:require [clojure.string :as cs]
            [clojure.java.io :as io])

  (:import [java.util Calendar]
           [java.io BufferedReader File]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; can't set this to stencil.core/render-string here because
;; pulling in the stencil lib in this library will cause
;; classloading issues when used by lein-blutbad as a
;; leiningen template.
;; this function should return back a string if using stencil
(def ^:dynamic *renderer-fn* nil)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defmacro
  ^:private trap!
  [s] `(throw (Exception. (str ~s))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn- fix-line-seps
  [s]
  (cs/replace s
              "\n"
              (if (System/getenv "LEIN_NEW_UNIX_NEWLINES")
                "\n" (System/getProperty "line.separator"))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn- slurp->lf

  "Read file and sanitize line feeds."
  [^BufferedReader r]

  (let [sb (StringBuilder.)]
    (loop [s (.readLine r)]
      (when s
        (.append sb s)
        (.append sb "\n")
        (recur (.readLine r)))) (str sb)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn- slurp-resource

  "Read and sanitize a resource."
  [res]

  ; for 2.0.0 compatibility, can break in 3.0.0
  (-> (if (string? res)
        (io/resource res) res) io/reader slurp->lf fix-line-seps))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defmacro sanitize

  "Replace - with _."
  [s]

  `(clojure.string/replace ~s "-" "_"))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn name->path

  "Sanitize a file path."
  [s]

  (cs/replace (sanitize s) "." java.io.File/separator))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn sanitize-nsp

  "Sanitize a namespace."
  [s]

  (-> (cs/replace s "/" ".") (cs/replace "_" "-")))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn group-name

  "Get the group name."
  [s]

  (let [grpseq (butlast (cs/split (sanitize-nsp s) #"\."))]
    (if (seq grpseq)
      (->> grpseq (interpose ".") (apply str)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn year

  "Get current year."
  []

  (.get (Calendar/getInstance) Calendar/YEAR))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn date

  "Get current date as string."
  []

  (-> (java.text.SimpleDateFormat. "yyyy-MM-dd")
      (.format (java.util.Date.))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn- res-path

  "Get resource path."
  [path]

  (let
    [p (str "czlab/blutbad/shared/new/" path)] [p (io/resource p)]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn renderer

  "Get the render function."

  ([name]
   (renderer name nil))

  ([name render-fn]
   (let [render (or render-fn *renderer-fn*)]
     (fn [template & [data]]
       (let [template (if data
                        (render template data) template)
             [p r] (res-path template)]
         (if r
           (if (nil? data)
             (io/reader r)
             (render (slurp-resource r) data))
           (trap! (format "Resource '%s' not found" p))))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn raw-resourcer

  "Read a resource."
  [name]

  (fn [file]
    (let [[p r] (res-path file)]
      (if r
        (io/input-stream r)
        (trap! (format "Resource '%s' not found" p))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn- template-path

  "Get template file."
  ^File [name path data]

  (io/file name (*renderer-fn* path data)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn x->files

  "Generate output files from templates."
  [{:as cli-options
    :keys [dir force?]} {:as data :keys [name]} & paths]

  (if (or (.equals "." dir)
          (.mkdir (io/file dir)) force?)
    (doseq [path paths]
      (if (string? path)
        (.mkdirs (template-path dir path data))
        (let [[path content & options] path
              path (template-path dir path data)
              options (apply array-map options)]
          (.mkdirs (.getParentFile path))
          (io/copy content (io/file path))
          (when (:executable options)
            (.setExecutable path true)))))
    (trap! (str "Could not create directory "
                dir
                ". Maybe it already exists?"
                "  See also :force or --force"))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;EOF


