(ns leap-step.example.wubs
  (:require [leap-step.core :as leap]
            [leap-step.hand :as hand]
            [overtone.live :as live]
            [leap-step.example.popular :as popular]
            [shadertone.tone :as t]))

(defn process-frame [frame dubs]
  (do      (cond
            (not (leap/hands? frame)) "false"
            (leap/single-hand? frame)
             (let [low-hand (leap/lowest-hand frame)
                   pos (hand/palm-position low-hand)
                   x (.getX pos)
                   y (.getY pos)
                   z (.getZ pos)]
               (println pos)
               (when (< z 150)
                (condp > y
                  ; "No Zone"
                  100 (live/ctl dubs :wobble 1)
                  175 (live/ctl dubs :wobble 2)
                  250 (live/ctl dubs :wobble 4)
                  325 (live/ctl dubs :wobble 8)
                  400 (live/ctl dubs :wobble 12)
                  475 (live/ctl dubs :wobble 16)
                  "No Zone")

                (if-not (hand/fist? low-hand)
                  (condp > x
                    -300 "No Zone"
                    -150 (live/ctl dubs :note 34 :chord-vol 0 :wob4-vol 0 :wob-vol 1)
                    0 (live/ctl dubs :note 37 :chord-vol 0 :wob4-vol 0 :wob-vol 1)
                    150 (live/ctl dubs :note 39 :chord-vol 0 :wob4-vol 0 :wob-vol 1)
                    300 (live/ctl dubs :note 42 :chord-vol 0 :wob4-vol 0 :wob-vol 1)
                    "No Zone")
                  (condp > x
                    -300 "No Zone"
                    -125 (live/ctl dubs :note 58 :chord-vol 1 :wob4-vol 0 :wob-vol 1)
                    0 (live/ctl dubs :note 61 :chord-vol 1 :wob4-vol 0 :wob-vol 1)
                    150 (live/ctl dubs :note 63 :chord-vol 1 :wob4-vol 0 :wob-vol 1)
                    300 (live/ctl dubs :note 68 :chord-vol 1 :wob4-vol 0 :wob-vol 1)
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
  (t/start "shiny/sine_dance.glsl"
           :width 614 :height 720
           :textures [:overtone-audio :previous-frame])
  (let [dubs (popular/dubstep 34 120 4)
        listener (leap/listener :frame #(process-frame (:frame %) dubs)
                                :default #(println "Toggling" (:state %) "for listener:" (:listener %)))
        [controller _] (leap/controller listener)]
    (println "Press Enter to quit")
    (read-line)
    (leap/remove-listener! controller listener)))

