(ns erkc.payment-barcode-recognizer.utils.mappers
  (:require [erkc.payment-barcode-recognizer.utils.formats :as f]))


(defn filter-request-body [{:keys [account bill-id from-date to-date] :as filters}]
  {:account account
   :bill-id bill-id
   :period {:from-date from-date :to-date to-date}})


(defn map-barcode-record-for-home-page [record]
  {
   :group-info (str {
                     :group (:group record)
                     :location (:location record)
                     })
   :code-info {
               :account (:account record)
               :bill-id (:bill-id record)
               :amount (f/amount-map (:amount record))
               }
   :additional-info (:additional-info record)
   :created-at (:created-at record)
   })


(defn map-barcode-record-for-history-page [record]
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
