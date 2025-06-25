#include <gtest/gtest.h>

#include <string>
#include <vector>

#include "BloomFilter.h"
#include "Hash/StdHash.h"

TEST(BloomFilterHashFunctionTest, RepeatedHashFromVectorValues) {
    std::vector<bool> array(256, false);
    std::unordered_set<std::string> blacklist;
    BloomFilter filter(array, {2, 4}, blacklist,
                       new StdHash);  // simulate "256 2 4"
    std::string url = "www.example.com";

    int h1 = filter.repeatedHash(url, filter.gethashRepeats()[0]);  // 2
    int h2 = filter.repeatedHash(url, filter.gethashRepeats()[1]);  // 4

    EXPECT_GE(h1, 0);
    EXPECT_LT(h1, 256);
    EXPECT_GE(h2, 0);
    EXPECT_LT(h2, 256);

    // Consistance
    EXPECT_EQ(h1, filter.repeatedHash(url, 2));
    EXPECT_EQ(h2, filter.repeatedHash(url, 4));
}

TEST(BloomFilterHashFunctionTest, SameInputSameRepeatYieldsSameIndex) {
    std::vector<bool> array(128, false);
    std::unordered_set<std::string> blacklist;
    BloomFilter filter(array, {3, 3}, blacklist,
                       new StdHash);  // We just need bitArray size
    std::string url = "example.com";

    int h1 = filter.repeatedHash(url, 3);
    int h2 = filter.repeatedHash(url, 3);
    int h3 = filter.repeatedHash(url, 3);

    EXPECT_EQ(h1, h2);
    EXPECT_EQ(h2, h3);
}
