(ns erkc.payment-barcode-recognizer.db.core
  (:require [cheshire.core :as json]
            [clojure.tools.logging :as log]
            [clojure.string :as str]))


(defn- filter-fields [record]
    (select-keys record [:group :location :created-at :account :bill-id :amount]))

(defn store-barcode-info [query-fn recognizing-info parsing-info]
  (let [{{:keys [group location]} :group-info code-info :code-info} recognizing-info
        created-at (java.time.LocalDateTime/now)
        barcode-record (assoc code-info
                        :barcode-info (json/encode parsing-info)
                        :created-at created-at
                        :group (str group)
                        :location (str location))]
    (try
      (do
        (query-fn :add-barcode barcode-record)
        ;;(assoc recognizing-info :created-at (str created-at))
        (filter-fields barcode-record))
      (catch Exception e {:error "Cant store barcode" :cause (.getMessage e)}))))


(defn- query-by-filter [filter-type]
  (case (keyword filter-type)
    :period :get-barcodes-by-period
    :account :get-barcodes-by-account
    :bill :get-barcode-by-bill-id
    :filter :get-barcodes-by-predicate
    :else nil))


(defn- build-condition [[key value]]
  (case key
    :account (if-not (empty? value) (format "account::text like '%%%s%%'" value) "true")
    :bill-id (if-not (empty? value) (format "bill_id::text like '%%%s%%'" value) "true")
    :period (let [{:keys [from-date to-date]} value]
              (if-not (empty? from-date)
                (format
                 "created_at between '%s' and '%s'"
                 from-date
                 (if-not (empty? to-date)
                   to-date
                   (.toString (java.time.LocalDate/now))))
                "true"))
    :else "true"))


(defn- keys-as-keywords [args-map]
  (reduce-kv (fn [m k v] (assoc m (keyword k) v)) {} args-map))

(defmulti barcode-query-condition (fn [query-type _args] query-type))

(defmethod barcode-query-condition :get-barcodes-by-predicate [query-type args]
  {:predicate (str/join " AND " (->> (keys-as-keywords args)
                                     (map (fn [el] [(first el) (second el)]))
                                     (map build-condition)))})

(defmethod barcode-query-condition :default  [query-type args]
  (keys-as-keywords args))

(defn fetch-history [query-fn filter-type args-map]
  (let [query-name (query-by-filter filter-type)
        query-args (barcode-query-condition query-name args-map)]
    (try
      (query-fn query-name query-args)
      (catch Exception e {
                          :error (format "Cant fetch history by filter %s params %s" filter-type args-map)
                          :cause (.getMessage e)})
      )))

