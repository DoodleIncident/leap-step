(ns leap-step.example.popular
  (:use [overtone.core]
        [overtone.synth.stringed]))

(defsynth dubstep [rootNote 34 bpm 120 wobble 8 note 34 snare-vol 0.7 kick-vol 0.7 hihat-vol 0.7 v 0.7 chord-vol 0 wob-vol 0 wob4-vol 0 out-bus 0]

  (defn wub [offset b-freq roomsize]
    (let [trig-i (impulse:kr (/ bpm 120))
          freq (midicps (+ note offset))
          swr (demand trig-i 0 (dseq [wobble] INF))
          sweep (lin-exp (lf-tri swr) -1 1 40 3000)
          wob (apply + (saw (* freq [0.99 1.01])))
          wob (lpf wob sweep)
          wob (* (+ (* wobble 0.02) 0.4) (normalizer wob))
          wob (+ wob (bpf wob b-freq 2))
          wob (+ wob (* 0.3 (g-verb wob roomsize 0.7 0.7)))]
      wob))
        
 (let [wob (wub 0 9000 8)
       ; 2,3,4,6,8,9, (10) 12,14,16
       crd1 (wub -5 2000 13)
       crd2 (wub 2 2000 13)
       crd3 (wub -24 2000 13)
       wob4 (wub 19 2000 13)
       wob5 (wub 12 2000 13)
       wob6 (wub 7 2000 13)
       kickenv (decay (t2a (demand (impulse:kr (/ bpm 30)) 0 (dseq [1 0 0 1 0 0 1 0 1 0 0 1 0 1 0 0] INF))) 0.7)
       kick (* (* kickenv 7) (sin-osc (+ 20 (* kickenv kickenv kickenv 150))))
       kick (clip2 kick 1)

       hihat (* 1 (pink-noise) (apply + (* (decay (impulse (/ bpm 60) 0.5) [0.4 1]) [1 0.05])))
       hihat (+ hihat (bpf (* 0.0001 hihat) 8000))
       hihat (clip2 hihat 1)

       hihatD (* 0.7 (pink-noise) (apply + (* (decay (impulse (/ bpm 30) 0.5) [0.4 1]) [1 0.05])))
       hihatD (+ hihatD (bpf (* 0.0001 hihatD) 8000))
       hihatD (clip2 hihatD 1)

       snare (* 3.5 (pink-noise) (apply + (* (decay (impulse (/ bpm 240) 0.5) [0.4 2]) [1 0.05])))
       snare (+ snare (bpf (* 4 snare) 2000))
       snare (clip2 snare 1)]


   (out out-bus    (* v (clip2 (+ (/ (+ (* wob-vol wob)
                                        (* chord-vol (+ crd1 crd2 crd3))
                                        (* wob4-vol wob4)
                                        (* (* wob4-vol 0.7) wob5)
                                        (* (* wob4-vol 0.8) wob6)) 
                                      (+ wob-vol (* 3 chord-vol) wob4-vol))
                                  (* kick-vol kick)
                                  (* snare-vol snare)
                                  (* hihat-vol hihat)
                                  (* 0.5 hihatD))
                               1.5)))))
   ;(out out-bus    (* v (clip2 (* hihat-vol hihat) 1)))))


(comment
  ;;Control the dubstep synth with the following:
  (def d (dubstep))
  (ctl d :wobble 8)
  (ctl d :note 40)
  (ctl d :bpm 250)
  (stop)
  )


(comment
  ;;For connecting with a monome to control the wobble and note
  (require '(polynome [core :as poly]))
  (def m (poly/init "/dev/tty.usbserial-m64-0790"))
  (def notes (reverse [25 27 28 35 40 41 50 78]))
  (poly/on-press m (fn [x y s]
                   (do
                     (let [wobble (inc y)
                           note (nth notes x)]
                       (println "wobble:" wobble)
                       (println "note:" note)
                       (poly/clear m)
                       (poly/led-on m x y)
                       (ctl dubstep :wobble wobble)
                       (ctl dubstep :note note)))))
  (poly/disconnect m))

(comment
  ;;For connecting with a monome to drive two separate dubstep bass synths
  (do
    (require '(polynome [core :as poly]))
    (def m (poly/init "/dev/tty.usbserial-m64-0790"))
    (def curr-vals (atom {:b1 [0 0]
                          :b2 [5 0]}))
    (def curr-vol-b1 (atom 1))
    (def curr-vol-b2 (atom 1))

    (at (+ 1000 (now))
        (def b1 (dubstep))
        (def b2 (dubstep)))

    (defn swap-vol
      [v]
      (mod (inc v) 2))

    (defn fetch-note
      [base idx]
      (+ base (nth-interval :minor-pentatonic idx)))

    (defn relight
      []
      (poly/clear m)
      (apply poly/led-on m (:b1 @curr-vals))
      (apply poly/led-on m (:b2 @curr-vals)))

    (defn low-bass
      [x y]
      (println "low" [x y])
      (if (= [x y]
             (:b1 @curr-vals))
        (ctl b1 :v (swap! curr-vol-b1 swap-vol))
        (do
          (ctl b1 :wobble (inc x) :note (fetch-note 20 y))
          (swap! curr-vals assoc :b1 [x y])))
      (relight))

    (defn hi-bass
      [x y]
      (println "hi" [x y])
      (if (= [x y]
             (:b2 @curr-vals))
        (ctl b2 :v (swap! curr-vol-b2 swap-vol))
        (do
          (ctl b2 :wobble (- x 3) :note (fetch-note 40 y))
          (swap! curr-vals assoc :b2 [x y])))
      (relight))

    (poly/on-press m (fn [x y s]
                       (if (< x 4)
                         (apply #'low-bass [x y])
                         (apply #'hi-bass [x y]))))

    (poly/on-press m (fn [x y s]
                       (poly/toggle-led m x y))))
)

(defn loop-seq [metro action things]
  (let [beat (metro)]
    (at (metro beat) (#(action (first things))))
    (apply-at (metro (inc beat)) loop-seq metro action (rest things) [])))
;(def metro (metronome 120))
;(def g (guitar))
;(loop-seq metro #(guitar-strum g %) (cycle [:C :G :Am :F]))

;;(stop)
