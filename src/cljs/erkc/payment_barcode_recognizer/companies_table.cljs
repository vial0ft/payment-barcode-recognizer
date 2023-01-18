(ns erkc.payment-barcode-recognizer.companies-table
  (:require [reagent.core :as r]))


(def grid-item-style {
                      :background-color "rgba(255, 255, 255, 0.8)"
                      :border "1px solid rgba(0, 0, 0, 0.8)"
                      :padding "10px"
                      :font-size "16px"
                      :font-weight "bold"
                      :text-align "center"
                      })


(def grid-item-company-style grid-item-style)

(def grid-style {
                 :display "grid"
                 :grid-template-columns "auto auto auto"
                 ;;:padding "10px"
                 })

(defn- HeaderRow []
        [
         [:div {:style grid-item-company-style} "Company"]
         [:div {:style grid-item-style} "Count"]
         [:div {:style grid-item-style} "Amount"]])

(defn- TableRow [company count amount]
  [
   [:div {:style grid-item-company-style} company]
   [:div {:style grid-item-style} count]
   [:div {:style grid-item-style} amount]])


(defn- TotalRow [{:keys [amount count]}]
  (TableRow "Total" count amount))


(defn transform-to-rows [companies]
  (loop [companies-groups companies
         acc []]
    (if-not companies-groups
      acc
      (recur
       (next companies-groups)
       (let [company-group (first companies-groups)
             company (first company-group)
             count (:count (second company-group))
             amount (:amount (second company-group))]
         (into acc (TableRow company count amount))
         )))
    ))

(defn total-stat [companies]
  (reduce (fn [acc {:keys [amount count]}]
            (-> acc
              (update :amount + amount)
              (update :count + count))) {:amount 0.0M :count 0} (vals companies)))

(defn CompaniesTable [companies]
  (let [companies-stat-rows  (transform-to-rows companies)
        total-stat-row (TotalRow (total-stat companies))]
    (-> [:div {:style grid-style}]
        (into (HeaderRow))
        (into companies-stat-rows)
        (into total-stat-row)
   )))
