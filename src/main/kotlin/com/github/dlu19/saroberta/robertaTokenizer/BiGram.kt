package com.github.dlu19.saroberta.robertaTokenizer


/**
 * A sequence of two adjacent elements from a string which differs by their position - left or right
 */
class BiGram(val left: String, val right: String) {

    companion object {
        private const val PAIR_SIZE = 2

        /**
         * Creates a new BiGram object from an array of Strings.
         * Expecting the array to be of size 2.
         * @param pairArray array of Strings
         * @return new BiGram where the String pairArray[0] will be left and pairArray[1] right.
         */
        fun of(pairArray: List<String>): BiGram {
            require(pairArray.size == PAIR_SIZE) { "Expecting given input pair to be exactly of size [$PAIR_SIZE], but it's of size: [${pairArray.size}]" }
            return BiGram(pairArray[0], pairArray[1])
        }

        /**
         * Creates an object with given parameters.
         * @param left will be the left String of the BiGram
         * @param right will be the right String of the BiGram
         * @return new BiGram object
         */
        fun of(left: String, right: String) = BiGram(left, right)
    }
}

