(ns erkc.payment-barcode-recognizer.components.history-table
  (:require [clojure.string :as str]
            [erkc.payment-barcode-recognizer.utils.big-decimal-utils :as bigdec]))



(defn- HistoryHeaderRow []
  [
   [:div.history-table-header-grid-item "Company"]
   [:div.history-table-header-grid-item "Account"]
   [:div.history-table-header-grid-item "Bill-id"]
   [:div.history-table-header-grid-item "Amount"]
   [:div.history-table-header-grid-item "Scanned At"]])

(defn- HistoryTableRow [{:keys [group-info account bill-id amount created-at]}]
    [
     [:div.history-table-grid-item group-info]
     [:div.history-table-grid-item account]
     [:div.history-table-grid-item bill-id]
     [:div.history-table-grid-item (if (or (nil? amount) (bigdec/equals? amount (bigdec/zero))) "-"
                                       (bigdec/pretty-value amount 3 " "))]
     [:div.history-table-grid-item (when created-at (.toLocaleString created-at))]])

(defn- transform-to-rows [records]
  (loop [records records
         acc []]
    (if-not records
      acc
        (recur (next records) (into acc (HistoryTableRow (first records))))
      )))


(defn HistoryTable [barcode-records]
  (let [history-rows (if (empty? barcode-records) []
                         (->> barcode-records
                             (transform-to-rows)))]
    [:div
     [:h2 "Scanned barcodes history"]
     (-> [:div.history-table-grid-container]
        (into (HistoryHeaderRow))
        (into history-rows)
        )]
    ))
