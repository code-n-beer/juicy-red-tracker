(ns re-frame-pomofront.pomodoro
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent :refer [atom]]
            [re-frame-pomofront.users-pomodoros :refer [old-pomodoros]]
            ))

(defn clj->json [d]
  (.stringify js/JSON (clj->js d)))

(defn start-pomodoro [name category length]
  (re-frame/dispatch [:start-pomodoro name category length]))
; (fn [db [name category time]]


(defn new-pomodoro []
  (let [length (atom 25)
        pomodoro-name (atom "")]
    (fn []
      [:div
       [:h4 "New pomodoro"]

       [:div "Length: "
        [:input {:type "text"
                 :value @length
                 :on-change #(reset! length (-> % .-target .-value))}]
        " minutes. "]

       [:div "Name: "
        [:input {:type "text"
                 :value @pomodoro-name
                 :on-change #(reset! pomodoro-name (-> % .-target .-value))}]]

       [:input {:type "button" :value "start" :on-click #(start-pomodoro @pomodoro-name "ses" @length)}]])))

(defn running-pomodoro []
  (let [running-pomodoro (re-frame/subscribe [:running-pomodoro])]
    (fn []
      [:div
       [:h4 "Running pomodoro"]
       (if (some? @running-pomodoro)
         [:div "has current pomo " @running-pomodoro])
       [:div ""]
       ])))

(defn pomodoro []
  [:div 
   [new-pomodoro]
   [running-pomodoro]
   [old-pomodoros]])
