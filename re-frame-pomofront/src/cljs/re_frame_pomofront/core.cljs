(ns re-frame-pomofront.core
    (:require [reagent.core :as reagent]
              [re-frame.core :as re-frame]
              [re-frame-pomofront.handlers]
              [re-frame-pomofront.subs]
              [re-frame-pomofront.routes :as routes]
              [re-frame-pomofront.views.root :as root]
              [re-frame-pomofront.config :as config]))

(when config/debug?
  (println "dev mode"))

(defn mount-root []
  (reagent/render [root/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init [] 
  (routes/app-routes)
  (re-frame/dispatch-sync [:initialize-db])
  (mount-root))
