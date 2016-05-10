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
  (fn [db [response component]]
    (assoc db :token (response :token)
           :current-bar :user-detail)))


