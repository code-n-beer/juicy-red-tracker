(defproject re-frame-pomofront "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.228"]
                 [cljs-ajax "0.5.4"]
                 [reagent "0.5.1"]
                 [re-frame "0.7.0"]
                 [secretary "1.2.3"]]

  :min-lein-version "2.5.3"

  :source-paths ["src/clj"]

  :plugins [[lein-cljsbuild "1.1.3"]]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"
                                    "test/js"]

  :figwheel {:server-ip "0.0.0.0"
             :css-dirs     ["resources/public/css"] }

  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src/cljs"]
     :figwheel     {:on-jsload "re-frame-pomofront.core/mount-root"
                    :websocket-host :js-client-host}
     :compiler     {:main                 re-frame-pomofront.core
                    :output-to            "resources/public/js/compiled/app.js"
                    :output-dir           "resources/public/js/compiled/out"
                    :asset-path           "js/compiled/out"
                    :source-map-timestamp true}}

    {:id           "min"
     :source-paths ["src/cljs"]
     :compiler     {:main            re-frame-pomofront.core
                    :output-to       "resources/public/js/compiled/app.js"
                    :optimizations   :advanced
                    :closure-defines {goog.DEBUG false}
                    :pretty-print    false}}
    {:id           "test"
     :source-paths ["src/cljs" "test/cljs"]
     :compiler     {:output-to     "resources/public/js/compiled/test.js"
                    :main          re-frame-pomofront.runner
                    :optimizations :none}}
    ]}

  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

  :profiles
  {:dev
   {:plugins [[lein-figwheel "0.5.3"]
              [lein-doo "0.1.6"]
              [cider/cider-nrepl "0.13.0-SNAPSHOT"]
              ]
    :dependencies [[figwheel-sidecar "0.5.3"]
                   [com.cemerick/piggieback "0.2.1"]]
    }})
