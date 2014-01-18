(ns leap-step.example.wubs
  (:require [leap-step.core :as leap]
            [leap-step.hand :as hand]
            [overtone.live :as live]
            [leap-step.example.popular :as popular]))

(defn process-frame [frame dubs]
  (println (if (leap/single-hand? frame)
             (let [low-hand (leap/lowest-hand frame)
                   [x y z] (hand/palm-position low-hand)]
               x)

             "false")))

  ;(cond
    ;(not (leap/hands? frame)) (do (live/ctl dubs :note 50 :wobble 1))
    ;(leap/single-hand? frame) (do (live/ctl dubs :note 32 :wobble 4))
    ;:else (do (live/ctl dubs :note 62 :wobble 8))))

(defn -main [& args]
  (let [dubs (popular/dubstep)
        listener (leap/listener :frame #(process-frame (:frame %) dubs)
                                :default #(println "Toggling" (:state %) "for listener:" (:listener %)))
        [controller _] (leap/controller listener)]
    (println "Press Enter to quit")
    (read-line)
    (leap/remove-listener! controller listener)))

