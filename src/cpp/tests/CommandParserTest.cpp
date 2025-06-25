#include <gtest/gtest.h>
#include "CommandParser.h"

// Valid POST command
TEST(CommandParserTest, AcceptsValidPostCommand) {
    CommandParser parser;
    parser.parse("POST http://example.com");
    EXPECT_TRUE(parser.isValid());
    EXPECT_EQ(parser.getCommand(), "POST");
    EXPECT_EQ(parser.getURL(), "http://example.com");
}

// Unsupported HTTP method
TEST(CommandParserTest, RejectsUnsupportedCommand) {
    CommandParser parser;
    parser.parse("PUT http://unknown.com");
    EXPECT_FALSE(parser.isValid());
}

// Empty input string
TEST(CommandParserTest, RejectsEmptyInput) {
    CommandParser parser;
    parser.parse("");
    EXPECT_FALSE(parser.isValid());
}

// Only the command, no URL
TEST(CommandParserTest, RejectsCommandWithoutUrl) {
    CommandParser parser;
    parser.parse("DELETE");
    EXPECT_FALSE(parser.isValid());
}

// Only a URL, no command
TEST(CommandParserTest, RejectsUrlOnly) {
    CommandParser parser;
    parser.parse("http://just-url.com");
    EXPECT_FALSE(parser.isValid());
}
