#include <gtest/gtest.h>

#include "BloomFilter.h"
#include "GetCommand.h"
#include "Hash/StdHash.h"

TEST(GetCommandTest, ExecuteReturnsTrueIfUrlIsBlacklisted) {
    std::vector<int> hashConfig = {1, 2};
    std::vector<bool> array(16, false);
    std::unordered_set<std::string> blacklist;
    BloomFilter filter(array, hashConfig, blacklist, new StdHash());

    std::string url = "www.blacklist-me.com";
    filter.add(url);

    GetCommand command;
    std::string result = command.execute(filter, url);

    EXPECT_EQ(result, "200 Ok\n\ntrue true\n");
}

TEST(GetCommandTest, ExecuteReturnsFalseIfUrlNotBlacklisted) {
    std::vector<int> hashConfig = {1, 2};
    std::vector<bool> array(16, false);
    std::unordered_set<std::string> blacklist;
    BloomFilter filter(array, hashConfig, blacklist, new StdHash());

    std::string url = "www.clean-site.com";

    GetCommand command;
    std::string result = command.execute(filter, url);

    EXPECT_EQ(result, "200 Ok\n\nfalse\n");
}
