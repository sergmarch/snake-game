(ns snake-game.view
  (:require
    [re-frame.core :refer [subscribe dispatch]]
    [snake-game.handlers :refer [board]]))

(defn render-board [[rows cols]]
  (let [board-width (range rows)
        board-height (range cols)
        snake (subscribe [:snake])
        point (subscribe [:point])
        snake-body (into #{} (:body @snake))
        table-styles {:height 377
                      :width 527}
        cells (for [row board-height]
                (into [:tr {:key row}]
                  (for [col board-width
                        :let [point-cell [col row]]]
                    (cond
                      (snake-body point-cell)
                      [:td.snake-on-cell]

                      (= point-cell @point)
                      [:td.point]

                      :else
                      [:td.cell]))))]
    (into [:table.stage {:style table-styles}]
          cells)))

(defn render-score []
  (let [points (subscribe [:points])]
    [:div.score (str "Score: " @points)]))

(defn game-over []
  (let [game-state (subscribe [:game-running?])]
    (if @game-state
      [:div]
      [:div.overlay
       [:div.play {:on-click #(dispatch [:initialize])}
        [:h1 "↺"]]])))

(defn app []
  [:div
   [game-over]
   [render-score]
   [render-board board]])
