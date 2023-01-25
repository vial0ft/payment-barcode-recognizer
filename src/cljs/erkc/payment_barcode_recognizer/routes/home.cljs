(ns erkc.payment-barcode-recognizer.routes.home
  (:require
   [reagent.core :as r]
   [clojure.string :as str]
   [erkc.payment-barcode-recognizer.components.scanner :refer (Scanner)]
   [erkc.payment-barcode-recognizer.components.barcode-info :refer (LastBarcodeInfo)]
   [erkc.payment-barcode-recognizer.components.companies-table :refer (CompaniesTable)]
   [erkc.payment-barcode-recognizer.api :as api]
   [erkc.payment-barcode-recognizer.utils.datetime-utils :as dtu]
   [erkc.payment-barcode-recognizer.utils.big-decimal-utils :as bigdec]
   [erkc.payment-barcode-recognizer.utils.formats :as f]))

;; -------------------------
;; States

(def companies-table (r/atom {}))

(def last-recognized (r/atom {}))

;; -------------------------
;; Cursors

(defn company-cursor [companies-store company-group]
  (r/cursor companies-store [company-group]))

;; -------------------------
;; Helpers

(defn- add-barcode
  [companies-store {{:keys [group location]} :group-info {additional-amount :amount} :code-info}]
  (swap!
   (company-cursor companies-store {group location})
   (fn [{:keys [amount count]}]
     {:amount (bigdec/add amount additional-amount) :count (inc count)})))

(defn- replace-last-recognized-barcode [last-recognized-code-store recognized-barcode]
  (reset! last-recognized-code-store recognized-barcode))

(defn- confirmation-str [{code-info :code-info}]
  (str/join "\n" (map (fn [attr] (str (first attr) " : " (second attr))) (vec code-info))))

(defn- confirmed-barcode? [recognized-barcode]
  (js/confirm (confirmation-str recognized-barcode)))

(defn- map-barcode-record [record]
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

(defn- reduce-history-records [records]
  (reduce (fn [acc record]
            (let [{:keys [group-info code-info]} (map-barcode-record record)]
              (-> acc
                  (update-in [group-info :amount] bigdec/add (:amount code-info))
                  (update-in [group-info :count] inc))
              )) {} records))

;; ----------------------
;; Handlers

(defn- recognized-code-handler
  "Response handler with recognized bar-code"
  [last-recognized-code-store companies-store]
  (fn [resp]
    (let [recognized-barcode (map-barcode-record resp)]
      (when (confirmed-barcode? recognized-barcode)
        (do
          (replace-last-recognized-barcode last-recognized-code-store recognized-barcode)
          (add-barcode companies-store recognized-barcode))
        ))
      ))

(defn error-handler
  "Handler for error response"
  [err-resp] (js/alert err-resp))


(defn onScanSuccess
  "Handler for success scanning"
  [last-recognized-code-store companies-store]
  (fn [decodedText decodedResult]
    (api/recognize-code
     decodedText
     (recognized-code-handler last-recognized-code-store companies-store)
     error-handler)))


(defn- load-today-history! [companies-store]
  (api/get-today-history
   (-> (dtu/offset-date)
       (dtu/start-of-date)
       (dtu/to-date))

   (-> (dtu/offset-date)
       (dtu/add-days 1)
       (dtu/to-date))

   (fn [resp] (reset! companies-store (reduce-history-records resp)))

   error-handler))


;; -------------------------
;; Views

(defn home-page []
  (let [companies-store companies-table
        last-recognized-code-store last-recognized]
    (load-today-history! companies-store)
    (fn []
      [:div
       [:div {:style {:display "flex" :flex-wrap "nowrap" }}
        [:div {:style {:margin-right 10 :width "50%" }}
         [CompaniesTable @companies-store]]
        [:div {:style {:margin-left 10 :width "50%" }}
         [Scanner {
                   :fps 10
                   :qrbox {:width 150 :height 150}
                   :disableFlip false
                   :qrCodeSuccessCallback (onScanSuccess last-recognized-code-store companies-store)
                   }]]]
       [LastBarcodeInfo @last-recognized]])))
