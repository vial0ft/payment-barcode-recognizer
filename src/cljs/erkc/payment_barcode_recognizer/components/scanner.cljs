(ns erkc.payment-barcode-recognizer.components.scanner
  (:require
   [reagent.core :as reagent]
   [reagent.dom :as d]
   ["html5-qrcode" :refer (Html5QrcodeScanner)]))



(defonce qrcodeRegionId "html5qr-code-full-region")

(defn- create-config [props]
  (select-keys props [:fps :qrbox :aspectRatio :disableFlip]))

(defn Scanner [props]
    (reagent/create-class
     {
      :display-name "scanner-component"

      :component-did-mount (fn [this]
                             (let [
                                   config (create-config props)
                                   verbose (= (:verbose props) true)
                                   qrCodeSuccessCallback (:qrCodeSuccessCallback props)
                                   qrCodeErrorCallback (:qrCodeErrorCallback props)
                                   scanner (new Html5QrcodeScanner qrcodeRegionId config verbose)]
                                 (.render scanner qrCodeSuccessCallback qrCodeErrorCallback)))

      :component-will-unmount (fn [this]
                                (when-let [scanner (:scanner this)]
                                  (.clear scanner)))

      :reagent-render(fn []
                       [:div {:id qrcodeRegionId}])})
  )

