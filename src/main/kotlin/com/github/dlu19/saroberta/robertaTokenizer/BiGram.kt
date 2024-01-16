package com.github.dlu19.saroberta.robertaTokenizer

/**
 * This file contains code based on RoBERTa Java Tokenizer
 * Source: https://github.com/purecloudlabs/roberta-tokenizer
 */

import com.google.common.base.Preconditions
import groovy.transform.EqualsAndHashCode
import org.jetbrains.annotations.NotNull

/**
 * A sequence of two adjacent elements from a string which differs by their position - left or right
 */
@EqualsAndHashCode
class BiGram {
    val left: String
    val right: String

    private constructor(@NotNull pairArray: List<String>) {
        Preconditions.checkState(
            pairArray.size == PAIR_SIZE,
            String.format(
                "Expecting given input pair to be exactly of size [%d], but it's of size: [%d]",
                PAIR_SIZE,
                pairArray.size
            )
        )
        this.left = pairArray[0]
        this.right = pairArray[1]
    }

    private constructor(@NotNull left: String, @NotNull right: String) {
        this.left = left
        this.right = right
    }

    companion object {
        private const val PAIR_SIZE = 2

        /**
         * Creates a new BiGram object from an array of Strings.
         * Expecting the array to be of size 2.
         * @param pairArray array of Strings
         * @return new BiGram where the String pairArray[0] will be left and pairArray[1] right.
         */
        fun of(@NotNull pairArray: List<String>): BiGram {
            return BiGram(pairArray)
        }

        /**
         * Creates an object with given parameters.
         * @param left will be the left String of the BiGRam
         * @param right will be the right String of the BiGRam
         * @return new BiGram object
         */
        fun of(@NotNull left: String, @NotNull right: String): BiGram {
            return BiGram(left, right)
        }
    }
}