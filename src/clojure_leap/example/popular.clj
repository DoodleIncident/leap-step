(ns clojure-leap.example.popular
  (:use [overtone.core]))

(defsynth dubstep [rootNote 34 bpm 120 wobble 8 note 34 snare-vol 1 kick-vol 1 hihat-vol 1 v 1 chord-vol 0 wob-vol 1 wob4-vol 0 out-bus 0]
 (let [trig (impulse:kr (/ bpm 120))
       freq (midicps note)

       cf1 (midicps (- note 5))
       cf2 (midicps (+ note 2))
       cf3 (midicps (- note 24))

       freq4 (midicps (+ note 7))
       freq5 (midicps (- note 24))

       swr (demand trig 0 (dseq [wobble] INF))
       sweep (lin-exp (lf-tri swr) -1 1 40 3000)
       wob (apply + (saw (* freq [0.99 1.01])))
       wob (lpf wob sweep)
       wob (* (+ (* wobble 0.02) 0.4) (normalizer wob))
       wob (+ wob (bpf wob 9000 2))
       wob (+ wob (* 0.3 (g-verb wob 8 0.7 0.7)))
       ; 2,3,4,6,8,9, (10) 12,14,16
       crd1 (apply + (saw (* cf1 [0.99 1.01])))
       crd1 (lpf crd1 sweep)
       crd1 (* (+ (* wobble 0.02) 0.4) (normalizer crd1))
       crd1 (+ crd1 (bpf crd1 2000 2))
       crd1 (+ crd1 (* 0.3 (g-verb crd1 13 0.7 0.7)))

       crd2 (apply + (saw (* cf2 [0.99 1.01])))
       crd2 (lpf crd2 sweep)
       crd2 (* (+ (* wobble 0.02) 0.4) (normalizer crd2))
       crd2 (+ crd2 (bpf crd2 2000 2))
       crd2 (+ crd2 (* 0.3 (g-verb crd2 13 0.7 0.7)))

       crd3 (apply + (saw (* cf3 [0.99 1.01])))
       crd3 (lpf crd3 sweep)
       crd3 (* (+ (* wobble 0.02) 0.4) (normalizer crd3))
       crd3 (+ crd3 (bpf crd3 2000 2))
       crd3 (+ crd3 (* 0.3 (g-verb crd3 13 0.7 0.7)))

       wob4 (apply + (saw (* freq4 [0.99 1.01])))
       wob4 (lpf wob4 sweep)
       wob4 (* (+ (* wobble 0.02) 0.4) (normalizer wob4))
       wob4 (+ wob4 (bpf wob4 2000 2))
       wob4 (+ wob4 (* 0.3 (g-verb wob4 13 0.7 0.7)))

       wob5 (apply + (saw (* freq5 [0.99 1.01])))
       wob5 (lpf wob5 sweep)
       wob5 (* (+ (* wobble 0.02) 0.4) (normalizer wob5))
       wob5 (+ wob5 (bpf wob5 2000 2))
       wob5 (+ wob5 (* 0.3 (g-verb wob5 13 0.7 0.7)))

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
                                        (* (* wob4-vol 0.7) wob5)) 
                                      (+ wob-vol (* 3 chord-vol) wob4-vol))
                                  (* kick-vol kick)
                                  (* snare-vol snare)
                                  (* hihat-vol hihat)
                                  (* 0.5 hihatD))
                               1)))))
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

;;(stop)
