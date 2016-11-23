(ns snake-game.core
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require
    [reagent.core :as reagent :refer [atom]]
    [re-frame.core :refer [register-handler register-sub subscribe dispatch dispatch-sync]]
    [goog.events :as events]))
