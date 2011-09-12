;; Clojure Number Guess
;; Matt DeBoard, 2011
;;
;; A re-write of "number guess" assignment in Clojure, a Lisp dialect that
;; uses the JVM runtime. The best way to read this is from the bottom up,
;; since the functions are necessarily ordered by their scope, with most-inner
;; scoped functions at the top, outermost scoped function at the bottom.
;;
;; The build system is Leiningen (https://github.com/technomancy/leiningen).
;; Lein compiles the Clojure source to a jarfile (which I have also included).
;; This can be run like normal, i.e. `java -jar numguess-1.0.0-standalone.jar`
;; for evaluation.
(ns numguess.core
  (:gen-class))

(def banner
  "Please think of a number between one and one hundred, and I will try to figure
out your number. You tell me if I'm too high, too low, or correct.\n")

;; To do: Map all these values into a single atom.
(def floor (atom 0))
(def ceiling (atom 100))
(def guess (atom 50))
(def counter (atom 1))

(defn update-vals [x y]
  "Function to update atom values. When dealing with a division equation that
resolves to a float, it actually outputs a rational number, so (/ 7 2) would
yield, literally, '7/2' in Clojure. So casting it to an int behaves like
Java would normally."
  (int
   (/ (+ x y) 2)
   ))

(defn too-high []
  "Core algorithm. (swap!) and (reset!) are functions used to change state for
atoms, which in turn are the synchronous & independent mutable objects used
in Clojure, which otherwise employs only immutable objects."
  (swap! counter + 1)
  (reset! ceiling @guess)
  (swap! guess update-vals @floor))

(defn too-low []
  "Core algorithm"
  (swap! counter + 1)
  (reset! floor @guess)
  (swap! guess update-vals @ceiling))

(defn answer-case [input]
  "Switch-case depending on user input."
  (case input
    "h" (too-high)
    "l" (too-low)
    "c" false))

(defn valid? [input]
  "Checks whether the user input is valid by checking for membership in a
predefined list of valid values."
  (some #{input} '("l" "h" "c")))

(defn prompt-read []
  "Reads input from the user and returns it."
  (println "\nWas I:")
  (println "(h)igh")
  (println "(l)ow")
  (println "(c)orrect")
  (print (format "Choose h, l or c: "))
  (flush)
  (read-line)
  )

(defn -main []
  "Main input loop. Prints the welcome banner once, then enters into the loop."
  (println banner)
  (loop []
    (printf "%d). My guess is: %d\n" @counter @guess)
    ;; If user input was a valid value, then let i equal that value. Otherwise,
    ;; recur.
    (if-let [i (valid? (prompt-read))]
      ;; If the user input was "c", then print the success message. Otherwise,
      ;; recur.
      (if-not (answer-case i)
        (do (printf "\nGot it, and it only took %d tries!\n" @counter) (flush))
        (recur))
      (recur))))
