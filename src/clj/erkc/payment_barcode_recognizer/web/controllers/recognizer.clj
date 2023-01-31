(ns erkc.payment-barcode-recognizer.web.controllers.recognizer
  (:require [ring.util.http-response :as http-response]
            [clojure.tools.logging :as log]
            [erkc.payment-barcode-recognizer.recognize.core :as rec]
            [erkc.payment-barcode-recognizer.db.core :as db]
            [erkc.payment-barcode-recognizer.web.routes.utils :as utils]
            [clojure.java.io :as io]
            [clojure.edn :as edn]
           ))

(def schema-file (io/resource "companies-schemas.edn"))
(defonce recognizing-schemas (edn/read (java.io.PushbackReader. (io/reader schema-file))))

(defn- struct-history-record [record]
  {
   :group-info {
                :group (:group record)
                :location (:location record)
                }
   :code-info {
               :account (:account record)
               :bill-id (:bill-id record)
               :amount (:amount record)
               }
   :created-at (:created-at record)
   })


(defn struct-history-result [records]
  (->> records
       (map struct-history-record)))

(defn- http-result [result]
  ((if (contains? result :error) http-response/bad-request
    http-response/ok) result))

(defn recognize-code
  [req]
  (let [{{code :code} :body-params} req
        {:keys [query-fn]} (utils/route-data req)]
    (do
      (log/debug "code " code)
      (let [recognizing-result (rec/code-processing code recognizing-schemas)]
        (if (not recognizing-result) (http-response/bad-request! {:error "Cant recognize barcode" :input code})
            (let [{:keys [result parsing-info]} recognizing-result
                  storing-result (db/store-barcode-info query-fn result parsing-info)
                  _ (log/debug storing-result)]
              (http-result storing-result)
              ))
        ))))


(defn fetch-history
  [req]
  (let [filter-type (get-in req [:path-params :filter-type])
        path-params (:query-params req)
        _ (log/debug path-params)
        {:keys [query-fn]} (utils/route-data req)
        result (db/fetch-history query-fn filter-type path-params)
        _ (log/debug result)]
    (http-result result)))


(defn fetch-history-with-filter
  [req]
  (let [filter-type (get-in req [:path-params :filter-type])
        filter (get-in req [:body-params :filter])
        {:keys [query-fn]} (utils/route-data req)
        result (db/fetch-history query-fn filter-type filter)]
       (http-result result)))


