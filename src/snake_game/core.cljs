(ns snake-game.core
  (:require
    [reagent.core :as reagent]
    [re-frame.core :refer [dispatch-sync dispatch]]
    [snake-game.view :refer [app]]))

(defonce trigger-snake-moving
         (js/setInterval #(dispatch [:update-snake-position]) 150))

(defn run []
  (dispatch-sync [:initialize])
  (reagent/render [app]
                  (.getElementById js/document "app")))

(run)
