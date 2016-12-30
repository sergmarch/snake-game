(ns snake-game.view
  (:require
    [re-frame.core :refer [subscribe]]
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

(defn app []
  [:div
   [render-board board]])
