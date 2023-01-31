(ns erkc.payment-barcode-recognizer.components.companies-table
  (:require
   [erkc.payment-barcode-recognizer.utils.big-decimal-utils :as bigdec]))

(defn- HeaderRow []
  [
   [:div.companies-table-header-grid-item "Company"]
   [:div.companies-table-header-grid-item "Count"]
   [:div.companies-table-header-grid-item "Amount"]])

(defn- TableRow [company count amount]
  [
   [:div.companies-table-grid-item (if (or (nil? company) (empty? company)) "-" company)]
   [:div.companies-table-grid-item (if (or (nil? count) (zero? count)) "-" count)]
   [:div.companies-table-grid-item (if (or (nil? amount) (bigdec/equals? amount (bigdec/zero))) "-"
                                     (bigdec/pretty-value amount 3 " "))]])


(defn- TotalRow [{:keys [amount count]}]
  (TableRow "Total" count amount))


(defn- transform-to-rows [companies]
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

(defn- total-stat [companies]
  (reduce (fn [acc {:keys [amount count]}]
            (-> acc
                (update :amount bigdec/add amount)
                (update :count + count)))
          {:amount (bigdec/zero) :count 0}
          (vals companies)))

(defn CompaniesTable [companies]
  (let [companies-stat-rows  (if (empty? companies) [] (transform-to-rows companies))
        total-stat-row (TotalRow (total-stat companies))]
    (-> [:div.companies-table-grid-container]
        (into (HeaderRow))
        (into companies-stat-rows)
        (into total-stat-row)
        )))
