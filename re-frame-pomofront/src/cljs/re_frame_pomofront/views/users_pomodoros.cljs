(ns re-frame-pomofront.views.users-pomodoros
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent :refer [atom]]
            [re-frame-pomofront.session :refer [GET POST]]))

(defn to-json [d]
  (.stringify js/JSON (clj->js d)))


(defn on-success [response]
 (re-frame/dispatch [:set-user response]))

(defn on-error [response]
  (js/console.log (to-json response)))


; month (.getMonth date)
; day (.getDay date)
; year (.getFullYear date)
; minutes (.getMinutes date)
; seconds (.getSeconds date)]

(defn pomo-list [items]
  [:ul
   (for [item items]
     (let [id (item :id)
           date (new js/Date (item :created_at))
           date-string (.toString date)]
       [:li {:key id :value id} date-string]))])

(defn your-stuff []
  (GET "/api/user/" nil on-success on-error)
  (let [user-data (re-frame/subscribe [:user-data])]
    (fn []
      [:div 
       [:h2 "Your pomodoros"]
       (if (empty? @user-data)
         [:div "Loading"]
         [pomo-list (@user-data :pomodoros)])
       [:h3 "Other stuff"]
       (to-json @user-data)])))

