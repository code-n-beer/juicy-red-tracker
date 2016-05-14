(ns re-frame-pomofront.handlers
    (:require [re-frame.core :as re-frame]
              [re-frame-pomofront.db :as db]))

(defn to-json [d]
  (.stringify js/JSON (clj->js d)))

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
    (assoc db :token token 
           :current-bar :user-detail
           :active-panel :pomodoro-panel)))

(re-frame/register-handler
  :start-pomodoro
  (fn [db [_ length task-id]]
    (assoc db :running-pomodoro {:length length :task-id task-id :started (.getTime (js/Date.))})))

(re-frame/register-handler
  :stop-pomodoro
  (fn [db _]
    (assoc db :running-pomodoro nil)))


(re-frame/register-handler
  :set-user
  (fn [db [_ response]]
    (assoc db :user-data response)))

(re-frame/register-handler
  :update-user
  (fn [db [_ where what]]
    (update-in db (cons :user-data where) what)))

(re-frame/register-handler
  :update-categories
  (fn [db [_ what]]
    (assoc-in db [:user-data :categories] what)))
