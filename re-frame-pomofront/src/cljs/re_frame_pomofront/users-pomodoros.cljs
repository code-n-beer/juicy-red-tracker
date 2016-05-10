(ns re-frame-pomofront.users-pomodoros
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET POST]]))

(defn old-pomodoros []
  [:div 
   [:h2 "Your pomodoros"]
   "placeholder user data"])
