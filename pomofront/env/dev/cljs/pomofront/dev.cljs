(ns ^:figwheel-no-load pomofront.dev
  (:require [pomofront.core :as core]
            [figwheel.client :as figwheel :include-macros true]))

(enable-console-print!)

(figwheel/watch-and-reload
  :jsload-callback core/mount-root)

(core/init!)
