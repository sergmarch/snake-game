(ns snake-game.utils
  (:require
    [re-frame.core :refer [subscribe]]))


(defn random-free-position [snake-body [rows cols]]
  (let [board-cells (into #{}
                          (for [row (range rows)
                                col (range cols)]
                            [row col]))
        snake-body-set (into #{} snake-body)
        board-free-space (into [] (clojure.set/difference board-cells snake-body-set))]
    (rand-nth board-free-space)))

(defn update-snake-position [{:keys [direction body] :as snake}]
  (let [new-snake-head-position (mapv + (first body) direction)
        new-snake-body (-> new-snake-head-position
                           (cons body))
        current-point (subscribe [:point])
        snake-head (first body)]
    (update-in snake [:body] #(into [] (if (= @current-point snake-head)
                                         new-snake-body
                                         (drop-last new-snake-body))))))

(defn snake-groving [body]
  (let [snake-body (subscribe [:snake])]))
