(ns org.stuff.briefmuter.main
  (:use [neko.activity :only [defactivity set-content-view!]]
        [neko.context :only [get-service]]
        [neko.notify :only [toast construct-pending-intent]]
        [neko.threading :only [on-ui]]
        [neko.ui :only [make-ui]]))

(defn test-trigger-mute [_]
  (toast "Muting for 5 sec")
  (let [alarm-manager (get-service :alarm)
        audio-manager (get-service :audio)
        pi (construct-pending-intent [:activity "org.stuff.briefmuter.UNMUTER"])]
    (.setRingerMode audio-manager android.media.AudioManager/RINGER_MODE_VIBRATE)
    (.set alarm-manager
          android.app.AlarmManager/ELAPSED_REALTIME_WAKEUP
          (+ 5000 (android.os.SystemClock/elapsedRealtime))
          pi)))

(def main-layout [:linear-layout {:orientation :vertical
                                  :layout-width :fill
                                  :layout-height :fill}
                  [:button {:text "Mute for 5 sec",
                            :on-click test-trigger-mute}]])

(defactivity org.stuff.briefmuter.MainActivity
  :def a
  :on-create
  (fn [this bundle]
    (on-ui
     (set-content-view! a
      (make-ui main-layout)))))
