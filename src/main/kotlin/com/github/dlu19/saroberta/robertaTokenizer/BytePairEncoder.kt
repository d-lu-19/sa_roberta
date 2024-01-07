package com.github.dlu19.saroberta.robertaTokenizer

/**
 * Byte-Pair-Encoding
 * Relies on a pre-tokenizer that splits the training data into words, in our case space.
 *
 * This greedy algorithm looks for the best way to divide given input word.
 * It does that by dividing the word into characters, then assembles sub strings of the given word trying to find the best
 * partition of the word according to the ranks of the merges file.
 */
class BytePairEncoder {

    fun encode(word: String, robertaTokenizerRobertaResources: RobertaTokenizerResources): List<String> {
        var wordCharactersStrList = word.map { it.toString() }

        var biGramsSet = getBiGrams(wordCharactersStrList)

        while (true) {
            var minScore = Integer.MAX_VALUE.toLong()
            var lowestScoreBiGram: BiGram? = null

            for (biGram in biGramsSet) {
                val score = robertaTokenizerRobertaResources.getRankOrDefault(biGram, Integer.MAX_VALUE)

                // Note that we turn the most frequent bi-gram from a max problem to minimum
                // The lower the score the higher the frequency
                if (score < minScore) {
                    minScore = score.toLong()
                    lowestScoreBiGram = biGram
                }
            }

            // Reaching here means that only BiGrams that aren’t in the vocabulary (got rank Integer.MAX_VALUE) are left in
            // wordCharactersStrList, so no more merges should be done and the final tokenized word is the current wordCharactersStrList.
            if (lowestScoreBiGram == null) {
                break
            }

            val first = lowestScoreBiGram.left
            val second = lowestScoreBiGram.right
            val newWordList = mutableListOf<String>()
            var currIdx = 0

            while (currIdx < wordCharactersStrList.size) {
                val biGramStartIndex = getIndexWithStartPosition(wordCharactersStrList, first, currIdx)

                if (biGramStartIndex != -1) {
                    newWordList.addAll(wordCharactersStrList.subList(currIdx, biGramStartIndex))
                    currIdx = biGramStartIndex
                } else {
                    newWordList.addAll(wordCharactersStrList.subList(currIdx, wordCharactersStrList.size))
                    break
                }

                if (wordCharactersStrList[currIdx] == first && currIdx < wordCharactersStrList.size - 1 &&
                    wordCharactersStrList[currIdx + 1] == second) {
                    newWordList.add(first + second)
                    currIdx += 2
                } else {
                    newWordList.add(wordCharactersStrList[currIdx])
                    currIdx += 1
                }
            }

            wordCharactersStrList = newWordList
            if (wordCharactersStrList.size == 1) {
                break
            } else {
                biGramsSet = getBiGrams(wordCharactersStrList)
            }
        }

        return wordCharactersStrList
    }

    private fun getBiGrams(wordStrChars: List<String>): Set<BiGram> {
        return (0 until wordStrChars.size - 1)
            .map { i -> BiGram.of(wordStrChars[i], wordStrChars[i + 1]) }
            .toSet()
    }

    private fun getIndexWithStartPosition(wordCharsList: List<String>, word: String, startPosition: Int): Int {
        return (startPosition until wordCharsList.size)
            .firstOrNull { idx -> wordCharsList[idx] == word }
            ?: -1
    }
}
