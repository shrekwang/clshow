(ns clshow.core
  (:import [com.thoughtworks.paranamer BytecodeReadingParanamer CachingParanamer JavadocParanamer Paranamer PositionalParanamer AdaptiveParanamer]
           [java.lang.reflect Method  Field Constructor]
           java.net.URL
           java.io.File)
  (:require [airtake-tools.tools :as tools]
            [clojure.java.io  :as io]
            [clojure.string  :as s]
            [clojure.pprint :refer [pprint]]))

(defn- create-jdk-paranamer [jdk-doc-loc] 
  (CachingParanamer.  
    (AdaptiveParanamer.
      (into-array Paranamer 
                  [(JavadocParanamer. jdk-doc-loc)
                   (PositionalParanamer.)]))))


(defn- create-third-lib-paranamer []
  (CachingParanamer. 
    (AdaptiveParanamer.
      (into-array Paranamer 
                  [(BytecodeReadingParanamer. )
                   (PositionalParanamer.)]))))


(def jdk-paranamer (atom (create-jdk-paranamer (URL. "https://docs.oracle.com/javase/7/docs/api"))))

(def third-lib-paranamer (atom (create-third-lib-paranamer )))


(def ^:dynamic *paranamer* nil)

(defn set-jdk-doc-loc!
  "Sets the jdk doc location,loc can be File or URL object"
  [loc]
  (reset! jdk-paranamer (create-jdk-paranamer loc)))


(defn- get-params-desc [m]
  (s/join 
    "," 
    (map #(s/join " " %) 
         (partition 
           2 
           (interleave (map #(.getSimpleName %) (.getParameterTypes m))
                       (into [] (.lookupParameterNames (deref *paranamer*) m)))))))


(defn print-members [members]
  (let [ks (keys (first members))
        widths (map (fn [k]
                      (apply max (map #(count (str % )) (map k  members))))
                    ks) 
        fmts (map #(str "%-" % "s") widths)
        fmt-row (fn [row]
                  (str (apply str 
                              (interpose " "
                                         (for [[col fmt] (map vector (map #(get row %) ks) fmts)]
                                           (format fmt (str col)))))))]
    (doseq [x members]
      (println (fmt-row x)))))


(defprotocol MemberDesc
  (get-desc [member]))

(extend-protocol MemberDesc
  Field
  (get-desc [m]
    {:type (.getSimpleName (.getType m))
     :name  (.getName m)})) 


(extend-protocol MemberDesc
  Method
  (get-desc [m]
    {:rtntype (.getSimpleName (.getReturnType m))
     :name (str (.getName m) "(" (get-params-desc m) ")") }))

(extend-protocol MemberDesc
  Constructor
  (get-desc [m]
    {:name (str (.getSimpleName (.getDeclaringClass m)) "(" (get-params-desc m) ")") }))

(defn show 
  "print class member info"
  [x]
  (let [c (if (class? x) x (class x))]
    (let [paranamer (if (.startsWith (.getName c) "java.") 
                      jdk-paranamer 
                      third-lib-paranamer)]
      (binding [*paranamer* paranamer]
        (println "======== fields ==========")
        (print-members (sort-by :name 
                                (map get-desc (.getFields c))))
        (println "\n======== methods ==========")
        (print-members (sort-by :name 
                                (map get-desc (.getMethods c))))
        (println "\n======== constructors ==========")
        (print-members (map get-desc (.getConstructors c)))))))


(comment 

  (set-jdk-doc-loc! (File. "/Users/wangsn/jdk-7u80-docs-all.zip"))
  (show java.util.HashSet)

)
