(ns org.stuff.briefmuter.unmuter
  (:use [neko.activity :only [defactivity]]
        [neko.context :only [get-service]]
        [neko.threading :only [on-ui]]
        [neko.notify :only [toast]]))

(defn unmute []
  (toast "Unmuting")
  (let [audio-manager (get-service :audio)]
    (.setRingerMode audio-manager android.media.AudioManager/RINGER_MODE_NORMAL)))

(defactivity org.stuff.briefmuter.UnmuteActivity
  :def a
  :on-create
  (fn [this bundle]
    (unmute)
    (on-ui
      (.finish this))))
