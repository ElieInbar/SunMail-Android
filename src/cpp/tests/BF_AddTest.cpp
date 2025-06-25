#include <gtest/gtest.h>

#include <functional>
#include <string>
#include <vector>

#include "BloomFilter.h"
#include "Hash/StdHash.h"

TEST(BloomFilterAddUnitTest, SetsAtLeastOneBitOnFirstAdd) {
    std::vector<bool> array(128, false);
    std::unordered_set<std::string> blacklist;
    BloomFilter filter(array, {1, 2}, blacklist, new StdHash());
    std::string url = "first-url.com";

    // Check if all bits are initially false
    for (bool bit : filter.getBitArray()) {
        EXPECT_FALSE(bit);
    }

    filter.add(url);

    // Check if at least one bit is set to true
    bool anyBitSet = false;
    for (bool bit : filter.getBitArray()) {
        if (bit) {
            anyBitSet = true;
            break;
        }
    }

    EXPECT_TRUE(anyBitSet);
}

TEST(BloomFilterAddTest, SetsCorrectBitsForKnownInput) {
    // Simulate input: "8 2 3"
    std::vector<bool> array(8, false);
    std::unordered_set<std::string> blacklist;
    BloomFilter filter(array, {2, 3}, blacklist, new StdHash());
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

    int expectedIndex1 = computeIndex(url, 2, 8);
    int expectedIndex2 = computeIndex(url, 3, 8);

    // Check if the bits are false
    EXPECT_FALSE(filter.getBitArray()[expectedIndex1]);
    EXPECT_FALSE(filter.getBitArray()[expectedIndex2]);

    filter.add(url);

    std::vector<bool> updatedBits = filter.getBitArray();

    // Check if the bits are set correctly
    EXPECT_TRUE(updatedBits[expectedIndex1]);
    EXPECT_TRUE(updatedBits[expectedIndex2]);
}
