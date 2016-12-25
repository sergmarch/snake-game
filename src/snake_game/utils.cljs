(ns snake-game.utils)

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
