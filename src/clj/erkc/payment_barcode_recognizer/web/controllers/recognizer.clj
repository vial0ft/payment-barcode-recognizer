(ns erkc.payment-barcode-recognizer.web.controllers.recognizer
  (:require [ring.util.http-response :as http-response]
            [clojure.tools.logging :as log]
            [erkc.payment-barcode-recognizer.recognize.core :as rec]))

(defn recognize-code
  [req]
  (let [{body-params :body-params} req
        {code :code} body-params]
    (do
      (log/debug "code " code)
      (-> (http-response/ok (rec/code-processing code))
          (http-response/header "Content-type" "application/json")))
    ))


