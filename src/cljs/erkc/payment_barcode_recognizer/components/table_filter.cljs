(ns erkc.payment-barcode-recognizer.components.table-filter
  (:require [reagent.core :as r]))





(def filter-form (r/atom {
                          :account ""
                          :bill-id ""
                          :from-date ""
                          :to-date ""
                          }))

(defn filter-form-cursor [field]
  (r/cursor filter-form [field]))



(defn- filter-item [{:keys [item-key lable-text input-type]}]
  [:div {:style {:margin "5px" :display "flex"}}
   [:label {:for item-key  :style {:width "30%" :display "block"}} lable-text]
   [:input {
            :id item-key
            :type input-type
            :name item-key
            :value @(filter-form-cursor (keyword item-key))
            :on-change #(reset! (filter-form-cursor (keyword item-key)) (.-value (.-target %)))}]
   ])

(defn account-bill-filter-item-block []
  [:div {:style {:margin-right 10 :width "50%" }}
   (filter-item {:item-key "account" :lable-text "Account: " :input-type "text"})
   (filter-item {:item-key "bill-id" :lable-text "Bill: " :input-type "text"})
   ])

(defn date-period-filter-item-block []
  [:div {:style {:margin-right 10 :width "50%" }}
   (filter-item {:item-key "from-date" :lable-text "From date: " :input-type "date"})
   (filter-item {:item-key "to-date" :lable-text "To date: " :input-type "date"})
   ])

(defn filter-apply-button [filter-callback]
  [:input {:type "button"
           :value "Apply filter"
           :on-click #(filter-callback @filter-form)}])

(defn Table-Filter [filter-callback]
  [:div
   [:h2 "Filters"]
   [:p {:hidden true} (str @filter-form)]
   [:div {:style {:display "flex" :flex-wrap "nowrap" }}
     (account-bill-filter-item-block)
     (date-period-filter-item-block)
    ]
   [:div {:style {:display "flex" :justify-content "flex-end"}}
    (filter-apply-button filter-callback)
    ]
   ])
