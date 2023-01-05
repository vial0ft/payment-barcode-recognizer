(ns erkc.payment-barcode-recognizer.db.core
  (:require [cheshire.core :as json]
            [clojure.tools.logging :as log]))



(defn store-barcode-info [query-fn {:keys [group location code-info]} parsing-info]
  (let [_ (log/debug code-info)
        barcode-record (assoc code-info
                        :barcode-info (json/encode parsing-info)
                        :created-at (java.time.LocalDateTime/now)
                        :group (str group)
                        :location (str location))]
    (try
      (do
        (query-fn :add-barcode barcode-record)
        {:ok barcode-record})
      (catch Exception e {:error "Cant store barcode" :cause (.getMessage e)}))))

