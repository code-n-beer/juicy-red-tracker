(ns re-frame-pomofront.pomodoro
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent :refer [atom]]
            [re-frame-pomofront.users-pomodoros :refer [old-pomodoros]]
            [re-frame-pomofront.session :refer [GET POST]]
            ))

;; -------------------------- convenience funcs 
(defn to-json [d]
  (.stringify js/JSON (clj->js d)))

(defn on-error [response]
  (js/console.log (to-json response)))

(defn text-input [data-atom]
  [:input {:type "text" :value @data-atom :on-change #(reset! data-atom (-> % .-target .-value))}])

(defn get-user []
  (GET "/api/user/" nil #(re-frame/dispatch [:set-user %]) on-error))


;; -------------------------- new pomodoros
(defn start-pomodoro [length task-id]
  (re-frame/dispatch [:start-pomodoro length task-id]))

(defn new-pomodoro []
  (let [length (atom 25)
        task-id (atom "")]
    (fn []
      [:div
       [:h4 "New pomodoro"]
       [:div "Length: " [text-input length] " minutes. "]
       [:div "Parent task id:" [text-input task-id]]
       [:input {:type "button" :value "start" :on-click #(start-pomodoro @length @task-id)}]])))


;; ------------------------- currently running pomodoro 
(defn post-pomodoro [task-id length]
  (POST (str "/api/user/task/" task-id "/pomodoro") {:minutes length :success true} #(get-user) on-error)
  (re-frame/dispatch [:stop-pomodoro]))

(defn running-pomodoro []
  (let [running-pomodoro (re-frame/subscribe [:running-pomodoro])]
    (fn []
      [:div
       [:h4 "Running pomodoro"]
       (if (some? @running-pomodoro)
         [:div "running! " (to-json @running-pomodoro)
          [:div 
           [:input {:type "button"
                    :value "finish"
                    :on-click #(post-pomodoro (@running-pomodoro :task-id) (@running-pomodoro :length))}]]]
         [:div "not running"])])))


;; -------------------------- categories 
(defn post-category [cat-name]
  (POST "/api/user/category/" {:name cat-name} #(get-user) on-error))

(defn new-category []
  (let [category-name (atom "")]
    (fn []
      [:div
       [:h4 "New category"]
       [:div "Name: " 
        [text-input category-name]]
       [:input {:type "button" :value "create" :on-click #(post-category @category-name)}]])))


;; ------------------------- tasks 
(defn post-task [task-name cat-id]
  (POST (str "/api/user/category/" cat-id "/task") {:name task-name} #(get-user) on-error))

(defn new-task []
  (let [task-name (atom "")
        category-id (atom "")]
    (fn []
      [:div
       [:h4 "New task"]
       [:div 
        "Category id: "
        [text-input category-id]
        "Task name: "
        [text-input task-name]]
       [:input {:type "button" :value "create" :on-click #(post-task @task-name @category-id)}]])))


(defn pomodoro []
  [:div 
   [new-pomodoro]
   [running-pomodoro]
   [new-category]
   [new-task]
   [old-pomodoros]])
