(ns snake-game.utils
  (:require
    [re-frame.core :refer [subscribe]]))

(def snake-head-directions {38 [0 -1]   ; top
                            39 [1 0]    ; right
                            40 [0 1]    ; bottom
                            37 [-1 0]}) ; left

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
                           (cons body))]
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

(defn snake-on-board? [snake board]
  (let [snake-body (:body snake)
        snake-head (first snake-body)
        head-x-coord (first snake-head)
        head-y-coord (last snake-head)
        board-width (first board)
        board-height (last board)
        eats-himself? (contains? (into #{} (drop 1 snake-body)) snake-head)]
    (and (not eats-himself?)
         (empty? (filter neg? snake-head))
         (< head-x-coord board-width)
         (< head-y-coord board-height))))

(defn process-move [{:keys [snake point board] :as db}]
  (let [snake-body (:body snake)]
    (if (= (first snake-body) point)
      (-> db
          (update-in [:snake] snake-growing)
          (update-in [:points] inc)
          (assoc :point (random-free-position snake-body board)))
      db)))
