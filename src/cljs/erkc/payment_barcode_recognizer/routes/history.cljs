(ns erkc.payment-barcode-recognizer.routes.history
  (:require [reagent.core :as r]
            [erkc.payment-barcode-recognizer.api :as api]
            [erkc.payment-barcode-recognizer.utils.datetime-utils :as dtu]
            [erkc.payment-barcode-recognizer.utils.formats :as f]
            [erkc.payment-barcode-recognizer.components.history-table :refer (HistoryTable)]))


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

(defn error-handler
  "Handler for error response"
  [err-resp] (js/alert err-resp))


(defn- load-history! [store]
  (api/get-today-history
   (-> (dtu/offset-date)
       (dtu/to-date))

   (-> (dtu/offset-date)
       (dtu/add-days 1)
       (dtu/to-date))

   (fn [resp] (reset! store (map map-barcode-record resp)))

   error-handler))


(defn history-page []
  (let [history-table-store history-table-store]
    (load-history! history-table-store)
    (fn []
      [:div
       [HistoryTable @history-table-store]])))
