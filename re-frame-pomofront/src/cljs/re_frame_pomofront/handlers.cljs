(ns re-frame-pomofront.handlers
    (:require [re-frame.core :as re-frame]
              [re-frame-pomofront.db :as db]))

(re-frame/register-handler
 :initialize-db
 (fn  [_ _]
   db/default-db))

(re-frame/register-handler
 :set-active-panel
 (fn [db [_ active-panel]]
   (assoc db :active-panel active-panel)))


(re-frame/register-handler
  :login-success
  (fn [db [_ token component]]
    (js/console.log "handler token " token)
    (assoc db :token token 
           :current-bar :user-detail
           :active-panel :pomodoro-panel)))

(re-frame/register-handler
  :start-pomodoro
  (fn [db [_ name category time]]
    (js/console.log "name: " name)
    (assoc db :running-pomodoro {:name name :category category :time time :started (.getTime (js/Date.))})))


