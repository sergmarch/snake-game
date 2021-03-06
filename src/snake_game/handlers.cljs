(ns snake-game.handlers
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require
    [re-frame.core :refer [register-handler register-sub subscribe dispatch]]
    [goog.events :as events]
    [snake-game.utils :as utils :refer [random-free-position update-snake-position process-move snake-on-board? snake-head-directions]]))

(def board [35 25])

(def snake {:direction [1 0]
            :body [[3 2] [2 2] [1 2] [0 2]]})

(def initial-state {:board board
                    :snake {:direction (:direction snake)
                            :body (:body snake)}
                    :point (random-free-position (:body snake) board)
                    :points 0
                    :game-running? true})

(defonce key-handler
  (events/listen js/window "keydown"
                 (fn [e]
                   (let [key-code (.-keyCode e)
                         current-head-dir (snake-head-directions key-code)
                         prev-head-dir (subscribe [:snake-head-direction])
                         filter-current-head-dir (mapv - @prev-head-dir)]
                     (when (and current-head-dir
                                (not= current-head-dir filter-current-head-dir)
                                (not= current-head-dir @prev-head-dir))
                       (dispatch [:update-snake-head-direction current-head-dir]))))))


; handlers
(register-handler
  :initialize
  (fn [db _]
    (merge db initial-state)))

(register-handler
  :update-snake-position
  (fn [db _]
    (if (:game-running? db)
      (-> db
          (update :snake update-snake-position)
          (as-> after-move
                (if (snake-on-board? (:snake after-move) (:board after-move))
                  (process-move after-move)
                  (assoc-in db [:game-running?] false))))
      db)))

(register-handler
  :update-snake-head-direction
  (fn [db [_ key-code]]
    (assoc-in db [:snake :direction] key-code)))

(register-handler
  :point
  (fn [db _]
    (update db :point random-free-position (get-in db [:snake :body]) board)))


; subscriptions
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

(register-sub
 :snake
 (fn [db _]
   (reaction (:snake @db))))

(register-sub
 :snake-head-direction
 (fn [db _]
   (reaction (:direction (:snake @db)))))
