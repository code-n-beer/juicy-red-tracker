(ns re-frame-pomofront.views.pomodoro
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent :refer [atom]]
            [re-frame-pomofront.views.users-pomodoros :refer [your-stuff]]
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
(defn start-pomodoro [length task]
  (re-frame/dispatch [:start-pomodoro length task]))

(defn new-pomodoro []
  (let [length (atom 25)
        task-id (atom "")
        categories (re-frame/subscribe [:categories])
        selected-category (atom nil)
        selected-task (atom nil)]
    (fn []
      (let [tasks (:tasks (some #(if (= (% :id) @selected-category) %) @categories))
            task (some #(if (= (% :id) @selected-task) %) tasks)]
         [:div
          [:h3 "New pomodoro"]
          (if (empty? @categories)
           [:div [:strong "Create a category first"]]
           [:div "Length: " [text-input length] " minutes. "
           [:div 
            [:label "Category: "]
            [dropdown @categories selected-category]]
           (if (empty? tasks)
             [:strong "Create a task first"]
             [:div
              [:label "Task: "]
              [dropdown tasks selected-task]
              [:input {:type "button" :value "start" :on-click #(start-pomodoro @length task)}]])])]))))


;; ------------------------- currently running pomodoro 
(defn post-pomodoro [task-id length]
  (POST (str "/api/user/task/" task-id "/pomodoro") {:minutes length :success true} #(get-user) on-error)
  (re-frame/dispatch [:stop-pomodoro]))


(defn running-pomodoro [pomo]
  (let [task (@pomo :task)
        time-left (@pomo :time-left)
        length (@pomo :length)
        pomo-name (task :name)
        task-id (task :id)
        minutes (time-left :min)
        seconds (time-left :sec)
        sum (* minutes seconds)]
    [:div "running " [:strong pomo-name] "! " minutes "min " seconds "sec left" 
     [:div 
      [:input {:type "button"
               :value "finish"
               :on-click #(post-pomodoro task-id length)}]
      [:input {:type "button"
               :value "cancel"
               :on-click #(re-frame/dispatch [:stop-pomodoro])}]]]))

(defn pomodoro-component []
  (let [pomodoro (re-frame/subscribe [:running-pomodoro])]
    (fn []
      [:div
       [:h3 "Running pomodoro"]
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
       [:h3 "New category"]
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
       [:h3 "New task"]
       (if (empty? @categories)
         [:div [:strong "Create a category first"]]
         [:div
          [:div "Category " [dropdown @categories selected-category]]
          " Task name: "
          [text-input task-name]
          [:input {:type "button" :value "create" :on-click #(post-task @task-name @selected-category)}]])])))


(defn pomodoro []
  [:div 
   [new-pomodoro]
   [pomodoro-component] ;; y u not consistent ;___;
   [new-category]
   [new-task]
   [your-stuff]])
