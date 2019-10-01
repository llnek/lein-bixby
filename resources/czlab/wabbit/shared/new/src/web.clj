;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;auto-generated

(ns ^{:doc ""
      :author "{{user}}"}

  {{domain}}.core

  (:require [czlab.wabbit.xpis :as xp]
            [czlab.niou.core :as cc]
            [czlab.basal.core :as c]
            [czlab.basal.xpis :as po]
            [czlab.wabbit.plugs.mvc :as mvc])

  (:import [java.io File]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn- ftl-context
  []
  {:landing
             {:title_line "Sample Web App"
              :title_2 "Demo wabbit"
              :tagline "Say something" }
   :about
             {:title "About wabbit demo" }
   :services {}
   :contact {:email "a@b.com"}
   :description "wabbit web app"
   :encoding "utf-8"
   :title "wabbit|Sample"})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn dft-handler
  [evt res]
  (c/do-with
    [ch (:socket evt)]
    (let
      [plug (xp/get-pluglet evt)
       svr (po/parent plug)
       ri (get-in evt
                  [:route :info])
       tpl (:template ri)
       {:keys [data ctype]}
       (if (c/hgl? tpl)
         (mvc/load-template plug tpl (ftl-context)))]
      (->>
        (-> (cc/res-header-set res "content-type" ctype)
            (assoc :body data))
        cc/reply-result ))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn app-main
  [svr]
  (println  "My AppMain called!"))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;EOF

