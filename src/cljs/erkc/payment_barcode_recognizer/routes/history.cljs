(ns erkc.payment-barcode-recognizer.routes.history
  (:require [reagent.core :as r]
            [clojure.string :as str]
            [erkc.payment-barcode-recognizer.api :as api]
            [erkc.payment-barcode-recognizer.utils.datetime-utils :as dtu]
            [erkc.payment-barcode-recognizer.utils.formats :as f]
            [erkc.payment-barcode-recognizer.components.history-table :refer (HistoryTable)]
            [erkc.payment-barcode-recognizer.components.table-filter :refer (Table-Filter)]))


;; -------------------------
;; States

(def history-table-store (r/atom {}))

(defn- map-barcode-record [record]
  {
   :group-info (str {
                     :group (:group record)
                     :location (:location record)
                     })
   :account (:account record)
   :bill-id (:bill-id record)
   :amount (f/amount-map (:amount record))
   :additional-info (:additional-info record)
   :created-at (:created-at record)
   })

(defn- filter-request-body [{:keys [account bill-id from-date to-date] :as filters}]
  {:account account
   :bill-id bill-id
   :period {:from-date from-date :to-date to-date}}
  )

(defn error-handler
  "Handler for error response"
  [err-resp] (js/alert err-resp))


(defn handle-history-responce [store]
  (fn [resp] (reset! store (map map-barcode-record resp))))


(defn- load-history! [store]
  (api/get-today-history
   (-> (dtu/offset-date)
       (dtu/to-date))

   (-> (dtu/offset-date)
       (dtu/add-days 1)
       (dtu/to-date))

   (handle-history-responce store)

   error-handler))


(defn filter-callback [history-store]
  (fn [{:keys [account bill-id from-date to-date] :as filters}]
    (api/get-filtered-history
     (filter-request-body filters)
     (handle-history-responce history-store)
     error-handler
     ))) ;; TODO: fetch history by filters


(defn history-page []
  (let [history-table-store history-table-store]
    (load-history! history-table-store)
    (fn []
      [:div
       [Table-Filter (filter-callback history-table-store)]
       [HistoryTable @history-table-store]])))
