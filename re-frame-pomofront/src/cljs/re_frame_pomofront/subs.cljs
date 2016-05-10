(ns re-frame-pomofront.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame]))

(re-frame/register-sub
 :name
 (fn [db]
   (reaction (:name @db))))

(re-frame/register-sub
 :active-panel
 (fn [db _]
   (reaction (:active-panel @db))))

(re-frame/register-sub
 :current-bar
 (fn [db _]
   (reaction (:current-bar @db))))

(re-frame/register-sub
 :token
 (fn [db _]
   (reaction (:token @db))))

(re-frame/register-sub
 :running-pomodoro
 (fn [db _]
   (reaction (:running-pomodoro @db))))

(re-frame/register-sub
 :user-data
 (fn [db _]
   (reaction (:user-data @db))))
