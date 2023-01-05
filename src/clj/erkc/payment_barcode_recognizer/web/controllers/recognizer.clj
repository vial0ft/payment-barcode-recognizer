(ns erkc.payment-barcode-recognizer.web.controllers.recognizer
  (:require [ring.util.http-response :as http-response]
            [clojure.tools.logging :as log]
            [erkc.payment-barcode-recognizer.recognize.core :as rec]

            [erkc.payment-barcode-recognizer.web.routes.utils :as utils]
            [clojure.java.io :as io]
            [clojure.edn :as edn]

            [cheshire.core :as json]
           ))

(def schema-file (io/file "./companies-schemas.edn"))
(defonce recognizing-schemas (edn/read (java.io.PushbackReader. (io/reader schema-file))))

(defn- store-barcode-info [query-fn {:keys [account bill-id amount]} parsing-info]
  (let [barcode-record {
                        :bill_id bill-id
                        :account account
                        :amount amount
                        :barcode_info (cheshire.core/encode parsing-info)
                        :created_at (java.time.LocalDateTime/now)}]
  (try
    (do
      (query-fn :add-barcode barcode-record)
      {:ok barcode-record})
    (catch Exception e {:error "Cant store barcode" :cause (.getMessage e)}))))

(defn recognize-code
  [req]
  (let [{{code :code} :body-params} req
        {:keys [query-fn]} (utils/route-data req)]
    (do
      (log/debug "code " code)
      (let [result (rec/code-processing code recognizing-schemas)
            _ (println result)]
        (if (not result) (http-response/bad-request! {:error "Cant recognize barcode" :input code})
            (let [{recognizing-result :result
                   parsing-info :parsing-info} result
                  storing-result (store-barcode-info query-fn (:code-info recognizing-result) parsing-info)]
              (cond
                    (contains? storing-result :error) (http-response/bad-request storing-result)
                    :else (http-response/ok recognizing-result))
            ))
      ))))


