(ns erkc.payment-barcode-recognizer.web.routes.api
  (:require
    [erkc.payment-barcode-recognizer.web.controllers.health :as health]
    [erkc.payment-barcode-recognizer.web.controllers.recognizer :as recognizer]
    [erkc.payment-barcode-recognizer.web.middleware.exception :as exception]
    [erkc.payment-barcode-recognizer.web.middleware.formats :as formats]
    [integrant.core :as ig]
    [reitit.coercion.malli :as malli]
    [reitit.ring.coercion :as coercion]
    [reitit.ring.middleware.muuntaja :as muuntaja]
    [reitit.ring.middleware.parameters :as parameters]
    [reitit.swagger :as swagger]
    [reitit.swagger-ui :as swagger-ui]))


;; TODO: Fix swagger https://cljdoc.org/d/metosin/reitit-schema/0.5.2/doc/ring/pluggable-coercion

;; Routes
(defn api-routes [opts]
  [["/swagger.json"
    {:get {:no-doc  true
           :swagger {:info {:title "Barcode recognizer API"}}
           :handler (swagger/create-swagger-handler)}}]
   ["/swagger-ui/*"
    {:get {:no-doc true
           :handler (swagger-ui/create-swagger-ui-handler {:url (str (:base-path opts) "/swagger.json") })}}]
   ["/health"
    {:get health/healthcheck!}]
   ["/recognize"
    {:post {:summary "recognize barcode"
            :parameters {:body {:code string?}}
            :description "Try recognize barcode in `code` field"
            :responses {200 {:body
                             {:group string?
                              :location string?
                              :created-at string?
                              :account int?
                              :bill-id int?
                              :amount decimal?}}}
            :handler recognizer/recognize-code}}]
   ["/history/filter"
    {:post {:summary "Fetch history"
            :parameters {:body {:filter {
                                         :account string?
                                         :bill-id string?
                                         :period {:from-date string? :to-date string?}
                                         }}}
            :description "Fetch history of scanned barcodes by `filter`"
            :responses {200 {:body list?}}
            :handler recognizer/fetch-history-with-filter}}]
   ])

(defn route-data
  [opts]
  (merge
    opts
    {:coercion   malli/coercion
     :muuntaja   formats/instance
     :swagger    {:id ::api}
     :middleware [;; query-params & form-params
                  parameters/parameters-middleware
                  ;; content-negotiation
                  muuntaja/format-negotiate-middleware
                  ;; encoding response body
                  muuntaja/format-response-middleware
                  ;; exception handling
                  coercion/coerce-exceptions-middleware
                  ;; decoding request body
                  muuntaja/format-request-middleware
                  ;; coercing response bodys
                  coercion/coerce-response-middleware
                  ;; coercing request parameters
                  coercion/coerce-request-middleware
                  ;; exception handling
                  exception/wrap-exception]}))

(derive :reitit.routes/api :reitit/routes)

(defmethod ig/init-key :reitit.routes/api
  [_ {:keys [base-path]
      :or   {base-path ""}
      :as   opts}]
  [base-path
   (route-data opts)
   (api-routes opts)])
