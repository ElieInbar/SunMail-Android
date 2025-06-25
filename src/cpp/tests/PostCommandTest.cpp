#include <gtest/gtest.h>

#include "PostCommand.h"
#include "BloomFilter.h"
#include "Hash/StdHash.h"

TEST(PostCommandTest, ExecuteAddsUrlToBloomFilter) {
    std::vector<int> hashConfig = {1, 2};
    std::vector<bool> array(16, false);
    std::unordered_set<std::string> blacklist;
    BloomFilter filter(array, hashConfig, blacklist, new StdHash());

    std::string testUrl = "www.test-add.com";
    PostCommand command;

    std::string result = command.execute(filter, testUrl);

    EXPECT_EQ(result, "201 Created\n");
    EXPECT_TRUE(filter.isBlacklisted(testUrl));
}
