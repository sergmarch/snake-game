(ns snake-game.core
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require
    [reagent.core :as reagent :refer [atom]]
    [re-frame.core :refer [register-handler register-sub subscribe dispatch dispatch-sync]]
    [goog.events :as events]))

(def board [35 25])

(def snake {:direction [1 0]
            :body [[3 2] [2 2] [1 2] [0 2]]})

(def snake-head-directions {38 [0 -1]
                            39 [1 0]
                            40 [0 1]
                            37 [-1 0]})

(defn new-snake-head-direction [key-val]
  (snake-head-directions key-val))

(defn update-snake-position [{:keys [direction body] :as snake}]
  (let [new-snake-head-position (mapv + (first body) direction)
        new-snake-body (-> new-snake-head-position
                           (cons body))]
    (update-in snake [:body] #(into [] (drop-last new-snake-body)))))

(defn random-free-position [snake-body [rows cols]]
  (let [board-cells (into #{}
                          (for [row (range rows)
                                col (range cols)]
                            [row col]))
        snake-body-set (into #{} snake-body)
        board-free-space (into [] (clojure.set/difference board-cells snake-body-set))]
    (rand-nth board-free-space)))

(def initial-state {:board board
                    :snake {:direction (:direction snake)
                            :body (:body snake)}
                    :point (random-free-position (:body snake) board)
                    :points nil
                    :game-running? nil})



; handlers
(register-handler
 :initialize
 (fn [db _]
   (merge db initial-state)))

(register-handler
 :update-snake-position
 (fn [db _]
   (update db :snake update-snake-position)))

(register-handler
 :update-snake-head-direction
 (fn [db [_ key-code]]
   (assoc-in db [:snake :direction] key-code)))

; subscriptions
(register-sub
 :point
 (fn [db _]
   (reaction (:point @db))))

(register-sub
 :snake
 (fn [db _]
   (reaction (:snake @db))))

(defonce trigger-snake-moving
  (js/setInterval #(dispatch [:update-snake-position]) 200))

(defonce key-handler
  (events/listen js/window "keydown"
                 (fn [e]
                   (let [key-code (.-keyCode e)
                         current-head-dir (snake-head-directions key-code)]
                     (when current-head-dir
                       (dispatch [:update-snake-head-direction current-head-dir]))))))


(defn render-board [[rows cols]]
  (let [board-width (range rows)
        board-height (range cols)
        snake-body (subscribe [:snake])
        snake-body-set (into #{} (:body @snake-body))
        current-point (subscribe [:point])
        table-styles {:height 377
                      :width 527}
        cells (for [row board-height]
               [:tr
                (for [col board-width
                      :let [point-cell [col row]]]
                  (cond
                    (snake-body-set point-cell) [:td.snake-on-cell]
                    (= point-cell @current-point) [:td.point]
                    :else [:td.cell]))])]
    [:table.stage {:style table-styles}
     cells]))

(defn app []
  [:div
   [render-board board]])

(defn run []
  (dispatch-sync [:initialize])
  (reagent/render [app]
                  (.getElementById js/document "app")))

(run)
