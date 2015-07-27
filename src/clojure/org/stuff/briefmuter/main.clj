(ns org.stuff.briefmuter.main
  (:require [neko.activity :refer [defactivity set-content-view!]]
            [neko.context :refer [get-service]]
            [neko.debug :refer [*a]]
            [neko.notify :refer [toast construct-pending-intent fire notification cancel]]
            [neko.resource :as res]
            [neko.find-view :refer [find-view]]
            [neko.threading :refer [on-ui]]
            [neko.ui :refer [make-ui]]
            [neko.log :as log]))

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

(defn cancel-pending-intent []
  (log/d "Cancelling pending intent")
  (toast "Unmuting")
  (let [alarm-manager (get-service :alarm)
        audio-manager (get-service :audio)]
    (.setRingerMode audio-manager android.media.AudioManager/RINGER_MODE_NORMAL)
    (.cancel alarm-manager @pending-intent)
    (reset! pending-intent nil)
    (cancel :muting-triggered)))

(defn trigger-mute-interval [activity interval text]
  (cancel-pending-intent)
  (log/d "Mute triggered " {:interval interval :text text})
  (toast (str "Muting for " text))
  (let [alarm-manager (get-service :alarm)
        audio-manager (get-service :audio)
        pi (construct-pending-intent activity [:activity "org.stuff.briefmuter.UNMUTER"])
        calendar (java.util.Calendar/getInstance)
        date-format (java.text.DateFormat/getTimeInstance)]
    (.setRingerMode audio-manager android.media.AudioManager/RINGER_MODE_VIBRATE)
    (.set alarm-manager
          android.app.AlarmManager/ELAPSED_REALTIME_WAKEUP
          (+ interval (android.os.SystemClock/elapsedRealtime))
          pi)
    (reset! pending-intent pi)
    (.add calendar java.util.Calendar/MILLISECOND interval)
    (log/d "Unmuting will happen at" {:time (.toString calendar)})
    (let [unmute-time (.format date-format (.getTime calendar))
          unmute-time-str (str "Muted until " unmute-time)
          mynotification (notification {:icon R$drawable/ic_launcher
                                        :ticker-text "Briefly muted"
                                        :content-title unmute-time-str
                                        :content-text "Select to unmute now"
                                        :action [:activity "org.stuff.briefmuter.UNMUTER"]})]
      ; Set the notification persistent
      (set! (. mynotification flags) android.app.Notification/FLAG_ONGOING_EVENT)
      (log/d "mynotification" (.flags mynotification))
      (fire :muting-triggered mynotification))))

(defn main-layout [activity]
  [:linear-layout {:orientation :vertical
                   :layout-width :fill
                   :layout-height :fill}
   [:text-view {:text "Mute period:"
                :text-size [25 :dp]}]
   [:button {:text TEXT_5_MIN,
             :on-click (fn [_] (trigger-mute-interval activity INTERVAL_5_MIN TEXT_5_MIN))}]
   [:button {:text TEXT_15_MIN,
             :on-click (fn [_] (trigger-mute-interval activity INTERVAL_15_MIN TEXT_15_MIN))}]
   [:button {:text TEXT_30_MIN,
             :on-click (fn [_] (trigger-mute-interval activity INTERVAL_30_MIN TEXT_30_MIN))}]
   [:button {:text TEXT_45_MIN,
             :on-click (fn [_] (trigger-mute-interval activity INTERVAL_45_MIN TEXT_45_MIN))}]
   [:button {:text TEXT_1_HOUR,
             :on-click (fn [_] (trigger-mute-interval activity INTERVAL_1_HOUR TEXT_1_HOUR))}]])

(defactivity org.stuff.briefmuter.MainActivity
  :key :main
  (onCreate [this bundle]
    (.superOnCreate this bundle)
    (neko.debug/keep-screen-on this)
    (on-ui
     (set-content-view! (*a)
      (make-ui this (main-layout (*a)))))))
