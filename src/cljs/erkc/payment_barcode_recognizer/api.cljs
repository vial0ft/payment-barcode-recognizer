(ns erkc.payment-barcode-recognizer.api
  (:require [ajax.core :refer [GET POST]]))



(defn recognize-code
  ([raw-code on-success] (recognize-code raw-code on-success nil))
  ([raw-code on-success on-error]
   (POST "/api/recognize" {
                           ;; SEE: https://github.com/JulianBirch/cljs-ajax#getpostput
                           :params {:code raw-code} ;; WTF ???? why PARAMS ???!!! why you couldn't do JUST :body ????!!!!
                           :format :json
                           :handler on-success
                           :error-handler on-error})))


(defn get-today-history
  [from to on-success on-error]
    (GET "/api/history/period" {:params {:begin-date from :end-date to}
                                :handler on-success
                                :error-handler on-error}))
