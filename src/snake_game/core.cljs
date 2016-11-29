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

(defn move-snake [{:keys [direction body] :as snake}]
  (let [new-head-position (mapv + direction (first body))]
   (update-in snake [:body] #(into [] (drop-last (cons new-head-position body))))))

(defn change-snake-direction [[new-x new-y] [x y]]
  (if (or (= x new-x)
          (= y new-y))
      [x y]
      [new-x new-y]))

(def key-code->move
  {38 [0 -1]
   40 [0 1]
   39 [1 0]
   37 [-1 0]})

(def initial-state {:board board
                    :snake snake
                    :point (random-free-position snake board)
                    :points 0
                    :game-running? true})

(register-handler
  :initialize
  (fn [db _]
    (merge db initial-state)))

(register-handler
  :next-state
  (fn [db _]
    (if (:game-running? db)
     (update db :snake move-snake)
     db)))

(register-handler
  :change-direction
  (fn [db [_ new-direction]]
    (update-in db [:snake :direction]
      (partial change-snake-direction new-direction))))

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

(defonce key-handler
  (events/listen js/window "keydown"
   (fn [e]
     (let [key-code (.-keyCode e)]
      (when (contains? key-code->move key-code)
       (dispatch [:change-direction (key-code->move key-code)]))))))

(defonce snake-moving
  (js/setInterval #(dispatch [:next-state]) 150))

(defn run []
  (dispatch-sync [:initialize])
  (reagent/render [game]
    (.getElementById js/document "app")))

(run)
