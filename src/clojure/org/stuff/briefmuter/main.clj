(ns org.stuff.briefmuter.main
  (:use [neko.activity :only [defactivity set-content-view!]]
        [neko.context :only [get-service]]
        [neko.notify :only [toast construct-pending-intent]]
        [neko.threading :only [on-ui]]
        [neko.ui :only [make-ui]]))

(def ^:const INTERVAL_5_MIN (* 5 60 1000))
(def ^:const TEXT_5_MIN "5 min")
(def ^:const INTERVAL_15_MIN (* 15 60 1000))
(def ^:const TEXT_15_MIN "15 min")
(def ^:const INTERVAL_30_MIN (* 30 60 1000))
(def ^:const TEXT_30_MIN "30 min")
(def ^:const INTERVAL_1_HOUR (* 60 60 1000))
(def ^:const TEXT_1_HOUR "1 hour")

(defn trigger-mute-interval [interval text]
  (toast (str "Muting for " text))
  (let [alarm-manager (get-service :alarm)
        audio-manager (get-service :audio)
        pi (construct-pending-intent [:activity "org.stuff.briefmuter.UNMUTER"])]
    (.setRingerMode audio-manager android.media.AudioManager/RINGER_MODE_VIBRATE)
    (.set alarm-manager
          android.app.AlarmManager/ELAPSED_REALTIME_WAKEUP
          (+ interval (android.os.SystemClock/elapsedRealtime))
          pi)))

(def main-layout [:linear-layout {:orientation :vertical
                                  :layout-width :fill
                                  :layout-height :fill}
                  [:button {:text TEXT_5_MIN,
                            :on-click (fn [_] (trigger-mute-interval INTERVAL_5_MIN TEXT_5_MIN))}]
                  [:button {:text TEXT_15_MIN,
                            :on-click (fn [_] (trigger-mute-interval INTERVAL_15_MIN TEXT_15_MIN))}]
                  [:button {:text TEXT_30_MIN,
                            :on-click (fn [_] (trigger-mute-interval INTERVAL_30_MIN TEXT_30_MIN))}]
                  [:button {:text TEXT_1_HOUR,
                            :on-click (fn [_] (trigger-mute-interval INTERVAL_1_HOUR TEXT_1_HOUR))}]
                  ])

(defactivity org.stuff.briefmuter.MainActivity
  :def a
  :on-create
  (fn [this bundle]
    (on-ui
     (set-content-view! this
      (make-ui main-layout)))))
