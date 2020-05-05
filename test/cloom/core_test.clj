(ns cloom.core-test
  (:require [clojure.test :refer :all]
            [cloom.core :refer :all]))

(deftest decify-test
  (testing "decify fail."
    (is (= (decify "00") 0))
    (is (= (decify "10") 16))
    (is (= (decify "ff") 255))
    ))

(deftest hexify-test
  (testing "hexify fail."
    (is (= (hexify 0) "00"))
    (is (= (hexify 16) "10"))
    (is (= (hexify 255) "ff"))
    ))

(deftest to-int32-test
  (testing "to-int32 fail."
    (is (= (to-int32 1) (list 1 0 0 0)))
    (is (= (to-int32 256) (list 0 1 0 0)))
    (is (= (to-int32 65536) (list 0 0 1 0)))
    (is (= (to-int32 65537) (list 1 0 1 0)))
    (is (= (to-int32 16777216) (list 0 0 0 1)))
    (is (= (to-int32 16777217) (list 1 0 0 1)))
    ))

(deftest from-int32-test
  (testing "from-int32 fail."
    (is (= (from-int32 (list 1 0 0 0)) 1))
    (is (= (from-int32 (list 0 1 0 0)) 256))
    (is (= (from-int32 (list 0 0 1 0)) 65536))
    (is (= (from-int32 (list 1 0 1 0)) 65537))
    (is (= (from-int32 (list 0 0 0 1)) 16777216))
    (is (= (from-int32 (list 1 0 0 1)) 16777217))
    ))

(deftest from-int16-test
  (testing "from-int16 fail."
    (is (= (from-int16 (list 1 0)) 1))
    (is (= (from-int16 (list 0 1)) 256))
    (is (= (from-int16 (list 255 255)) 65535))
    ))

(deftest to-int16-test
  (testing "to-int16 fail."
    (is (= (to-int16 1) (list 1 0)))
    (is (= (to-int16 256) (list 0 1)))
    (is (= (to-int16 65535) (list 255 255)))
    ))

(deftest hexstring-to-ints-test
  (testing "hexstring-to-ints fail."
    (is (= (hexstring-to-ints "0f1022") (list 15 16 34)))
    (is (= (hexstring-to-ints "49574144670b0000b822de000000001f") (list 73 87 65 68 103 11 0 0 184 34 222 0 0 0 0 31)))
    ))

(deftest read-header-test
  (testing "read-header fail."
    (is (= (read-header "49574144670b0000b822de000000001f")
           {:id "IWAD"
            :num-lumps 2919
            :info-table-offset 14557880}
           ))
    ))

(deftest read-directory-test
  (testing "read-directory fail."
    (is (= (count (read-directory (slurp "the_assault.wad.hex") (read-header (slurp "the_assault.wad.hex"))))
           51))
    ))

(deftest things-to-ints-test
  (testing "things-to-ints fail."
    (let [thing {:x-position 1234
                 :y-position 2345
                 :angle-facing 1
                 :thing-type 2035
                 :flags 7}
          ]
      (is (= (count (things-to-ints (list thing))) bytes-per-thing))
      (is (= thing (read-thing (things-to-ints (list thing)))))
      )))

(deftest linedefs-to-ints-test
  (testing "linedefs-to-ints fail."
    (let [linedef {:start-vertex 0
                   :end-vertex 1
                   :flags 4
                   :special-type 1
                   :sector-tag 4
                   :front-sidedef 1
                   :back-sidedef 2
                   }
          ]
      (is (= (count (linedefs-to-ints (list linedef))) bytes-per-linedef))
      (is (= linedef (read-linedef (linedefs-to-ints (list linedef)))))
      )))

(deftest sidedefs-to-ints-test
  (testing "sidedefs-to-ints fail."
    (let [sidedef {:x-offset 0
                   :y-offset 1
                   :upper-texture-name "TEXTURE1"
                   :lower-texture-name "TEXTURE2"
                   :middle-texture-name "TEXTURE3"
                   :sector-number 2
                   }
          ]
      (is (= (count (sidedefs-to-ints (list sidedef))) bytes-per-sidedef))
      (is (= sidedef (read-sidedef (sidedefs-to-ints (list sidedef)))))
      )))

(deftest vertexes-to-ints-test
  (testing "vertexes-to-ints fail."
    (let [vertex {:x-position 0
                  :y-position 1
                  }
          ]
      (is (= (count (vertexes-to-ints (list vertex))) bytes-per-vertex))
      (is (= vertex (read-vertex (vertexes-to-ints (list vertex)))))
      )))

(deftest sectors-to-ints-test
  (testing "sectors-to-ints fail."
    (let [sector {:floor-height 0
                  :ceiling-height 1
                  :floor-texture-name "TEXTURE1"
                  :ceiling-texture-name "TEXTURE2"
                  :light-level 3
                  :sector-type 5
                  :tag-number 5
                  }
          ]
      (is (= (count (sectors-to-ints (list sector))) bytes-per-sector))
      (is (= sector (read-sector (sectors-to-ints (list sector)))))
      )))

(deftest map-to-wad-hexstring-test
  (testing "map-to-wad-hexstring fail."
    (let [hs (slurp "the_assault.wad.hex")
          header (read-header hs)
          directory (read-directory hs header)
          lumps (map #(read-lump hs %) directory)
          things (nth lumps 3)
          linedefs (nth lumps 4)
          sidedefs (nth lumps 5)
          vertexes (nth lumps 6)
          sectors (nth lumps 10)
          out-hs (map-to-wad-hexstring "MAP01" things linedefs sidedefs vertexes sectors)
          ]
      (spit "test_copied.wad.hex" out-hs))
    ))

(deftest custom-map-test
  (testing "Custom map test fail."
    (let [things (list 
                   ; player start
                   {:x-position 16
                    :y-position 16
                    :angle-facing 0
                    :thing-type 1 
                    :flags 0x7
                    }
                   ; barrel
                   {:x-position 64
                    :y-position 64
                    :angle-facing 0
                    :thing-type 2035
                    :flags 0x7
                    })
          linedefs (list
                     ; SW-NW
                     {:start-vertex 0
                      :end-vertex 1
                      :flags 0x1
                      :special-type 0
                      :sector-tag 0
                      :front-sidedef 0
                      :back-sidedef 0xffff ;none
                      }
                     ; NW-NE
                     {:start-vertex 1
                      :end-vertex 2
                      :flags 0x1
                      :special-type 0
                      :sector-tag 0
                      :front-sidedef 1
                      :back-sidedef 0xffff ;none
                      }
                     ; NE-SE
                     {:start-vertex 2
                      :end-vertex 3
                      :flags 0x1
                      :special-type 0
                      :sector-tag 0
                      :front-sidedef 2
                      :back-sidedef 0xffff ;none
                      }
                     ; NE-SW
                     {:start-vertex 3
                      :end-vertex 0
                      :flags 0x1
                      :special-type 0
                      :sector-tag 0
                      :front-sidedef 3
                      :back-sidedef 0xffff ;none
                      })
          sidedefs (list
                     ; SW-NW
                     {:x-offset 0
                      :y-offset 0
                      :upper-texture-name ""
                      :lower-texture-name ""
                      :middle-texture-name "BRONZE1"
                      :sector-number 0
                      }
                     ; NW-NE
                     {:x-offset 0
                      :y-offset 0
                      :upper-texture-name ""
                      :lower-texture-name ""
                      :middle-texture-name "BRONZE2"
                      :sector-number 0
                      }
                     ; NE-SE
                     {:x-offset 0
                      :y-offset 0
                      :upper-texture-name ""
                      :lower-texture-name ""
                      :middle-texture-name "TEKGREN1"
                      :sector-number 0
                      }
                     ; SE-SW
                     {:x-offset 0
                      :y-offset 0
                      :upper-texture-name ""
                      :lower-texture-name ""
                      :middle-texture-name "TEKGREN2"
                      :sector-number 0
                      })
          vertexes (list
                     ; SW
                     {:x-position 0
                      :y-position 0}
                     ; NW
                     {:x-position 0 
                      :y-position 256}
                     ; NE
                     {:x-position 256
                      :y-position 256}
                     ; SE
                     {:x-position 256
                      :y-position 0}
                     )
          sectors (list
                    {:floor-height 0
                     :ceiling-height 64
                     :floor-texture-name "FLAT2"
                     :ceiling-texture-name "CEIL1_2"
                     :light-level 255
                     :sector-type 0
                     :tag-number 0
                     })
          hs (map-to-wad-hexstring "MAP01" things linedefs sidedefs vertexes sectors)
          ]
      (is (= nil (println (str "Thing-ints:\n" (into [] (things-to-ints things))))))
      (is (= nil (println (str "Linedef-ints:\n" (into [] (linedefs-to-ints linedefs))))))
      (is (= nil (println (str "Sidedef-ints:\n" (into [] (sidedefs-to-ints sidedefs))))))
      (is (= nil (println (str "Vertex-ints:\n" (into [] (vertexes-to-ints vertexes))))))
      (is (= nil (println (str "Sector-ints:\n" (into [] (sectors-to-ints sectors))))))
      (is (not= nil hs))
      (is (= nil (spit "test_room.wad.hex" hs)))
    )))

