(ns erkc.payment-barcode-recognizer.companies-table)

(defn- HeaderRow []
  [
   [:div.companies-table-grid-item "Company"]
   [:div.companies-table-grid-item "Count"]
   [:div.companies-table-grid-item "Amount"]])

(defn- TableRow [company count amount]
  [
   [:div.companies-table-grid-item company]
   [:div.companies-table-grid-item count]
   [:div.companies-table-grid-item amount]])


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
             company (str (first company-group))
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
    (-> [:div.companies-table-grid-container]
        (into (HeaderRow))
        (into companies-stat-rows)
        (into total-stat-row)
        )))
