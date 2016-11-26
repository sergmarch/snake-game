(ns snake-game.core
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require
    [reagent.core :as reagent :refer [atom]]
    [re-frame.core :refer [register-handler register-sub subscribe dispatch dispatch-sync]]
    [goog.events :as events]))

(def board [35 25])

(def snake {:direction [1 0]
            :body [[3 2] [2 2] [1 2] [0 2]]})

(defn random-free-position [snake [x y]]
  (let [snake-positions-set (into #{} (:body snake))
        board-positions (for [x-pos (range x)
                              y-pos (range y)]
                             [x-pos y-pos])]
       (when-let [free-positions (seq (remove snake-positions-set board-positions))]
        (rand-nth free-positions))))

(def initial-state {:board board
                    :snake snake
                    :point (random-free-position snake board)
                    :points 0
                    :game-running? true})

(register-handler
  :initialize
  (fn [db _]
    (merge db initial-state)))

(register-sub
  :board
  (fn [db _]
    (reaction (:board @db))))

(defn render-board []
  (let [board (subscribe [:board])]
   (fn []
     (let [[width height] @board
           cells (for [y (range height)]
                  (into [:tr]
                    (for [x (range width)
                          :let [current-pos [x y]]]
                         [:td.cell])))]
          (into [:table.stage {:style {:height 377
                                       :width 527}}]
                cells)))))

(defn game []
  [:div [render-board]])

(defn run []
  (dispatch-sync [:initialize])
  (reagent/render [game]
    (.getElementById js/document "app")))

(run)
