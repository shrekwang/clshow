# clshow

clshow is a clojure library for showing class members infomation. clshow use
[paranamer](https://github.com/paul-hammant/paranamer "paranamer") to 
extract parameter name of methods, and print all members infomation in a nice format.

## Installation


```clojure

    ;Add the following dependency to your project.clj file:
    [clshow "1.0.1"]

    ;and in your clj code
    (ns my.ns
        (:require [clshow.core :refer :all]))

    ;or customize clshow in your ~/.lein/profiles.clj 
    :dependencies [[clshow "1.0.1"]]
    :repl-options { :init (do
                            (require 'clshow.core)
                            (clshow.core/set-jdk-doc-loc! (java.io.File. "/Users/admin/jdk-7u80-docs-all.zip"))) }

```


## Usage

```clojure


  ;if no local zip provided, clshow will use online javadoc location as default
  (set-jdk-doc-loc! (File. "/Users/admin/jdk-7u80-docs-all.zip"))

  (show java.io.File)
  (show (io/file "aa.txt"))

  (show String)
  (show "str-value")
  ; ======== fields ==========
  ; Comparator CASE_INSENSITIVE_ORDER
  ; 
  ; ======== methods ==========
  ; char         charAt(int index)                                                             
  ; int          codePointAt(int index)                                                        
  ; int          codePointBefore(int index)                                                    
  ; String       concat(String str)                                                            
  ; boolean      contentEquals(StringBuffer sb)                                                
  ; boolean      endsWith(String suffix)                                                       
  ; String       format(Locale arg0,String arg1,Object[] arg2)                                 
  ; String       format(String arg0,Object[] arg1)   
  ; .....

```

## License

Copyright © 2016 shrek wang

Distributed under the Eclipse Public License, the same as Clojure.
