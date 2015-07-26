(ns org.stuff.briefmuter.unmuter
  (:require [neko.activity :refer [defactivity]]
            [neko.context :refer [get-service]]
            [neko.notify :refer [toast]]
            [neko.threading :refer [on-ui]]))

(defn unmute []
  (toast "Unmuting")
  (let [audio-manager (get-service :audio)]
    (.setRingerMode audio-manager android.media.AudioManager/RINGER_MODE_NORMAL)))

(defactivity org.stuff.briefmuter.UnmuteActivity
  (onCreate [this bundle]
    (.superOnCreate this bundle)
    (unmute)
    (on-ui
      (.finish this))))
