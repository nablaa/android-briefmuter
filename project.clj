(defproject briefmuter/briefmuter "0.0.3-SNAPSHOT"
  :description "App for muting the phone for a short period of time"
  :url "https://github.com/nablaa/android-briefmuter"
  :license {:name "GNU General Public License, version 2"
            :url "https://www.gnu.org/licenses/gpl-2.0.html"}

  :global-vars {*warn-on-reflection* true}

  :source-paths ["src/clojure" "src"]
  :java-source-paths ["src/java"]
  :javac-options ["-target" "1.6" "-source" "1.6" "-Xlint:-options"]
  :plugins [[lein-droid "0.4.0-alpha6"]]

  :dependencies [[org.clojure-android/clojure "1.7.0" :use-resources true]
                 [neko/neko "4.0.0-alpha2"]]
  :profiles {:default [:dev]

             :travis
             [:android-common :android-user
              {:dependencies [[org.clojure/tools.nrepl "0.2.10"]]
               :target-path "target/debug"
               :android {:aot :all-with-unused
                         :rename-manifest-package "org.briefmuter.debug"
                         :manifest-options {:app-name "BriefMuter (debug)"}
                         :sdk-path "/usr/local/android-sdk/"}}]

             :dev
             [:android-common :android-user
              {:dependencies [[org.clojure/tools.nrepl "0.2.10"]]
               :target-path "target/debug"
               :android {:aot :all-with-unused
                         :rename-manifest-package "org.briefmuter.debug"
                         :manifest-options {:app-name "BriefMuter (debug)"}}}]
             :release
             [:android-common
              {:target-path "target/release"
               :android
               {;; :keystore-path "/home/user/.android/private.keystore"
                ;; :key-alias "mykeyalias"
                ;; :sigalg "MD5withRSA"

                :ignore-log-priority [:debug :verbose]
                :aot :all
                :build-type :release}}]}

  :android {;; Specify the path to the Android SDK directory.
            ;; :sdk-path "/home/user/path/to/android-sdk/"

            ;; Try increasing this value if dexer fails with
            ;; OutOfMemoryException. Set the value according to your
            ;; available RAM.
            :dex-opts ["-JXmx4096M" "--incremental"]

            :target-version "19"
            :aot-exclude-ns ["clojure.parallel" "clojure.core.reducers"
                             "cider.nrepl" "cider-nrepl.plugin"
                             "cider.nrepl.middleware.util.java.parser"
                             #"cljs-tooling\..+"]})
