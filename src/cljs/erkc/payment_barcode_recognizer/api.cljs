(ns erkc.payment-barcode-recognizer.api
  (:require [ajax.core :refer [GET POST]]
            [erkc.payment-barcode-recognizer.utils.datetime-utils :as dtu]
            [erkc.payment-barcode-recognizer.utils.mappers :as m]))

(defn recognize-code
  "Send `raw-code` string like to recognizing"
  ([raw-code on-success] (recognize-code raw-code on-success nil))
  ([raw-code on-success on-error]
   (POST "/api/recognize" {:params {:code raw-code} ;; SEE: https://github.com/JulianBirch/cljs-ajax#getpostput
                           :format :json
                           :handler on-success
                           :error-handler on-error})))

(defn get-filtered-history
  "Fetch history of scanned barcodes by `filters`"
  [filters on-success on-error]
  (POST "/api/history/filter"{:params {:filter filters}
                              :format :json
                              :handler on-success
                              :error-handler on-error}))




(defn load-today-history
  "Fetch history of scanned barcodes for today only"
  [on-success on-error]
  (get-filtered-history
   (m/filter-request-body {:from-date (-> (dtu/offset-date)
                                          (dtu/to-date))})
   on-success
   on-error))
