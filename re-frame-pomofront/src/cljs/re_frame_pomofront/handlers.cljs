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


(defn counter [length started]
  (let [now (.getTime (js/Date.))
        length-num (js/parseInt length)
        length-milliseconds (* length-num 60 1000)
        result-milliseconds (- (+ length-milliseconds started) now)
        result-seconds-f (/ result-milliseconds 1000)
        result-seconds (js/Math.floor result-seconds-f)
        result-seconds-mod (mod result-seconds 60)
        result-minutes-f (/ result-seconds 60)
        result-minutes (js/Math.floor result-minutes-f)]
    {:min result-minutes :sec result-seconds-mod}))

(re-frame/register-handler
  :start-pomodoro
  (fn [db [_ length task]]
    (if (db :running-pomodoro)
      (js/clearInterval (:timer (db :running-pomodoro))))
    (let [init-val {:min 999 :sec 0}
          start (.getTime (js/Date.))
          timer (js/setInterval (fn [] 
                                  (let [time-left (counter length start)]
                                    (re-frame/dispatch [:update-clock time-left])))
                                1000)]
      (assoc db :running-pomodoro 
             {:task task
              :timer timer
              :length length
              :time-left init-val}))))

(re-frame/register-handler
  :update-clock
  (fn [db [_ time-remaining]]
    (assoc-in db [:running-pomodoro :time-left] time-remaining)))

(re-frame/register-handler
  :stop-pomodoro
  (fn [db _]
    (if (db :running-pomodoro)
      (js/clearInterval (:timer (db :running-pomodoro))))
    (assoc db :running-pomodoro nil)))

(re-frame/register-handler
  :pause-pomodoro
  (fn [db _]
    (if (db :running-pomodoro)
      (js/clearInterval (:timer (db :running-pomodoro))))
    db))


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

(re-frame/register-handler
  :update-tasks
  (fn [db [_ what]]
    (assoc-in db [:user-data :tasks] what)))
