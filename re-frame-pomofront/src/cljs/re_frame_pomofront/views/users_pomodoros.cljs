(ns re-frame-pomofront.views.users-pomodoros
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent :refer [atom]]
            [re-frame-pomofront.session :refer [GET POST]]))

(defn to-json [d]
  (.stringify js/JSON (clj->js d)))

; month (.getMonth date)
; day (.getDay date)
; year (.getFullYear date)
; minutes (.getMinutes date)
; seconds (.getSeconds date)]

(defn task-pomodoros [pomodoros]
  [:ul
   (for [pomodoro pomodoros]
     (let [id (pomodoro :id)
           date (new js/Date (pomodoro :created_at))
           date-string (.toDateString date)
           clock-string (str (.getHours date) ":" (.getMinutes date))]
       [:li {:key id} date-string " " clock-string]))])

(defn task-list [tasks]
  [:div
   (for [task tasks]
     (let [id (task :id)
           task-name (task :name)]
       [:div {:key id}
        [:h4 task-name]
        [task-pomodoros (task :pomodoros)]]))])

(defn task-grouped-pomodoros []
  (let [tasks (re-frame/subscribe [:task-view])]
    (fn []
      [task-list @tasks])))

(defn your-stuff []
  [:div
   [:h2 "Your pomodoros"]
   [task-grouped-pomodoros]])
