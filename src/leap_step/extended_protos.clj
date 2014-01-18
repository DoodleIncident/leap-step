(ns leap-step.extended-protos
  (:require [leap-step.protocols :as l-protocols]
            [leap-step.frame :as l-frame]
            [leap-step.hand :as l-hand]
            [leap-step.finger :as l-finger]
            [leap-step.tool :as l-tool]
            [leap-step.pointable :as l-pointable]
            [leap-step.screen :as l-screen]
            [leap-step.vector :as l-vector])
  (:import (com.leapmotion.leap Frame
                                Hand HandList
                                Pointable PointableList
                                FingerList ToolList
                                Screen
                                Vector)))

;; All leap-step/protocols are extended in this file.
;; The use of protocols allows the consumer to choose between
;;  - performance (using the raw, type-hinted functions within the Namespaces)
;;  - ease (using the protocols)

(extend-type Frame
  l-protocols/EntityValidity
  (valid? [t]
    (l-frame/valid? t))

  l-protocols/FingerContainer
  (fingers? [t]
    (l-frame/fingers? t))
  (fingers [t]
    (l-frame/fingers t))
  (raw-finger [t finger-id]
    (l-frame/raw-finger t finger-id))
  (finger [t finger-id]
    (l-frame/finger t finger-id))
  (leftmost-finger [t]
    (l-frame/leftmost-finger t))
  (rightmost-finger [t]
    (l-frame/rightmost-finger t))
  (highest-finger [t]
    (l-frame/highest-finger t))
  (lowest-finger [t]
    (l-frame/lowest-finger t))

  l-protocols/ToolContainer
  (tools? [t]
    (l-frame/tools? t))
  (tools [t]
    (l-frame/tools t))
  (raw-tool [t tool-id]
    (l-frame/raw-tool t tool-id))
  (tool [t tool-id]
    (l-frame/tool t tool-id))
  (leftmost-tool [t]
    (l-frame/leftmost-tool t))
  (rightmost-tool [t]
    (l-frame/rightmost-tool t))
  (highest-tool [t]
    (l-frame/highest-tool t))
  (lowest-tool [t]
    (l-frame/lowest-tool t))

  l-protocols/PointableContainer
  (pointables? [t]
    (l-frame/pointables? t))
  (pointables [t]
    (l-frame/pointables t))
  (raw-pointable [t pointable-id]
    (l-frame/raw-pointable t pointable-id))
  (pointable [t pointable-id]
    (l-frame/pointable t pointable-id))
  (leftmost-pointable [t]
    (l-frame/leftmost-pointable t))
  (rightmost-pointable [t]
    (l-frame/rightmost-pointable t))
  (highest-pointable [t]
    (l-frame/highest-pointable t))
  (lowest-pointable [t]
    (l-frame/lowest-pointable t)))

(extend-type Hand
  l-protocols/EntityValidity
  (valid? [t]
    (l-hand/valid? t))

  l-protocols/FingerContainer
  (fingers? [t]
    (l-hand/fingers? t))
  (fingers [t]
    (l-hand/fingers t))
  (raw-finger [t finger-id]
    (l-hand/raw-finger t finger-id))
  (finger [t finger-id]
    (l-hand/finger t finger-id))
  (leftmost-finger [t]
    (l-hand/leftmost-finger t))
  (rightmost-finger [t]
    (l-hand/rightmost-finger t))
  (highest-finger [t]
    (l-hand/highest-finger t))
  (lowest-finger [t]
    (l-hand/lowest-finger t))

  l-protocols/ToolContainer
  (tools? [t]
    (l-hand/tools? t))
  (tools [t]
    (l-hand/tools t))
  (raw-tool [t tool-id]
    (l-hand/raw-tool t tool-id))
  (tool [t tool-id]
    (l-hand/tool t tool-id))
  (leftmost-tool [t]
    (l-hand/leftmost-tool t))
  (rightmost-tool [t]
    (l-hand/rightmost-tool t))
  (highest-tool [t]
    (l-hand/highest-tool t))
  (lowest-tool [t]
    (l-hand/lowest-tool t))
  
  l-protocols/PointableContainer
  (pointables? [t]
    (l-hand/pointables? t))
  (pointables [t]
    (l-hand/pointables t))
  (raw-pointable [t pointable-id]
    (l-hand/raw-pointable t pointable-id))
  (pointable [t pointable-id]
    (l-hand/pointable t pointable-id))
  (leftmost-pointable [t]
    (l-hand/leftmost-pointable t))
  (rightmost-pointable [t]
    (l-hand/rightmost-pointable t))
  (highest-pointable [t]
    (l-hand/highest-pointable t))
  (lowest-pointable [t]
    (l-hand/lowest-pointable t)))

(extend-type Pointable
  l-protocols/EntityValidity
  (valid? [t]
    (l-pointable/valid? t)))

(extend-type Screen
  l-protocols/EntityValidity
  (valid? [t]
    (l-screen/valid? t)))

(extend-type Vector
  l-protocols/EntityValidity
  (valid? [t]
    (l-vector/valid? t)))

(extend-type HandList
  l-protocols/LeapList
  (count [t] (l-hand/count t))
  (empty? [t] (l-hand/empty? t)))

(extend-type FingerList
  l-protocols/LeapList
  (count [t] (l-finger/count t))
  (empty? [t] (l-finger/empty? t)))

(extend-type ToolList
  l-protocols/LeapList
  (count [t] (l-tool/count t))
  (empty? [t] (l-tool/empty? t)))

