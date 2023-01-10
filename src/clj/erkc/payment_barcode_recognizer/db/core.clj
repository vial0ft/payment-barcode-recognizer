(ns erkc.payment-barcode-recognizer.db.core
  (:require [cheshire.core :as json]
            [clojure.tools.logging :as log]))



(defn store-barcode-info [query-fn {:keys [group location code-info] :as recognizing-info} parsing-info]
  (let [created-at (java.time.LocalDateTime/now)
        barcode-record (assoc code-info
                        :barcode-info (json/encode parsing-info)
                        :created-at created-at
                        :group (str group)
                        :location (str location))]
    (try
      (do
        (query-fn :add-barcode barcode-record)
        (assoc recognizing-info :created-at (str created-at)))
      (catch Exception e {:error "Cant store barcode" :cause (.getMessage e)}))))


(defn- query-by-filter [filter-type]
  (case (keyword filter-type)
    :period :get-barcodes-by-period
    :account :get-barcodes-by-account
    :bill :get-barcode-by-bill-id
    :else nil))

(defn keys-as-keywords [args-map]
  (reduce-kv (fn [m k v] (assoc m (keyword k) v)) {} args-map))

(defn fetch-history [query-fn filter-type args-map]
  (let [query-name (query-by-filter filter-type)]
    (try
      (query-fn query-name (keys-as-keywords args-map))
      (catch Exception e {
                          :error (format "Cant fetch history by filter %s params %s" filter-type args-map)
                          :cause (.getMessage e)})
      )))

