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

(defn get-date[date-str]
  (let [js-date (new js/Date date-str)
        d-m-y (str (.getDate js-date) "." (.getMonth js-date) "." (.getFullYear js-date))]
    d-m-y))

(defn get-clock [date]
  (let [hours (.getHours date)
        minutes (.getMinutes date)
        hours-format (str (if (> 10 hours) "0" "") hours)
        minutes-format (str (if (> 10 minutes) "0" "") minutes)
        time (str hours-format ":" minutes-format)]
    time))

(defn task-date-pomodoros [pomodoros]
 [:div
   (for [date pomodoros]
     (let [datestr (first date)
           pomos (second date)
           num (count pomos)]
     [:div {:key datestr}
      [:h4.day-header [:i datestr]]
      [:div.pomodoros-for-day {:key datestr}
       [:ul
        (for [pomodoro pomos]
          (let [id (pomodoro :id)
                date (new js/Date (pomodoro :created_at))
                time (get-clock date)]
            [:li {:key id} time]))]
       [:strong "= " num]]]))])

(defn benis-list [tasks]
  [:div
   (for [task tasks]
     (let [id (task :id)
           task-name (task :name)
           pomo-count (count (task :pomodoros))
           date-grouped-pomos (reverse (group-by #(get-date (% :created_at)) (task :pomodoros)))]
       [:div {:key id}
        [:div.task-header task-name]
        [:div.task
         [task-date-pomodoros date-grouped-pomos]
         [:p pomo-count " pomodoros for " [:strong task-name]]]]))])

(defn date-grouped-pomos []
  (let [tasks (re-frame/subscribe [:task-view])]
    (fn []
      [benis-list @tasks])))

(defn your-stuff []
  [:div
   [:h2 "Your old pomodoros"]
   [date-grouped-pomos]])

