(ns snake-game.view
  (:require
   [re-frame.core :refer [subscribe]]
   [snake-game.handlers :refer [board]]))

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
