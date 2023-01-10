(ns erkc.payment-barcode-recognizer.core
    (:require
      [reagent.core :as r]
      [reagent.dom :as d]
      [erkc.payment-barcode-recognizer.scanner :as scanner]
      [ajax.core :refer [GET POST]]))



(defonce scanned (r/atom {}))

(defn handler [resp]
  (.log js/console (str resp)))

(defn error-handler [{:keys [status status-text] :as err-resp}]
  (.log js/console (str "something bad happened: " status " " status-text " " err-resp)))

(defn onScanSuccess [decodedText decodedResult]
  (do
    (.log js/console (str decodedText))
    (.log js/console (str decodedResult))
    (let [body {"code" decodedText}
          body-json (.stringify js/JSON (clj->js {:code decodedText}))]
      (POST "/api/recognize" {
                              ;; SEE: https://github.com/JulianBirch/cljs-ajax#getpostput 
                              :params body ;; WTF ???? why PARAMS ???!!! why you couldn't do JUST :body ????!!!!
                              :format :json
                              :handler handler
                              :error-handler error-handler}))))

(defn onScanFailure [error]
  (.warn js/console  error))

;; -------------------------
;; Views


(defn doPrint [result err]
  (do
    (if (and (not (nil? result)) (not (undefined? result))) (.log js/console result))
    ;;(if (and (not (nil? err)) (not (undefined? err))) (.warn js/console err))
    ))


(defn home-page []
  [:div
   [:h2 "Welcome to Reagent!"]
   [:div {:style {:margin-left 50 :width "50%"}}
    [scanner/scanner-component {
                                :fps 10
                                :qrbox {:width 150 :height 150}
                                :disableFlip false
                                :qrCodeSuccessCallback onScanSuccess
                                :qrCodeErrorCallback onScanFailure
                                }
    ]]])

;; -------------------------
;; Initialize app

(defn ^:dev/after-load mount-root []
  (d/render [home-page] (.getElementById js/document "app")))


(defn ^:export ^:dev/once init! []
  (mount-root))
