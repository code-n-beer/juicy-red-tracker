(ns re-frame-pomofront.views.pomodoro
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent :refer [atom]]
            [re-frame-pomofront.views.users-pomodoros :refer [old-pomodoros]]
            [re-frame-pomofront.views.dropdown-component :refer [dropdown]]
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

(defn get-tasks []
  (GET "/api/user/task" nil #(re-frame/dispatch [:update-tasks %]) on-error))

;; -------------------------- new pomodoros
(defn start-pomodoro [length task-id]
  (re-frame/dispatch [:start-pomodoro length task-id]))


(defn new-pomodoro []
  (let [length (atom 25)
        task-id (atom "")
        categories (re-frame/subscribe [:categories])
        selected-category (atom nil)
        selected-task (atom nil)]
    (fn []
      (let [category-by-id (fn [x] 
                             (if (= (x :id) @selected-category) x))
            tasks (:tasks (some category-by-id @categories))]
        [:div
         [:h4 "New pomodoro"]
         [:div "Length: " [text-input length] " minutes. "]
         (if (nil? @categories)
           [:strong "Create a category first"]
           [:div 
            [:label "Category: "]
            [dropdown @categories selected-category]])
         (if (empty? tasks)
           [:strong "Create a task first"]
           [:div 
            [:label "Task: "]
            [dropdown tasks selected-task]
            [:input {:type "button" :value "start" :on-click #(start-pomodoro @length @task-id)}]])]))))


;; ------------------------- currently running pomodoro 
(defn post-pomodoro [task-id length]
  (POST (str "/api/user/task/" task-id "/pomodoro") {:minutes length :success true} #(get-user) on-error)
  (re-frame/dispatch [:stop-pomodoro]))

(defn counter [timer-atom length started]
  (let [now (.getTime (js/Date.))
        length-num (js/parseInt length)
        length-milliseconds (* length-num 60 1000)
        result-milliseconds (- (+ length-milliseconds started) now)
        result-seconds-f (/ result-milliseconds 1000)
        result-seconds (js/Math.floor result-seconds-f)
        result-seconds-mod (mod result-seconds 60)
        result-minutes-f (/ result-seconds 60)
        result-minutes (js/Math.floor result-minutes-f)]
    (reset! timer-atom (str result-minutes "min " result-seconds-mod "sec left"))))

(defn running-pomodoro [pomo]
  (let [length (@pomo :length)
        started (@pomo :started)
        task-id (@pomo :task-id)
        timer (atom 0)]
    (fn []
      (js/setTimeout #(counter timer length started) 1000)
      [:div "running! " @timer
       [:div 
        [:input {:type "button"
                 :value "finish"
                 :on-click #(post-pomodoro task-id length)}]]])))

(defn pomodoro-component []
  (let [pomodoro (re-frame/subscribe [:running-pomodoro])]
    (fn []
      [:div
       [:h4 "Running pomodoro"]
       (if (some? @pomodoro)
         [running-pomodoro pomodoro]
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
        category-id (atom "")
        categories (re-frame/subscribe [:categories])
        selected-category (atom nil)]
    (fn []
      [:div
       [:h4 "New task"]
       [:div 
        "Category "
        (if (not-empty @categories)
          [dropdown @categories selected-category]
          [:strong "Create a category first"])
        " Task name: "
        [text-input task-name]]
       [:input {:type "button" :value "create" :on-click #(post-task @task-name @selected-category)}]])))


(defn pomodoro []
  [:div 
   [new-pomodoro]
   [pomodoro-component] ;; y u not consistent ;___;
   [new-category]
   [new-task]
   [old-pomodoros]])
