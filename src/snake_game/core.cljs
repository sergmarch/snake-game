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

(register-sub
  :snake
  (fn [db _]
    (reaction (:body (:snake @db)))))

(register-sub
  :point
  (fn [db _]
    (reaction (:point @db))))

(register-sub
  :points
  (fn [db _]
    (reaction (:points @db))))

(register-sub
  :game-running?
  (fn [db _]
    (reaction (:game-running? @db))))

(defn score []
  (let [points (subscribe [:points])]
   (fn []
     [:div.score (str "Score: " @points)])))

(defn game-over []
  (let [game-state (subscribe [:game-running?])]
   (fn []
     (if @game-state
       [:div]
       [:div.overlay
        [:div.play
         [:h1 "↺"]]]))))

(defn render-board []
  (let [board (subscribe [:board])
        snake (subscribe [:snake])
        point (subscribe [:point])]
   (fn []
     (let [[width height] @board
           snake-positions (into #{} @snake)
           current-point @point
           cells (for [y (range height)]
                  (into [:tr]
                    (for [x (range width)
                          :let [current-pos [x y]]]
                         (cond
                           (snake-positions current-pos) [:td.snake-on-cell]
                           (= current-pos current-point) [:td.point]
                           :else [:td.cell]))))]
          (into [:table.stage {:style {:height 377
                                       :width 527}}]
                cells)))))

(defn game []
  [:div
   [render-board]
   [score]
   [game-over]])

(defn run []
  (dispatch-sync [:initialize])
  (reagent/render [game]
    (.getElementById js/document "app")))

(run)
