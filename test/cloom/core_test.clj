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
