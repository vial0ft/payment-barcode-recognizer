(ns erkc.payment-barcode-recognizer.web.controllers.recognizer
  (:require [ring.util.http-response :as http-response]
            [clojure.tools.logging :as log]
            [erkc.payment-barcode-recognizer.recognize.core :as rec]
            [erkc.payment-barcode-recognizer.db.core :as db]
            [erkc.payment-barcode-recognizer.web.routes.utils :as utils]
            [clojure.java.io :as io]
            [clojure.edn :as edn]
           ))

(def schema-file (io/file "./companies-schemas.edn"))
(defonce recognizing-schemas (edn/read (java.io.PushbackReader. (io/reader schema-file))))

(defn recognize-code
  [req]
  (let [{{code :code} :body-params} req
        {:keys [query-fn]} (utils/route-data req)]
    (do
      (log/debug "code " code)
      (let [recognizing-result (rec/code-processing code recognizing-schemas)]
        (if (not recognizing-result) (http-response/bad-request! {:error "Cant recognize barcode" :input code})
            (let [{:keys [result parsing-info]} recognizing-result
                  storing-result (db/store-barcode-info query-fn result parsing-info)]
              (cond
                    (contains? storing-result :error) (http-response/bad-request storing-result)
                    :else (http-response/ok result))
            ))
      ))))


