(ns re-frame-pomofront.views.dropdown-component
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent :refer [atom]]
            ))

(defn dropdown [items selected]
  (if (nil? @selected)
    (reset! selected (:id (first items))))
  (fn [items selected]
    [:select {:on-change #(reset! selected (js/parseInt (-> % .-target .-value)))}
    ; [:select {:on-change #(js/console.log "hhhhh "(-> % .-target .-value))}
     (for [item items]
       [:option {:key (item :id) :value (item :id)} (item :name) ])]))
