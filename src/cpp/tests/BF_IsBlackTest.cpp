#include <Hash/StdHash.h>
#include <gtest/gtest.h>

#include <functional>
#include <string>
#include <unordered_set>

#include "BloomFilter.h"

TEST(BloomFilterIsBlacklistedTest, ReturnsTrueWhenBitsSetAndUrlInBlacklist) {
    std::vector<bool> array(8, false);
    std::unordered_set<std::string> blacklist;
    BloomFilter filter(array, {2, 3}, blacklist, new StdHash());
    std::string url = "safe.com";

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

    std::vector<bool> manualBits(8, false);
    manualBits[index1] = true;
    manualBits[index2] = true;

    BloomFilter filter2(manualBits, filter.gethashRepeats(), blacklist,
                        filter.getHashFunction());

    // simulate add(url)
    filter2.add(url);

    EXPECT_TRUE(filter2.isBlacklisted(url));
}
