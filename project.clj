(defproject leap-step "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [overtone "0.9.1"]
                 [shadertone "0.2.2"]
                 [org.clojars.noodle-incident/leap-linux-native-deps "1.0.0"]
                 [rogerallen/leaplib "1.0.8"]]

  :plugins [[lein-git-deps "0.0.1-SNAPSHOT"]]
  :git-dependencies [["https://github.com/DoodleIncident/clojure-leap.git" "linux_sdk"]]
  :source-paths ["src" ; BAKA
                 ".lein-git-deps/clojure-leap/src"]

  :main leap-step.core)
