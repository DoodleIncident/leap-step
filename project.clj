(defproject leap_step "0.1.0-SNAPSHOT"
  :description "WUB WUB WUB WUB"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.0"]
                 [overtone "0.9.1"]
                 [shadertone "0.2.2"]]
  :resource-paths ["leap_lib/LeapJava.jar" "resources"]
  
;  :warn-on-reflection true
  
  :jvm-opts  [~(str "-Djava.library.path=leap_lib/:" (System/getenv "LD_LIBRARY_PATH"))]
  :main leap-step.example.wubs)

