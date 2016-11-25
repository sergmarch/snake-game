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

(defn run [] (dispatch-sync [:initialize]))

(run)
