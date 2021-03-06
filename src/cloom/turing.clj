(ns cloom.turing
  (:require [cloom.core :refer :all]))

(defn logic-node []
  "Build a custom map that demonstrates a single logic node."
  ; walk from the NW corner to the SE across a square room composed of two sectors, W and E
  ; if the E sector is raised, you'll slide to the south wall, passing triggers along the way
  ; if the E sector is not raised, you'll reach the SE corner, passing triggers along the way
  ; at the end of either path, teleport to next logic node (identical destination)
  (let [things (list
                 ; player start
                 {:x-position 16
                  :y-position 496
                  :angle-facing 315 ; we have to start at an angle, because you can only "slide" of NS/EW walls for some reason
                  :thing-type 1 
                  :flags 0x7
                  }
                 ; teleport destination (same as player start)
                 {:x-position 16
                  :y-position 496
                  :angle-facing 315 ; we have to start at an angle, because you can only "slide" of NS/EW walls for some reason
                  :thing-type 14
                  :flags 0x7
                  }
                 )
        linedefs (list
                   ; SW-NW wall
                   {:start-vertex 0
                    :end-vertex 1
                    :flags 0x1
                    :special-type 211 ; can switch platform height, repeatable toggle
                    :sector-tag 1
                    :front-sidedef 0
                    :back-sidedef 0xffff ;none
                    }
                   ; NW-N wall
                   {:start-vertex 1
                    :end-vertex 2
                    :flags 0x1
                    :special-type 0
                    :sector-tag 0
                    :front-sidedef 1
                    :back-sidedef 0xffff ;none
                    }
                   ; N-S divide
                   {:start-vertex 2
                    :end-vertex 3
                    :flags 0x4 ; two-sided
                    :special-type 0
                    :sector-tag 0
                    :front-sidedef 2
                    :back-sidedef 4 ; two-sided
                    }
                   ; S-SW wall
                   {:start-vertex 3
                    :end-vertex 0
                    :flags 0x1
                    :special-type 0
                    :sector-tag 0
                    :front-sidedef 3
                    :back-sidedef 0xffff ;none
                    }
                   ; N-NE wall
                   {:start-vertex 2
                    :end-vertex 4
                    :flags 0x1
                    :special-type 0
                    :sector-tag 0
                    :front-sidedef 5
                    :back-sidedef 0xffff ;none
                    }
                   ; NE-SE wall
                   {:start-vertex 4
                    :end-vertex 5
                    :flags 0x1
                    :special-type 0
                    :sector-tag 0
                    :front-sidedef 6
                    :back-sidedef 0xffff ;none
                    }
                   ; SE-S wall
                   {:start-vertex 5
                    :end-vertex 3
                    :flags 0x1
                    :special-type 0
                    :sector-tag 0
                    :front-sidedef 7 
                    :back-sidedef 0xffff ;none
                    }
                   ; teleport region (faces outward)
                   {:start-vertex 0
                    :end-vertex 5
                    :flags 0x0
                    :special-type 97; teleporter
                    :sector-tag 0
                    :front-sidedef 8
                    :back-sidedef 12
                    }
                   ; teleport region (faces outward)
                   {:start-vertex 5
                    :end-vertex 7
                    :flags 0x0
                    :special-type 97; teleporter
                    :sector-tag 0
                    :front-sidedef 9
                    :back-sidedef 13
                    }
                   ; W teleport region (faces outward)
                   {:start-vertex 7
                    :end-vertex 6
                    :flags 0x0
                    :special-type 97; teleporter
                    :sector-tag 0
                    :front-sidedef 10
                    :back-sidedef 14
                    }
                   ; W teleport region (faces outward)
                   {:start-vertex 6
                    :end-vertex 0
                    :flags 0x0
                    :special-type 97; teleporter
                    :sector-tag 0
                    :front-sidedef 11
                    :back-sidedef 15
                    }
                   )
        sidedefs (list
                   ; room SW-NW (0)
                   {:x-offset 0
                    :y-offset 0
                    :upper-texture-name "-"
                    :lower-texture-name "-"
                    :middle-texture-name "BRONZE1"
                    :sector-number 0
                    }
                   ; NW-N
                   {:x-offset 0
                    :y-offset 0
                    :upper-texture-name "-"
                    :lower-texture-name "-"
                    :middle-texture-name "BRONZE1"
                    :sector-number 0
                    }
                   ; N-S
                   {:x-offset 0
                    :y-offset 0
                    :upper-texture-name "BRONZE2"
                    :lower-texture-name "BRONZE1"
                    :middle-texture-name "-"
                   :sector-number 0
                    }
                   ; S-SW
                   {:x-offset 0
                    :y-offset 0
                    :upper-texture-name "-"
                    :lower-texture-name "-"
                    :middle-texture-name "TEKGREN2"
                    :sector-number 0
                    }
                   ; S-N
                   {:x-offset 0
                    :y-offset 0
                    :upper-texture-name "BRONZE2"
                    :lower-texture-name "BRONZE1"
                    :middle-texture-name "-"
                    :sector-number 1
                    }
                   ; N-NE
                   {:x-offset 0
                    :y-offset 0
                    :upper-texture-name "ASHWALL3"
                    :lower-texture-name "CEMENT9"
                    :middle-texture-name "BRONZE2"
                    :sector-number 1
                    }
                   ; NE-SE
                   {:x-offset 0
                    :y-offset 0
                    :upper-texture-name "ASHWALL3"
                    :lower-texture-name "CEMENT9"
                    :middle-texture-name "TEKGREN1"
                    :sector-number 1
                    }
                   ; SE-S
                   {:x-offset 0
                    :y-offset 0
                    :upper-texture-name "ASHWALL3"
                    :lower-texture-name "CEMENT9"
                    :middle-texture-name "BRONZE2"
                    :sector-number 1
                    }
                   ; teleport region w outward (8)
                   {:x-offset 0
                    :y-offset 0
                    :upper-texture-name "TEKGREN2"
                    :lower-texture-name "CEMENT9"
                    :middle-texture-name "-"
                    :sector-number 2
                    }
                   ; teleport region s outward
                   {:x-offset 0
                    :y-offset 0
                    :upper-texture-name "TEKGREN2"
                    :lower-texture-name "CEMENT9"
                    :middle-texture-name "-"
                    :sector-number 2
                    }
                   ; teleport region e outward
                   {:x-offset 0
                    :y-offset 0
                    :upper-texture-name "TEKGREN2"
                    :lower-texture-name "CEMENT9"
                    :middle-texture-name "-"
                    :sector-number 2
                    }
                   ; teleport region n outward
                   {:x-offset 0
                    :y-offset 0
                    :upper-texture-name "TEKGREN2"
                    :lower-texture-name "CEMENT9"
                    :middle-texture-name "-"
                    :sector-number 2
                    }
                   ; teleport region w inward (12)
                   {:x-offset 0
                    :y-offset 0
                    :upper-texture-name "TEKGREN2"
                    :lower-texture-name "CEMENT9"
                    :middle-texture-name "-"
                    :sector-number 2
                    }
                   ; teleport region s inward
                   {:x-offset 0
                    :y-offset 0
                    :upper-texture-name "TEKGREN2"
                    :lower-texture-name "CEMENT9"
                    :middle-texture-name "-"
                    :sector-number 2
                    }
                   ; teleport region e inward
                   {:x-offset 0
                    :y-offset 0
                    :upper-texture-name "TEKGREN2"
                    :lower-texture-name "CEMENT9"
                    :middle-texture-name "-"
                    :sector-number 2
                    }
                   ; teleport region n inward
                   {:x-offset 0
                    :y-offset 0
                    :upper-texture-name "TEKGREN2"
                    :lower-texture-name "CEMENT9"
                    :middle-texture-name "-"
                    :sector-number 2
                    }
                   )
        vertexes (list
                   ; SW of room (0)
                   {:x-position 0
                    :y-position 0}
                   ; NW of room (1)
                   {:x-position 0
                    :y-position 512}
                   ; N of room (2)
                   {:x-position 256
                    :y-position 512}
                   ; S of room (3)
                   {:x-position 256
                    :y-position 0}
                   ; NE of room (4)
                   {:x-position 512
                    :y-position 512}
                   ; SE of room (5)
                   {:x-position 512
                    :y-position 0}
                   ; NW teleport region (6)
                   {:x-position 0
                    :y-position 32}
                   ; NE teleport region (7)
                   {:x-position 512
                    :y-position 32}
                   )
        sectors (list
                  ; W
                  {:floor-height 0
                   :ceiling-height 256
                   :floor-texture-name "FLOOR0_1"
                   :ceiling-texture-name "CEIL1_2"
                   :light-level 255
                   :sector-type 0
                   :tag-number 0
                   }
                  ; E
                  {:floor-height 0 ; can be toggled to ceiling
                   :ceiling-height 256
                   :floor-texture-name "FLAT5"
                   :ceiling-texture-name "CEIL1_3"
                   :light-level 255
                   :sector-type 0
                   :tag-number 1
                   }
                  ; teleporter pad
                  {:floor-height 16
                   :ceiling-height 256
                   :floor-texture-name "GATE1"
                   :ceiling-texture-name "CEIL1_2"
                   :light-level 255
                   :sector-type 0
                   :tag-number 0
                   }
                  )
        hs (map-to-wad-hexstring "MAP01" things linedefs sidedefs vertexes sectors)
        ]
    (spit "test_room.wad.hex" hs)
  ))

