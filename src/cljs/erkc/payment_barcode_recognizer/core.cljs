(ns erkc.payment-barcode-recognizer.core
    (:require
      [reagent.core :as r]
      [reagent.dom :as d]
      [reitit.frontend :as rf]
      [reitit.frontend.easy :as rfe]
      [erkc.payment-barcode-recognizer.routes.home :refer (home-page)]
      [erkc.payment-barcode-recognizer.routes.history :refer (history-page)]))


(defonce match (r/atom nil))

(defn current-page []
  [:div
   [:div {:style {:display "flex" :flex-wrap "nowrap" :text-align "center"}}
    [:div {:style {:margin 10 :width "50%" }} [:a {:href (rfe/href ::home)} "Home page"]]
    [:div {:style {:margin 10 :width "50%" }} [:a {:href (rfe/href ::history)} "History"]]]
   (if @match
     (let [view (:view (:data @match))]
       [view @match]))])


(def routes
  [["/"
    {:name ::home
     :view home-page}]

   ["/history"
    {:name ::history
     :view history-page}]])

;; --------------------------
;; Initialize app

(defn ^:dev/after-load mount-root []

  (rfe/start!
   (rf/router routes)
   (fn [m] (reset! match m))
   ;; set to false to enable HistoryAPI
   {:use-fragment true})
  (d/render [current-page] (.getElementById js/document "app")))


(defn ^:export ^:dev/once init! []
  (mount-root))
