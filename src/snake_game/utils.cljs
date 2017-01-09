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
    (update-in snake [:body] #(into [] (drop-last new-snake-body)))))

(defn snake-tail [coord-1 coord-2]
  (if (= coord-1 coord-2)
    coord-1
    (if (> coord-1 coord-2)
      (dec coord-2)
      (inc coord-2))))

(defn snake-growing [{:keys [body direction] :as snake}]
  (let [[[first-x first-y] [second-x second-y]] (take-last 2 body)
        x (snake-tail first-x second-x)
        y (snake-tail first-y second-y)]
    (update-in snake [:body] #(conj % [x y]))))

(defn process-move [{:keys [snake point board] :as db}]
  (let [snake-body (:body snake)]
    (if (= (first snake-body) point)
      (-> db
          (update-in [:snake] snake-growing)
          (assoc :point (random-free-position snake-body board)))
      db)))
