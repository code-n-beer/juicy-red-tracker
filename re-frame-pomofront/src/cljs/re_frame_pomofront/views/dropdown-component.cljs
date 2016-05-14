(ns re-frame-pomofront.views.dropdown-component
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent :refer [atom]]
            ))

(defn dropdown [items selected]
    [:select
     (for [item @items]
       [:option {:value (item :id)} (item :name) ])])
