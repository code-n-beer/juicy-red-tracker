(ns pomofront.prod
  (:require [pomofront.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
