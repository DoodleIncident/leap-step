(ns leap-step.test-leap
  (:require [clojure-leap.core :as leap]
            [clojure-leap.hand :as hand]
            [clojure-leap.gestures :as gestures]
            [overtone.live :as live]))
(comment ns leap-step.test-leap)

(defn noop-frame [frame])

(comment defn -main [& args]
  (do (noop-frame 0)
      (println (System/getProperty "java.library.path"))))

(defn -main [& args]
  (let [listener (leap/listener :frame #(noop-frame (:frame %))
                                :default #(println "toggling" (:state %) "for listener:" (:listener %)))
        [controller _] (leap/controller listener)]
    (println "Press ctrl-c to quit")
    (read-line)
    (leap/remove-listener! controller listener)))
