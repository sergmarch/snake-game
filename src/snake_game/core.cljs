(ns snake-game.core
  (:require
    [reagent.core :as reagent]
    [re-frame.core :refer [dispatch-sync]]
    [snake-game.view :refer [app]]))

(defn run []
  (dispatch-sync [:initialize])
  (reagent/render [app]
                  (.getElementById js/document "app")))

(run)
