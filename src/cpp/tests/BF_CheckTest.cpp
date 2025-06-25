#include <gtest/gtest.h>

#include <functional>
#include <string>
#include <vector>

#include "BloomFilter.h"
#include "Hash/StdHash.h"

TEST(BloomFilterContainTest, ReturnsTrueWhenAllRelevantBitsAreSet) {
    std::vector<bool> bits(8, false);
    std::vector<int> repeats = {2, 3};
    std::unordered_set<std::string> blacklist;
    BloomFilter filter(bits, repeats, blacklist, new StdHash());
    std::string url = "www.test.com";
    // Conpute the index manually
    auto computeIndex = [](const std::string& input, int repeatCount,
                           size_t size) {
        std::hash<std::string> hasher;
        std::string value = input;
        for (int i = 0; i < repeatCount; ++i) {
            value = std::to_string(hasher(value));
        }
        return std::stoull(value) % size;
    };

    int index1 = computeIndex(url, 2, 8);
    int index2 = computeIndex(url, 3, 8);

    // modif of bits
    auto modifiedBits = filter.getBitArray();
    modifiedBits[index1] = true;
    modifiedBits[index2] = true;

    // New filter with modified bits
    BloomFilter preparedFilter(modifiedBits, repeats, blacklist, new StdHash());

    EXPECT_TRUE(preparedFilter.check(url));
}

TEST(BloomFilterContainTest, ReturnsFalseWhenOneRelevantBitIsNotSet) {
    std::vector<bool> bits(8, false);
    std::vector<int> repeats = {2, 3};
    std::unordered_set<std::string> blacklist;

    BloomFilter filter(bits, repeats, blacklist, new StdHash());
    std::string url = "www.test.com";

    auto computeIndex = [](const std::string& input, int repeatCount,
                           size_t size) {
        std::hash<std::string> hasher;
        std::string value = input;
        for (int i = 0; i < repeatCount; ++i) {
            value = std::to_string(hasher(value));
        }
        return std::stoull(value) % size;
    };

    int index1 = computeIndex(url, 2, 8);
    int index2 = computeIndex(url, 3, 8);

    auto modifiedBits = filter.getBitArray();
    modifiedBits[index1] = true;
    modifiedBits[index2] = false;

    BloomFilter preparedFilter(modifiedBits, repeats, blacklist, new StdHash());

    EXPECT_FALSE(preparedFilter.check(url));
}
