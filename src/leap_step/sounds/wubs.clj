(ns leap-step.sounds.wubs
  (:use [overtone.core]
        [overtone.synth.stringed]))

(defsynth dubstep [rootNote 34 bpm 120 wobble 8 note 34 snare-vol 1 kick-vol 1 hihat-vol 1 v 1 chord-vol 0 wob-vol 0 wob4-vol 0 out-bus 0]

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
  (stop))
