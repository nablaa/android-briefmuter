(ns org.stuff.briefmuter.main
  (:require [neko.activity :refer [defactivity set-content-view!]]
            [neko.context :refer [get-service]]
            [neko.debug :refer [*a]]
            [neko.notify :refer [toast construct-pending-intent fire notification cancel]]
            [neko.resource :as res]
            [neko.find-view :refer [find-view]]
            [neko.threading :refer [on-ui]]
            [neko.ui :refer [make-ui]]))

;; We execute this function to import all subclasses of R class. This gives us
;; access to all application resources.
(res/import-all)

(def ^:const INTERVAL_5_MIN (* 5 60 1000))
(def ^:const TEXT_5_MIN "5 min")
(def ^:const INTERVAL_15_MIN (* 15 60 1000))
(def ^:const TEXT_15_MIN "15 min")
(def ^:const INTERVAL_30_MIN (* 30 60 1000))
(def ^:const TEXT_30_MIN "30 min")
(def ^:const INTERVAL_45_MIN (* 45 60 1000))
(def ^:const TEXT_45_MIN "45 min")
(def ^:const INTERVAL_1_HOUR (* 60 60 1000))
(def ^:const TEXT_1_HOUR "1 hour")

(def pending-intent (atom nil))

(defn trigger-mute-interval [activity interval text]
  (toast (str "Muting for " text))
  (let [alarm-manager (get-service :alarm)
        audio-manager (get-service :audio)
        pi (construct-pending-intent activity [:activity "org.stuff.briefmuter.UNMUTER"])]
    (.setRingerMode audio-manager android.media.AudioManager/RINGER_MODE_VIBRATE)
    (.set alarm-manager
          android.app.AlarmManager/ELAPSED_REALTIME_WAKEUP
          (+ interval (android.os.SystemClock/elapsedRealtime))
          pi)
    (reset! pending-intent pi)
    (fire :muting-triggered
          (notification {:icon R$drawable/ic_launcher
                        :ticker-text "Briefly muted"
                        :content-title "Muted until TODO" ; TODO show time here
                        :content-text "Select to unmute now"
                        :action [:activity "org.stuff.briefmuter.UNMUTER"]}))))

(defn cancel-pending-intent []
  (toast "Unmuting")
  (let [alarm-manager (get-service :alarm)
        audio-manager (get-service :audio)]
    (.setRingerMode audio-manager android.media.AudioManager/RINGER_MODE_NORMAL)
    (.cancel alarm-manager @pending-intent)
    (reset! pending-intent nil)
    (cancel :muting-triggered)))

(defn main-layout [activity]
  [:linear-layout {:orientation :vertical
                   :layout-width :fill
                   :layout-height :fill}
   [:button {:text TEXT_5_MIN,
             :on-click (fn [_] (trigger-mute-interval activity INTERVAL_5_MIN TEXT_5_MIN))}]
   [:button {:text TEXT_15_MIN,
             :on-click (fn [_] (trigger-mute-interval activity INTERVAL_15_MIN TEXT_15_MIN))}]
   [:button {:text TEXT_30_MIN,
             :on-click (fn [_] (trigger-mute-interval activity INTERVAL_30_MIN TEXT_30_MIN))}]
   [:button {:text TEXT_45_MIN,
             :on-click (fn [_] (trigger-mute-interval activity INTERVAL_45_MIN TEXT_45_MIN))}]
   [:button {:text TEXT_1_HOUR,
             :on-click (fn [_] (trigger-mute-interval activity INTERVAL_1_HOUR TEXT_1_HOUR))}]
   (if (not (nil? @pending-intent))
     [:button {:text "Unmute now",
               :on-click (fn [_] (cancel-pending-intent))}])
   ])

(defactivity org.stuff.briefmuter.MainActivity
  :key :main
  (onCreate [this bundle]
    (.superOnCreate this bundle)
    (neko.debug/keep-screen-on this)
    (on-ui
     (set-content-view! (*a)
      (make-ui this (main-layout (*a)))))))
