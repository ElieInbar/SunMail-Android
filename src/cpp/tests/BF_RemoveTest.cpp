#include <gtest/gtest.h> // NOLINT
#include <string>
#include <unordered_set>
#include <vector>

#include "BloomFilter.h"
#include "Hash/StdHash.h"

TEST(BloomFilterRemoveTest, RemoveUrlFromBlacklist) {
    std::vector<bool> bitArray(64, false);
    auto* hash = new StdHash();
    std::unordered_set<std::string> blacklist = {"http://remove-me.com"};

    BloomFilter filter(bitArray, {2, 3}, blacklist, hash);

    // Add the URL to the Bloom filter
    filter.add("http://remove-me.com");

    // Now it should be blacklisted
    EXPECT_TRUE(filter.isBlacklisted("http://remove-me.com"));

    filter.remove("http://remove-me.com");

    // Now it should no longer be blacklisted
    EXPECT_FALSE(filter.isBlacklisted("http://remove-me.com"));

    delete hash;
}

TEST(BloomFilterRemoveTest, RemoveUrlNotInBlacklistDoesNothing) {
    std::vector<bool> bitArray(64, false);
    auto* hash = new StdHash();
    std::unordered_set<std::string> blacklist = {"http://existing.com"};

    BloomFilter filter(bitArray, {2, 3}, blacklist, hash);

    filter.add("http://existing.com");

    filter.remove("http://not-in-list.com");

    EXPECT_TRUE(filter.isBlacklisted("http://existing.com"));
    EXPECT_FALSE(filter.isBlacklisted("http://not-in-list.com"));

    delete hash;
}
