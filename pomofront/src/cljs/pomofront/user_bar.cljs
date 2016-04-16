(ns pomofront.user-bar
  (:require [pomofront.interactions :refer [get-interaction interactions]]))

(defn frp-thing []
      [:div "The count is:" @(get-interaction {:name "clicker" :init-val 0})
       [:div
       [:button {:on-click (interactions "clicker" inc)} "up"]
       [:button {:on-click (interactions "clicker" dec)} "down"]]])

