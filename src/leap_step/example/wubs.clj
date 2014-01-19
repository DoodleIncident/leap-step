(ns leap-step.example.wubs
  (:require [leap-step.core :as leap]
            [leap-step.hand :as hand]
            [overtone.live :as live]
            [leap-step.example.popular :as popular]
            [shadertone.tone :as t]))

;(t/start "shiny/implicit_fn.glsl"
;         :width 614 :height 720)
(def my-rgb (atom [0.8 0.8 0]))
(t/start-fullscreen "shiny/spectrograph.glsl"
         ;:width 1024 :height 512
         :textures [:overtone-audio :previous-frame]
         :user-data {"iRGB" my-rgb})
;(t/start "shiny/rgb.glsl"
;         :width 1024 :height 512
;         :user-data {"iRGB" my-rgb})


(defn process-frame [frame dubs]
  (do (cond
        (not (leap/hands? frame)) 
        (do (swap! my-rgb (fn [x] [0 0 1]))
            (live/ctl dubs :wob-vol 0)
            (live/ctl dubs :wob4-vol 0)
            (live/ctl dubs :chord-vol 0))
        (leap/single-hand? frame)
        (let [low-hand (leap/lowest-hand frame)
              pos (hand/palm-position low-hand)
              x (.getX pos)
              y (.getY pos)
              z (.getZ pos)]
          (println pos)
          (if (< z 150)
            (do (swap! my-rgb (fn [x] [1 0 0]))
                (condp > y
                  ; "No Zone"
                  100 (live/ctl dubs :wobble 1)
                  175 (live/ctl dubs :wobble 2)
                  250 (live/ctl dubs :wobble 4)
                  325 (live/ctl dubs :wobble 8)
                  400 (live/ctl dubs :wobble 12)
                  475 (live/ctl dubs :wobble 16)
                  "No Zone"))
            (do (swap! my-rgb (fn [x] [0 0 1]))
                (live/ctl dubs :wob-vol 0)
                (live/ctl dubs :wob4-vol 0)
                (live/ctl dubs :chord-vol 0)))

            (if-not (hand/fist? low-hand)
              (do (swap! my-rgb (fn [x] [1 0 0]))
                (condp > x
                  -300 "No Zone"
                  -150 (live/ctl dubs :note 34 :chord-vol 0 :wob4-vol 0 :wob-vol 1)
                  0 (live/ctl dubs :note 37 :chord-vol 0 :wob4-vol 0 :wob-vol 1)
                  150 (live/ctl dubs :note 39 :chord-vol 0 :wob4-vol 0 :wob-vol 1)
                  300 (live/ctl dubs :note 42 :chord-vol 0 :wob4-vol 0 :wob-vol 1)
                  "No Zone"))
              (do (swap! my-rgb (fn [x] [0 1 1]))
                (condp > x
                  -300 "No Zone"
                  -125 (live/ctl dubs :note 58 :chord-vol 1 :wob4-vol 0)
                  0 (live/ctl dubs :note 61 :chord-vol 1 :wob4-vol 0)
                  150 (live/ctl dubs :note 63 :chord-vol 1 :wob4-vol 0)
                  300 (live/ctl dubs :note 68 :chord-vol 1 :wob4-vol 0)
                  "No Zone"))))
          :else    
          (let [lefthand (leap/leftmost-hand frame)
                pos1 (hand/palm-position lefthand)
                x1 (.getX pos1)
                y1 (.getY pos1)
                z1 (.getZ pos1)
                righthand (leap/rightmost-hand frame)
                pos2 (hand/palm-position righthand)
                x2 (.getX pos2)
                y2 (.getY pos2)
                z2 (.getZ pos2)]
            (swap! my-rgb (fn [x] [0 1 0]))
            (when (and (< z1 150) (< z2 150))
              (println (- x1 x2))
              (condp > y1
                ; "No Zone"
                100 (live/ctl dubs :wobble 1)
                175 (live/ctl dubs :wobble 2)
                250 (live/ctl dubs :wobble 4)
                325 (live/ctl dubs :wobble 8)
                400 (live/ctl dubs :wobble 12)
                475 (live/ctl dubs :wobble 16)
                "No Zone")


              (condp > (- x1 x2)
                -700 "No Zone"
                -400 (live/ctl dubs :note 34 :chord-vol 0 :wob4-vol 1 :wob-vol 1.5)
                -300 (live/ctl dubs :note 37 :chord-vol 0 :wob4-vol 1 :wob-vol 1.5)
                -150 (live/ctl dubs :note 39 :chord-vol 0 :wob4-vol 1 :wob-vol 1.5)
                -80 (live/ctl dubs :note 42 :chord-vol 0 :wob4-vol 1 :wob-vol 1.5)
                "No Zone"))))))

  ;(cond
  ;(not (leap/hands? frame)) (do (live/ctl dubs :note 50 :wobble 1))
  ;(leap/single-hand? frame) (do (live/ctl dubs :note 32 :wobble 4))
  ;:else (do (live/ctl dubs :note 62 :wobble 8))))

  (defn -main [& args]
    (let [dubs (popular/dubstep 34 120 4)
          listener (leap/listener :frame #(process-frame (:frame %) dubs)
                                  :default #(println "Toggling" (:state %) "for listener:" (:listener %)))
          [controller _] (leap/controller listener)]
      ;    (read-line)
      ;    (swap! my-rgb (fn [x] [0 0 1]))
      ;    (read-line)
      ;    (swap! my-rgb (fn [x] [1 0 0]))
      ;    (read-line)
      ;    (swap! my-rgb (fn [x] [0 1 1]))
      (println "Press Enter to quit")
      (read-line)
      (leap/remove-listener! controller listener)))

