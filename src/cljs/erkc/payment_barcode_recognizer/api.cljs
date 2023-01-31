(ns erkc.payment-barcode-recognizer.api
  (:require [ajax.core :refer [GET POST]]
            [erkc.payment-barcode-recognizer.utils.datetime-utils :as dtu]))



(defn recognize-code
  ([raw-code on-success] (recognize-code raw-code on-success nil))
  ([raw-code on-success on-error]
   (POST "/api/recognize" {:params {:code raw-code} ;; SEE: https://github.com/JulianBirch/cljs-ajax#getpostput
                           :format :json
                           :handler on-success
                           :error-handler on-error})))


(defn get-today-history
  [from to on-success on-error]
    (GET "/api/history/period" {:params {:begin-date from :end-date to}
                                :handler on-success
                                :error-handler on-error}))

(defn get-filtered-history
  [filters on-success on-error]
  (POST "/api/history/filter"{:params {:filter filters}
                              :format :json
                              :handler on-success
                              :error-handler on-error}))
