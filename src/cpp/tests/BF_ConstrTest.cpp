#include <gtest/gtest.h>
#include <string>
#include <unordered_set>
#include <vector>
#include "BloomFilter.h"
#include "Hash/StdHash.h"

TEST(BloomFilterConstructorTest, ConstructorDoesNotThrow) {
    std::vector<bool> bits(256, false);
    std::vector<int> repeats = {2, 3, 5};

    EXPECT_NO_THROW({
        std::vector<bool> array(256, false);
        std::unordered_set<std::string> blacklist;
        BloomFilter filter(array, {2, 3, 5}, blacklist, new StdHash());
    });
}

TEST(BloomFilterConstructorTest, ConstructorSetsBitArraySize) {
    std::vector<bool> array(128, false);
    std::unordered_set<std::string> blacklist;
    BloomFilter filter(array, {1, 2}, blacklist, new StdHash());
    EXPECT_EQ(filter.getBitArray().size(), 128);
}

TEST(BloomFilterConstructorTest, ConstructorStoresHashRepeats) {
    std::vector<int> repeats = {2, 3, 4};
    std::vector<bool> array(128, false);
    std::unordered_set<std::string> blacklist;
    BloomFilter filter(array, repeats, blacklist, new StdHash());
    EXPECT_EQ(filter.gethashRepeats().size(), 3);
    EXPECT_EQ(filter.gethashRepeats()[0], 2);
    EXPECT_EQ(filter.gethashRepeats()[1], 3);
    EXPECT_EQ(filter.gethashRepeats()[2], 4);
}

TEST(BloomFilterConstructorTest, BlacklistIsInitiallyEmpty) {
    std::vector<bool> array(64, false);
    std::unordered_set<std::string> blacklist;
    BloomFilter filter(array, {1}, blacklist, new StdHash());
    EXPECT_TRUE(filter.getBlacklist().empty());
}

TEST(BloomFilterConstructorTest, BitArrayIsInitiallyFalse) {
    std::vector<bool> array(64, false);
    std::unordered_set<std::string> blacklist;
    BloomFilter filter(array, {1}, blacklist, new StdHash());
    for (bool bit : filter.getBitArray()) {
        EXPECT_FALSE(bit);
    }
}
