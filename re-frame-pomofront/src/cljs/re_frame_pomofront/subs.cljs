(ns re-frame-pomofront.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame]))

(defn to-json [d]
  (.stringify js/JSON (clj->js d)))

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

(re-frame/register-sub
  :categories
  (fn [db _]
    (reaction (get-in @db [:user-data :categories]))))

(re-frame/register-sub
  :tasks
  (fn [db _]
    (reaction (get-in @db [:user-data :tasks]))))

(re-frame/register-sub
  :user-name
  (fn [db _]
    (reaction (or
                (get-in @db [:user-data :email])
                "user"))))

(defn get-date[date-str]
  (let [js-date (new js/Date date-str)
        d-m-y (str (.getDay js-date) (.getMonth js-date) (.getFullYear js-date))]
    d-m-y))

(re-frame/register-sub
  :task-view
  (fn [db _]
    (let [pomodoros (reaction (get-in @db [:user-data :pomodoros]))
          grouped-pomos (reaction (group-by :task_id @pomodoros))
          tasks (reaction (@db :tasks))
          pomos-with-names (reaction (mapv #(assoc % :pomodoros (@grouped-pomos (% :id))) @tasks))]
      (reaction (reverse @pomos-with-names)))))

