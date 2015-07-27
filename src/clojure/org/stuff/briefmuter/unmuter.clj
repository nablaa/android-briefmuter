(ns org.stuff.briefmuter.unmuter
  (:require [neko.activity :refer [defactivity]]
            [neko.context :refer [get-service]]
            [neko.notify :refer [toast cancel]]
            [neko.threading :refer [on-ui]]
            [org.stuff.briefmuter.main :refer [pending-intent]]))

(defn unmute []
  (toast "Unmuting")
  (let [audio-manager (get-service :audio)
        alarm-manager (get-service :alarm)]
    (.setRingerMode audio-manager android.media.AudioManager/RINGER_MODE_NORMAL)
    (.cancel alarm-manager @pending-intent)
    (cancel :muting-triggered))
  (reset! pending-intent nil))

(defactivity org.stuff.briefmuter.UnmuteActivity
  (onCreate [this bundle]
    (.superOnCreate this bundle)
    (unmute)
    (on-ui
      (.finish this))))
