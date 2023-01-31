(ns erkc.payment-barcode-recognizer.routes.history
  (:require [reagent.core :as r]
            [erkc.payment-barcode-recognizer.api :as api]
            [erkc.payment-barcode-recognizer.components.history-table :refer (HistoryTable)]
            [erkc.payment-barcode-recognizer.components.table-filter :refer (Table-Filter)]
            [erkc.payment-barcode-recognizer.utils.mappers :as m]))


;; -------------------------
;; States

(def history-table-store (r/atom {}))



;; ----------------------
;; Handlers

(defn- error-handler
  "Handler for error response"
  [err-resp] (js/alert err-resp))


(defn- handle-history-responce
  "Handler for success fetching of history"
  [store]
  (fn [resp] (reset! store (map m/map-barcode-record-for-history-page resp))))


(defn- load-today-history
  "Fetch today info when page is loading"
  [store]
  (api/load-today-history
   (handle-history-responce store)
   error-handler))


(defn- filter-callback
  "Fetch history by filter"
  [history-store]
  (fn [{:keys [account bill-id from-date to-date] :as filters}]
    (api/get-filtered-history

     (m/filter-request-body filters)

     (handle-history-responce history-store)

     error-handler
     )))

;; -------------------------
;; Views

(defn history-page []
  (let [history-table-store history-table-store]
    (load-today-history history-table-store)
    (fn []
      [:div
       [Table-Filter (filter-callback history-table-store)]
       [HistoryTable @history-table-store]])))
